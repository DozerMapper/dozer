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
package org.dozer.eclipse.plugin.sourcepage.hyperlink;

import org.dozer.eclipse.plugin.sourcepage.util.DozerPluginUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.Logger;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.springframework.ide.eclipse.beans.core.internal.model.BeansMapEntry;
import org.springframework.ide.eclipse.beans.core.model.IBean;
import org.springframework.ide.eclipse.beans.ui.editor.hyperlink.JavaElementHyperlink;
import org.springframework.ide.eclipse.beans.ui.editor.util.BeansEditorUtils;
import org.springframework.ide.eclipse.core.StringUtils;
import org.springframework.ide.eclipse.core.java.Introspector;
import org.springframework.ide.eclipse.core.java.JdtUtils;
import org.springframework.ide.eclipse.core.model.ISourceModelElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import java.util.Set;

public class DozerClassHyperlinkDetector extends AbstractHyperlinkDetector {

	@SuppressWarnings("restriction")
	public final IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null || textViewer == null) {
			return null;
		}

		IDocument document = textViewer.getDocument();
		
		Node currentNode = BeansEditorUtils.getNodeByOffset(document, region.getOffset());
		if (currentNode != null) {
			switch (currentNode.getNodeType()) {
			case Node.ELEMENT_NODE:
				// at first try to handle selected attribute value
				Attr currentAttr = BeansEditorUtils.getAttrByOffset(
						currentNode, region.getOffset());
				IDOMAttr attr = (IDOMAttr) currentAttr;
				if (currentAttr != null
						&& region.getOffset() >= attr
								.getValueRegionStartOffset()) {
					if (isLinkableAttr(currentAttr)) {
						IRegion hyperlinkRegion = getHyperlinkRegion(currentAttr);
						IHyperlink hyperLink = createHyperlink(currentAttr
								.getName(), currentAttr.getNodeValue(),
								currentNode, currentNode.getParentNode(),
								document, textViewer, hyperlinkRegion, region);
						if (hyperLink != null) {
							return new IHyperlink[] { hyperLink };
						}
					}
				}
				break;

			case Node.TEXT_NODE:
				IRegion hyperlinkRegion = getHyperlinkRegion(currentNode);
				Node parentNode = currentNode.getParentNode();
				if (parentNode != null) {
					IHyperlink hyperLink = createHyperlink(parentNode
							.getNodeName(), currentNode.getNodeValue(),
							currentNode, parentNode, document, textViewer,
							hyperlinkRegion, region);
					if (hyperLink != null) {
						return new IHyperlink[] { hyperLink };
					}
				}
				break;
			}
		}
		return null;
	}

	public IHyperlink createHyperlink(String attrName, String target, Node node,
			Node parentNode, IDocument document, ITextViewer textViewer,
			IRegion hyperlinkRegion, IRegion cursor) {
		String nodeName = parentNode.getNodeName();
		IFile file = BeansEditorUtils.getFile(document);				
		IType type = JdtUtils.getJavaType(file.getProject(), target);
		
		if ("class-a".equals(nodeName) || "class-b".equals(nodeName) || "a-hint".equals(attrName) || "b-hint".equals(attrName) || "a-deep-index-hint".equals(attrName) || "b-deep-index-hint".equals(attrName)) {
			//make a simple hyperlink to the class
			if (type != null) {
				return new JavaElementHyperlink(hyperlinkRegion, type);
			}
		} else if ("set-method".equals(attrName) || "get-method".equals(attrName) || "map-set-method".equals(attrName) || "map-get-method".equals(attrName) ) {
			if ("field".equals(nodeName)) {
				String targetClassName = DozerPluginUtils.getMappingClassName(node);
				
				//get the first method that matches and make a hyperlink to that
				if (targetClassName != null) {
					IType targetClass = JdtUtils.getJavaType(file.getProject(), targetClassName);
					IMethod method = getMethodFromType(targetClass, target);
					if (method != null) {
						return new JavaElementHyperlink(hyperlinkRegion, method);
					}
				}
			}
		} else if ("a-hint".equals(attrName) || "b-hint".equals(attrName)) {
			//make a simple hyperlink to the class
			if (parentNode.getParentNode() != null && "mapping".equals(parentNode.getParentNode().getNodeName())) {
				if (type != null) {
					return new JavaElementHyperlink(hyperlinkRegion, type);
				}
			}		
		} else if ("custom-converter".equals(attrName)) {
			//make a simple hyperlink to the class
			if (type != null) {
				return new JavaElementHyperlink(hyperlinkRegion, type);
			}
		} else if ("custom-converter-id".equals(attrName)) {
			//search spring-bean that uses our mapping file and return the mapping
			//for the customconverter-id map-property
			ISourceModelElement modelElement = getModelElementForCustomConverter(BeansEditorUtils.getFile(document), target);
					
			if (modelElement != null) {
				return new SourceModelHyperlink(modelElement, hyperlinkRegion);
			}
		} else if ("a".equals(nodeName) || "b".equals(nodeName) ) {
			String targetClassName = DozerPluginUtils.getMappingClassName(node.getParentNode());
			String property = node.getNodeValue();
			
			if (targetClassName != null) {
				IType targetClass = JdtUtils.getJavaType(file.getProject(), targetClassName);
				
				try {
					//if we are doing indexed property mapping, we remove the []
					property = property.replaceAll("\\[\\]", "");
					
					IMethod method = Introspector.getReadableProperty(targetClass, property);
					
					if (method != null)
						return new JavaElementHyperlink(hyperlinkRegion, method);					
				} catch (Throwable e) {
					Logger.logException(e);
				}				
			}
		} else if ("bean-factory".equals(attrName)) {
			//make a simple hyperlink to the class
			if (type != null) {
				return new JavaElementHyperlink(hyperlinkRegion, type);
			}
		}
		
		return null;
	}
	
	/**
	 * Get First Method by name
	 * 
	 * @param type
	 * @param methodName
	 * @return
	 */
	private IMethod getMethodFromType(IType type, String methodName) {
		try {
			Set<IMethod> methods = Introspector.getAllMethods(type);
			for (IMethod method : methods) {
				if (method.getElementName().equals(methodName)) {
					return method;
				}
			}
		} catch (Exception e) {}
		
		return null;
	}
	
	private ISourceModelElement getModelElementForCustomConverter(IFile file, String customConverterId) {
		IBean bean = DozerPluginUtils.getDozerBeanForMappingFile(file);
		BeansMapEntry modelElement = DozerPluginUtils.getCCIModelElementForMappingFile(bean, customConverterId);

		if (modelElement != null)
			return modelElement;

	    //exact property not found, at least return bean
		return bean;
	}	

	public boolean isLinkableAttr(Attr attr) {
		String attrName = attr.getName();
		String ownerName = attr.getOwnerElement().getNodeName();
		String parentName = attr.getOwnerElement().getParentNode().getNodeName();
		
		if ("bean-factory".equals(attrName))
			return true;		
		else if ("field".equals(ownerName)) {
			if ("custom-converter".equals(attrName) || "custom-converter-id".equals(attrName))
				return true;
		} else if ("field".equals(parentName)) {
			if ("set-method".equals(attrName) || "get-method".equals(attrName) || "map-set-method".equals(attrName) || "map-get-method".equals(attrName))
				return true;
		}

			
		return false;
	}	
	
	@SuppressWarnings("restriction")
	protected final IRegion getHyperlinkRegion(Node node) {
		if (node != null) {
			switch (node.getNodeType()) {
			case Node.DOCUMENT_TYPE_NODE:
			case Node.TEXT_NODE:
				IDOMNode docNode = (IDOMNode) node;
				return new Region(docNode.getStartOffset(), docNode
						.getEndOffset()
						- docNode.getStartOffset());

			case Node.ELEMENT_NODE:
				IDOMElement element = (IDOMElement) node;
				int endOffset;
				if (element.hasEndTag() && element.isClosed()) {
					endOffset = element.getStartEndOffset();
				}
				else {
					endOffset = element.getEndOffset();
				}
				return new Region(element.getStartOffset(), endOffset
						- element.getStartOffset());

			case Node.ATTRIBUTE_NODE:
				IDOMAttr att = (IDOMAttr) node;
				// do not include quotes in attribute value region
				int regOffset = att.getValueRegionStartOffset();
				int regLength = att.getValueRegionText().length();
				String attValue = att.getValueRegionText();
				if (StringUtils.isQuoted(attValue)) {
					regOffset += 1;
					regLength = regLength - 2;
				}
				return new Region(regOffset, regLength);
			}
		}
		return null;
	}
	
}
