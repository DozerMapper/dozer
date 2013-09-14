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
package org.dozer.eclipse.plugin.sourcepage.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.springframework.ide.eclipse.beans.core.BeansCorePlugin;
import org.springframework.ide.eclipse.beans.core.internal.model.Bean;
import org.springframework.ide.eclipse.beans.core.internal.model.BeanReference;
import org.springframework.ide.eclipse.beans.core.internal.model.BeansList;
import org.springframework.ide.eclipse.beans.core.internal.model.BeansMap;
import org.springframework.ide.eclipse.beans.core.internal.model.BeansMapEntry;
import org.springframework.ide.eclipse.beans.core.internal.model.BeansModelUtils;
import org.springframework.ide.eclipse.beans.core.internal.model.BeansTypedString;
import org.springframework.ide.eclipse.beans.core.model.IBean;
import org.springframework.ide.eclipse.beans.core.model.IBeanProperty;
import org.springframework.ide.eclipse.beans.core.model.IBeansConfig;
import org.springframework.ide.eclipse.beans.core.model.IBeansProject;
import org.springframework.ide.eclipse.beans.ui.BeansUIImages;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.BeansJavaCompletionProposal;
import org.springframework.ide.eclipse.beans.ui.editor.util.BeansEditorUtils;
import org.springframework.ide.eclipse.core.StringUtils;
import org.springframework.ide.eclipse.core.java.Introspector;
import org.springframework.ide.eclipse.core.java.JdtUtils;
import org.springframework.ide.eclipse.core.model.IModelElement;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DozerPluginUtils {

	public static char[] DOZER_CONTENT_ASSIST_CHARS = new char[]{'.'};
	
	public static String getMappingClassName(Node aOrBNode) {
		if (!"a".equals(aOrBNode.getNodeName()) && !"b".equals(aOrBNode.getNodeName()))
			return null;
		
		String aOrBNodeName = aOrBNode.getNodeName();		
		Node mappingNode = getMappingNode(aOrBNode);
		
		NodeList childNodes = mappingNode.getChildNodes();
		int len = childNodes.getLength();
		
		//get the class-a/class-b nodes, so we know what class to go into
		for (int i = 0; i < len; i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("class-"+aOrBNodeName)) {
				Text textNode = (Text)childNode.getFirstChild();
				return textNode.getNodeValue();				
			}
		}
		
		return null;
	}
	
	public static Node getMappingNode(Node someNode) {
		Node parentNode = someNode;
		while(!"mapping".equals((parentNode = parentNode.getParentNode()).getNodeName())) {
			//do nothing
		}
		if ("mapping".equals(parentNode.getNodeName()))
			return parentNode;
		else
			return null;
	}
	
	public static IBean getDozerBeanForMappingFile(IFile mappingIFile) {
		Set<IBean> beans = new HashSet<IBean>();
		IBean returnBean = null;
		
		//The mapping file that we are in
		File mappingFile = new File(mappingIFile.getName());
		String mappingFileName = mappingFile.getName();
		
		//First get direct references
		IBeansProject beansProject = BeansCorePlugin.getModel().getProject(mappingIFile.getProject());
		
		if (beansProject == null)
			return null;
		
		Set<IBeansConfig> tempConfigs = beansProject.getConfigs();
		
		if (tempConfigs == null)
			return null;		
		
		//We need some File to later get allBeans from ConfigSets
		IFile someBeansModelFile = null;
		
		//check local beans
		for (IBeansConfig config : tempConfigs) {
			if (someBeansModelFile == null)
				someBeansModelFile = (IFile)config.getElementResource();
			
			beans.addAll(config.getBeans());
		}
		returnBean = getDozerBeanFromBeans(beans, mappingFileName);				
		
		//no local bean for mappingfile found
		if (returnBean == null && someBeansModelFile != null) {
			//check configsets
			Set<IBean> allBeans = BeansEditorUtils.getBeansFromConfigSets(someBeansModelFile);
			returnBean = getDozerBeanFromBeans(allBeans, mappingFileName);
		}
		
		return returnBean;
	}
	
	protected static IBean getDozerBeanFromBeans(Set<IBean> beans, String mappingFileName) {
		//get first bean that uses our mapping file
		for (IBean bean : beans) {
			String className = BeansModelUtils.getBeanClass(bean, null);			
			if ("org.dozer.util.mapping.DozerBeanMapper".equals(className)) {
				//get mappingFiles-property
				IBeanProperty mappingFilesProperty = bean.getProperty("mappingFiles");
				if (mappingFilesProperty == null)
					continue;		
				
				BeansList mappingFilesPropertyValues = (BeansList)mappingFilesProperty.getValue();
				IModelElement[] modelElements = mappingFilesPropertyValues.getElementChildren();
	
				//find reference to our mapping file
				for (IModelElement modelElement : modelElements) {
					String configFilePath = ((BeansTypedString)modelElement).getString();
					File configFile = new File(configFilePath);
					
					//property with mapping file found, so this is our target bean
					if (mappingFileName.equals(configFile.getName())) {
						return bean;
					}
				}
			}
		}
		
		return null;
	}

	public static IModelElement[] getPossibleCCIMappingForMappingFile(IBean bean) {
		if (bean == null)
			return null;
		
		//get customConvertersWithId-property
		IBeanProperty ccwiProperty = bean.getProperty("customConvertersWithId");
		
		//no customConvertersWithId property, return at least the bean
		if (ccwiProperty == null)
			return null;
		
		BeansMap ccwiPropertyValues = (BeansMap)ccwiProperty.getValue();
		return ccwiPropertyValues.getElementChildren();
	}
	
	public static BeansMapEntry getCCIModelElementForMappingFile(IBean bean, String customConverterId) {
		if (bean == null || customConverterId == null || "".equals(customConverterId))
			return null;
		
		IModelElement[] ccwiPropertyMapEntries = getPossibleCCIMappingForMappingFile(bean);
		
		//map entries not found, at least return bean;
		if (ccwiPropertyMapEntries == null)
			return null;
		
		//find the custom-converter-id property
	    for (IModelElement beansMapEntry : ccwiPropertyMapEntries) {
	    	BeansMapEntry entry = (BeansMapEntry)beansMapEntry;
			BeansTypedString key = (BeansTypedString)entry.getKey();
			if (key.getString().equals(customConverterId)) {
				return entry;
			}
		}
	    
	    return null;
	}
	
	public static String getClassNameForCCI(IFile mappingIFile, String customConverterId) {		
		IBean bean = getDozerBeanForMappingFile(mappingIFile);
		
		if (bean == null)
			return null;
		
		BeansMapEntry beansMapEntry = getCCIModelElementForMappingFile(bean, customConverterId);

		if (beansMapEntry != null) {
			if (beansMapEntry.getValue() instanceof Bean) {
				Bean cciBean = (Bean)beansMapEntry.getValue();
				return cciBean.getBeanDefinition().getBeanClassName();
			} else if (beansMapEntry.getValue() instanceof BeanReference) {
				BeanReference beanRef = (BeanReference)beansMapEntry.getValue();				
				String beanName = beanRef.getBeanName();
				
				Set<IBeansProject> projects = BeansCorePlugin.getModel().getProjects();
				for (IBeansProject beansProject : projects) {
					Set<IBeansConfig> tempConfigs = beansProject.getConfigs();
					for (IBeansConfig config : tempConfigs) {
						IBean foundBean = config.getBean(beanName);
						if (foundBean != null) {
							return foundBean.getClassName();
						}
					}
				}
			}
		}
		
		return null;
	}	

	public static void addPropertyNameAttributeValueProposals(
			ContentAssistRequest request, String prefix, String oldPrefix,
			IType type) {
		
		// resolve type of indexed and nested property path
		if (prefix.lastIndexOf(".") >= 0) {
			int firstIndex = prefix.indexOf(".");
			String firstPrefix = prefix.substring(0, firstIndex);
			String lastPrefix = prefix.substring(firstIndex);
			if (".".equals(lastPrefix)) {
				lastPrefix = "";
			}
			else if (lastPrefix.startsWith(".")) {
				lastPrefix = lastPrefix.substring(1);
			}
			try {
				Collection<?> methods = Introspector.findReadableProperties(
						type, firstPrefix, true);
				if (methods != null && methods.size() == 1) {

					Iterator<?> iterator = methods.iterator();
					while (iterator.hasNext()) {
						IMethod method = (IMethod) iterator.next();
						IType returnType = JdtUtils
								.getJavaTypeForMethodReturnType(method, type);
						if (returnType != null) {
							String newPrefix = oldPrefix + firstPrefix + ".";

							addPropertyNameAttributeValueProposals(request,
									lastPrefix, newPrefix, returnType);
						}
						return;
					}
				}
			}
			catch (CoreException e) {
				// do nothing
			}
		}
		else {
			try {
				Collection<?> methods = Introspector.findWritableProperties(
						type, prefix, true);
				if (methods != null && methods.size() > 0) {
					Iterator<?> iterator = methods.iterator();
					while (iterator.hasNext()) {
						createMethodProposal(request,
								(IMethod) iterator.next(), oldPrefix);
					}
				}
			}
			catch (JavaModelException e1) {
				// do nothing
			}
		}
	}

	public static void createMethodProposal(ContentAssistRequest request,
			IMethod method, String prefix) {
		try {
			String[] parameterNames = method.getParameterNames();
			String[] parameterTypes = JdtUtils.getParameterTypesString(method);
			String propertyName = JdtUtils.getPropertyNameFromMethodName(method);

			String replaceText = prefix + propertyName;

			StringBuilder buf = new StringBuilder();
			buf.append(propertyName);
			buf.append(" - ");
			buf.append(method.getParent().getElementName());
			buf.append('.');
			buf.append(method.getElementName());
			buf.append('(');
			buf.append(parameterTypes[0]);
			buf.append(' ');
			buf.append(parameterNames[0]);
			buf.append(')');
			String displayText = buf.toString();

			Image image = BeansUIImages.getImage(BeansUIImages.IMG_OBJS_PROPERTY);

			BeansJavaCompletionProposal proposal = new BeansJavaCompletionProposal(
					replaceText, request.getReplacementBeginPosition(), request
							.getReplacementLength(), replaceText.length(),
							image, displayText, null, 0, method);
			request.addProposal(proposal);
		}
		catch (JavaModelException e) {
			// do nothing
		}
	}	
	
	public static IType hasReadProperty(String property, String className, IProject project, boolean noGetter) throws JavaModelException {
		IType javaType = JdtUtils.getJavaType(project, className);
		String checkProperty = property;
		
		String[] propertySplitted = checkProperty.split("\\.");
		//we only check the first level, if there is some x.y.z property
		if (propertySplitted.length > 1) {
			checkProperty = propertySplitted[0];
		}
		//if we are doing indexed property mapping, we remove the []
		checkProperty = checkProperty.replaceAll("\\[\\]", "");
		
		if (noGetter) {
			IField[] fields = javaType.getFields();
			for (IField field : fields) {
				if (field.getElementName().equals(property)) {
					return field.getDeclaringType();
				}
			}
			return null;
		}
		
		Collection<IMethod> methods = Introspector.findReadableProperties(javaType, checkProperty, true);		
		
		boolean retVal = methods.size() > 0;
		if (!retVal)
			return null;
		else {
			try {
				//className = JdtUtils.resolveClassName(className, javaType);
				if (!Class.forName(className).isPrimitive())
					className = null;					
			} catch (Throwable t) {
				//className = null;
			}
			
			Iterator<?> iterator = methods.iterator();
			while (iterator.hasNext()) {
				IMethod method = (IMethod) iterator.next();
				IType returnType = JdtUtils.getJavaTypeForMethodReturnType(method, javaType);
				
				if (className == null) {
					if (returnType != null)
						className = returnType.getFullyQualifiedName();
				}				
				if (className != null) {
					if (propertySplitted.length > 1) {			
						List<String> l = new ArrayList<String>(Arrays.asList(propertySplitted));
						l.remove(0);			
						property = StringUtils.collectionToDelimitedString(l, ".");
			
						return hasReadProperty(property, returnType.getFullyQualifiedName(), project, false);
					} else {
						return returnType;
					}
				}
			}
		}
		
		return null;
	}
	
	public static IType hasWriteProperty(String property, String className, IProject project) throws JavaModelException {
		IType javaType = JdtUtils.getJavaType(project, className);
		String checkProperty = property;
		
		String[] propertySplitted = checkProperty.split("\\.");
		if (propertySplitted.length > 1) {
			List<String> l = new ArrayList(Arrays.asList(propertySplitted));
			checkProperty = l.get(l.size()-1);
			l.remove(l.size()-1);			
			property = StringUtils.collectionToDelimitedString(l, ".");
			
			javaType = DozerPluginUtils.hasReadProperty(property, className, project, false);
			if (javaType == null)
				return null;			
		}
		//if we are doing indexed property mapping, we remove the []
		checkProperty = checkProperty.replaceAll("\\[\\]", "");
		
		Collection<?> methods = Introspector.findWritableProperties(javaType, checkProperty, true);
		
		boolean retVal = methods.size() > 0;
		if (!retVal)
			return null;
		else
			return javaType;
	}
	
}
