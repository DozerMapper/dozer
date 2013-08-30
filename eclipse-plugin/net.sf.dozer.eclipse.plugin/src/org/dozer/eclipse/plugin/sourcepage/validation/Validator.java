/**
 * Copyright 2005-2013 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dozer.eclipse.plugin.sourcepage.validation;

import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.dozer.eclipse.plugin.sourcepage.util.DozerPluginUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationInfo;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.springframework.ide.eclipse.core.java.JdtUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.net.URI;

@SuppressWarnings("restriction")
public class Validator extends AbstractNestedValidator {

	@Override
	public ValidationReport validate(String uri, InputStream inputstream,
			NestedValidatorContext context) {		

		ValidationInfo validationReport = new ValidationInfo(uri);
		try {
			//Get IFile from URI
			IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new URI(uri.replace(" ", "%20")));
			
			//hmm, could be multiple? Lets just take the first
			IFile file = files[0];
			
//			FIXME this is sick.
//			To get the line/column-numbers for the broken custom-converter properties, we need to
//			extend the DOMParser class to get the location information.
//			No idea whether this will work everywhere. I bet there is a a better solution somewhere...			
			LocationTrackingDOMParser parser = new LocationTrackingDOMParser();
			parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);	//must disable that feature to get location
			parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			parser.parse(new InputSource(uri));
			Document doc = parser.getDocument();	//Parse the Doc (every node gets UserData-Info for location info, see below)
			
			//DocumentType docType = doc.getDoctype();
			//String sysId = null;
			//if (docType != null)
			//	sysId = docType.getSystemId();
			
			//if (sysId != null && "http://dozer.sourceforge.net/dtd/dozerbeanmapping.dtd".equals(sysId)) {
			if ("mapping".equals(doc.getDocumentElement().getNodeName()))
				//classes exist?
				checkClassNodes(doc.getElementsByTagName("class-a"), file, validationReport);
				checkClassNodes(doc.getElementsByTagName("class-b"), file, validationReport);
				
				//field correct?
				checkFieldNodes(doc.getElementsByTagName("field"), file, validationReport);
				checkFieldNodes(doc.getElementsByTagName("field-exclude"), file, validationReport);
			//}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return validationReport;
	}
	
	@SuppressWarnings("restriction")
	private void checkClassNodes(NodeList nodeList, IFile file, ValidationInfo validationReport) throws JavaModelException, DOMException {
		int len = nodeList.getLength();
		for (int i = 0; i < len; i++) {
			Node node = nodeList.item(i);
			Node textNode = node.getFirstChild();
			
			if (textNode.getNodeType() == Node.TEXT_NODE) {
				String className = textNode.getNodeValue();
				
				IType javaType = JdtUtils.getJavaType(file.getProject(), className);
				//class not found
				if (javaType == null) {
					Integer[] location = (Integer[])node.getUserData("location");
					validationReport.addError("Class "+className+" not found.", location[0], location[1], validationReport.getFileURI());
				}
			}
			
		}
	}
	
	@SuppressWarnings("restriction")
	private void checkFieldNodes(NodeList nodeList, IFile file, ValidationInfo validationReport) throws JavaModelException, DOMException {
		int len = nodeList.getLength();
		//check all field-nodes...
		for (int i = 0; i < len; i++) {
			Node node = nodeList.item(i);	//field
			
			//..have custom-converter attributes
			Node attrNode = node.getAttributes().getNamedItem("custom-converter");
			if (attrNode != null) {
				//...doesnt implement the CustomConverter Interface
				if (!checkClassImplementsCustomConverter(file.getProject(), attrNode.getNodeValue())) {
					//this class doesnt implement the interface and is worth an error
					Integer[] location = (Integer[])node.getUserData("location");
					validationReport.addError("Class does not implement interface CustomConverter", location[0], location[1], validationReport.getFileURI());
				}
			//...have custom-converter-id attributes
			} else {
				attrNode = node.getAttributes().getNamedItem("custom-converter-id");
				if (attrNode != null) {
					String className = DozerPluginUtils.getClassNameForCCI(file, attrNode.getNodeValue());
					if (className != null && !checkClassImplementsCustomConverter(file.getProject(), className)) {
						//this class doesnt implement the interface and is worth an error
						Integer[] location = (Integer[])node.getUserData("location");
						validationReport.addError("Bean does not implement interface CustomConverter", location[0], location[1], validationReport.getFileURI());
					}
				}
			}
			
			NodeList abList = node.getChildNodes();
			int abLen = abList.getLength();
			for (int a = 0; a < abLen; a++) {
				Node abNode = abList.item(a);	//a or b
				if ("a".equals(abNode.getNodeName()) || "b".equals(abNode.getNodeName())) {
					Node textNode = abNode.getFirstChild();
					//...no value set between <a></a> or <b></b>?
					if(textNode == null){
						Integer[] location = (Integer[])node.getUserData("location");
						String className = DozerPluginUtils.getMappingClassName(abNode);
						String nodeName = abNode.getNodeName();						
						
						validationReport.addError("Unsetted property value in node "+nodeName+" for class " + className, location[0], location[1], validationReport.getFileURI());
					}
					//...is fieldname correct?
					else if (textNode.getNodeType() == Node.TEXT_NODE) {					
						String property = textNode.getNodeValue();
						String className = DozerPluginUtils.getMappingClassName(abNode);
						
						attrNode = DozerPluginUtils.getMappingNode(abNode).getAttributes().getNamedItem("type");
						boolean bIsBiDirectional = true;
						if (attrNode != null) {
							bIsBiDirectional = "bi-directional".equals(attrNode.getNodeValue());
						}
						
						if (!"this".equals(property)) {
							Node isAccessibleNode = abNode.getAttributes().getNamedItem("is-accessible");
							boolean isAccessible = isAccessibleNode != null && "true".equals(isAccessibleNode.getNodeValue());
							
							if ((bIsBiDirectional || "a".equals(abNode.getNodeName())) && 
									DozerPluginUtils.hasReadProperty(property, className, file.getProject(), isAccessible) == null) {
								Integer[] location = (Integer[])abNode.getUserData("location");
								validationReport.addError("Property "+property+" for class "+className+" cannot be read from.", location[0], location[1], validationReport.getFileURI());
							} else if ((bIsBiDirectional || "b".equals(abNode.getNodeName())) && 
									DozerPluginUtils.hasWriteProperty(property, className, file.getProject()) == null) {
								Integer[] location = (Integer[])abNode.getUserData("location");
								validationReport.addError("Property "+property+" for class "+className+" cannot be written to.", location[0], location[1], validationReport.getFileURI());
							}
						}
					}			
				}				
			}
		}
	}
	
	private boolean checkClassImplementsCustomConverter(IProject project, String className) throws JavaModelException {
		IType javaType = JdtUtils.getJavaType(project, className);
		
		if (javaType == null)
			return false;
		
		ITypeHierarchy hierarchy = javaType.newSupertypeHierarchy(new NullProgressMonitor());
		IType[] interfaces = hierarchy.getAllSuperInterfaces(javaType);
		
		for (IType type : interfaces) {
			if (type.getElementName().equals("CustomConverter")) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Borrowed and enhanced from Cocoon Woody DOMHelper Class
	 */
	public static class LocationTrackingDOMParser extends DOMParser {
        XMLLocator locator;

        @Override
		public void startDocument(XMLLocator xmlLocator,
				String arg1, org.apache.xerces.xni.NamespaceContext arg2,
				Augmentations arg3) throws org.apache.xerces.xni.XNIException {
			super.startDocument(xmlLocator, arg1, arg2, arg3);
            this.locator = xmlLocator;
            setLocation();
		}

		@Override
		public void startElement(QName qName, XMLAttributes xmlAttributes,
                Augmentations augmentations) throws XNIException {
            super.startElement(qName, xmlAttributes, augmentations);
            setLocation();
        }

        private final void setLocation() {
        	if (this.locator == null) {
                throw new RuntimeException(
                        "Error: locator is null. Check that you have the" +
                        " correct version of Xerces (such as the one that" +
                        " comes with Cocoon) in your endorsed library path.");
            }
            NodeImpl node = null;
            try {
                node = (NodeImpl)this.getProperty(
                        "http://apache.org/xml/properties/dom/current-element-node");
            } catch (org.xml.sax.SAXException ex) {
                System.err.println("except" + ex);
            }
            if (node != null) {
                Integer[] location = new Integer[] {locator.getLineNumber(), locator.getColumnNumber()};
                node.setUserData("location", location, (UserDataHandler)null);
            }
        }
	}

}
