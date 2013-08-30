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
package org.dozer.eclipse.plugin.sourcepage.contentassist;

import org.dozer.eclipse.plugin.sourcepage.util.DozerPluginUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLContentAssistProcessor;
import org.springframework.ide.eclipse.beans.core.internal.model.BeansMapEntry;
import org.springframework.ide.eclipse.beans.core.internal.model.BeansTypedString;
import org.springframework.ide.eclipse.beans.core.model.IBean;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.BeansJavaCompletionProposal;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.DefaultContentAssistContext;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.DefaultContentAssistProposalRecorder;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.IContentAssistCalculator;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.IContentAssistContext;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.IContentAssistProposalRecorder;
import org.springframework.ide.eclipse.beans.ui.editor.contentassist.MethodContentAssistCalculator;
import org.springframework.ide.eclipse.beans.ui.editor.util.BeansEditorUtils;
import org.springframework.ide.eclipse.beans.ui.editor.util.BeansJavaCompletionUtils;
import org.springframework.ide.eclipse.beans.ui.model.BeansModelImages;
import org.springframework.ide.eclipse.core.java.FlagsMethodFilter;
import org.springframework.ide.eclipse.core.java.IMethodFilter;
import org.springframework.ide.eclipse.core.java.JdtUtils;
import org.springframework.ide.eclipse.core.model.IModelElement;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class DozerContentAssistProcessor extends XMLContentAssistProcessor {
	
	@Override
	@SuppressWarnings("restriction")
	protected ContentAssistRequest computeAttributeValueProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		if ("field".equals(node.getNodeName()) || "a".equals(node.getNodeName()) || "b".equals(node.getNodeName())) {
			NamedNodeMap attrs = nodeAtOffset.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				AttrImpl existingAttr = (AttrImpl) attrs.item(i);
				ITextRegion valueRegion = existingAttr.getValueRegion();
				String attrName = existingAttr.getName();
				if (completionRegion.getStart() >= valueRegion.getStart() && 
							completionRegion.getEnd() <= valueRegion.getEnd()) {
						
					//the first " in matchstring must be deleted for search
					if ("custom-converter".equals(attrName)) {
						return computeDozerClassContentProposals(documentPosition, matchString.substring(1), completionRegion, existingAttr, node);
					} else if ("custom-converter-id".equals(attrName)) {
						return computeDozerBeanContentProposals(documentPosition, matchString.substring(1), completionRegion, existingAttr, node);
					} else if  ("get-method".equals(attrName) || "set-method".equals(attrName) || "map-get-method".equals(attrName) || "map-set-method".equals(attrName)) {
						return computeDozerMethodContentProposals(documentPosition, matchString.substring(1), completionRegion, existingAttr, node, attrName.substring(0,3).equals("set"));
					}
				}
			}
		}
		
		return super.computeAttributeValueProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}
	
	@Override
	@SuppressWarnings("restriction")
	protected ContentAssistRequest computeEndTagOpenProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		Node firstChild = nodeAtOffset.getFirstChild();
		
		if ("class-a".equals(node.getNodeName()) || "class-b".equals(node.getNodeName()) || "a-hint".equals(node.getNodeName()) || "b-hint".equals(node.getNodeName()) || "a-deep-index-hint".equals(node.getNodeName()) || "b-deep-index-hint".equals(node.getNodeName())) {
			if (firstChild == null)
				firstChild = nodeAtOffset.appendChild(node.getOwnerDocument().createTextNode(""));
			if (firstChild.getNodeType() == Node.TEXT_NODE) {
				return computeDozerClassContentProposals(documentPosition, firstChild.getNodeValue(), completionRegion, (IDOMNode)firstChild, node);
			}
		} else if ("a".equals(node.getNodeName()) || "b".equals(node.getNodeName())) { 
			if (firstChild == null)
				firstChild = nodeAtOffset.appendChild(node.getOwnerDocument().createTextNode(""));
			if (firstChild.getNodeType() == Node.TEXT_NODE && 
					("field".equals(node.getParentNode().getNodeName()) || "field-exclude".equals(node.getParentNode().getNodeName()))) {
				return computeDozerPropertyContentProposals(documentPosition, firstChild.getNodeValue(), completionRegion, (IDOMNode)firstChild, node);
			}
		}
		
		return super.computeEndTagOpenProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}
	
	protected void convertProposals(List proposals) {
		//somehow the springide class BeansJavaCompletionUtils adds " to the replacement. Maybe this
		//is because of spring never uses TEXT_NODE's for class-declarations but only attributes, which needs "
		//Thus, we must use our own Proposal class. SpringIDE's BeansJavaCompletionUtils is very inflexible
		//here and cannot be subclassed. So we must replace every Proposal.		
		List<ICompletionProposal> newList = new ArrayList<ICompletionProposal>();
		for (Object object : proposals) {
			BeansJavaCompletionProposal bjcp = (BeansJavaCompletionProposal)object;
			DozerJavaCompletionProposal djcp = 
				new DozerJavaCompletionProposal(bjcp.getReplacementString(),
												bjcp.getReplacementOffset(),
												bjcp.getReplacementLength(),
												bjcp.getCursorPosition(),
												bjcp.getImage(),
												bjcp.getDisplayString(),
												bjcp.getContextInformation(),
												bjcp.getRelevance(),
												true,
												bjcp.getProposedObject());
			newList.add(djcp);
		}
		proposals.clear();
		proposals.addAll(newList);
	}
	
	@SuppressWarnings("restriction")
	protected ContentAssistRequest computeDozerClassContentProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		int offset = nodeAtOffset.getStartOffset();
		int len = nodeAtOffset.getLength();
		if (nodeAtOffset.getNodeType() == Node.ATTRIBUTE_NODE) {
			offset += nodeAtOffset.getNodeName().length()+2;
			len -= nodeAtOffset.getNodeName().length()+3;
		}
		
		
		ContentAssistRequest contentAssistRequest = new ContentAssistRequest(nodeAtOffset, node, getStructuredDocumentRegion(documentPosition), completionRegion, offset, len, matchString);
		IContentAssistProposalRecorder recorder = new DefaultContentAssistProposalRecorder(contentAssistRequest);
		IContentAssistContext context = new DefaultContentAssistContext(contentAssistRequest, "xyz", //@TODO
				matchString);		
		
		BeansJavaCompletionUtils.addClassValueProposals(context, recorder, BeansJavaCompletionUtils.FLAG_PACKAGE | BeansJavaCompletionUtils.FLAG_CLASS | BeansJavaCompletionUtils.FLAG_INTERFACE);
		convertProposals(contentAssistRequest.getProposals());
		
		return contentAssistRequest;		
	}
	
	
	@SuppressWarnings("restriction")
	protected ContentAssistRequest computeDozerPropertyContentProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		int offset = nodeAtOffset.getStartOffset();
		int len = nodeAtOffset.getLength();
		
		ContentAssistRequest contentAssistRequest = new ContentAssistRequest(nodeAtOffset, node, getStructuredDocumentRegion(documentPosition), completionRegion, offset, len, matchString);
		
		IFile file = BeansEditorUtils.getFile(contentAssistRequest);
		IProject project = file.getProject(); 
		String className = DozerPluginUtils.getMappingClassName(nodeAtOffset.getParentNode());
		IType type = JdtUtils.getJavaType(project, className);
		
		//by default immer this hinzu
		DozerJavaCompletionProposal newProposal = new DozerJavaCompletionProposal(
				"\" this",
				offset,
				len,
				2,
				JavaPluginImages.get(JavaPluginImages.IMG_OBJS_CLASS),
				"this",
				(IContextInformation)null,
				0,
				true,
				null);
		contentAssistRequest.addProposal(newProposal);
		
		DozerPluginUtils.addPropertyNameAttributeValueProposals(contentAssistRequest, matchString, "", type);		
		convertProposals(contentAssistRequest.getProposals());
		
		return contentAssistRequest;		
	}	
	
	@SuppressWarnings("restriction")
	protected ContentAssistRequest computeDozerMethodContentProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node, boolean onlySetter) {
		int offset = nodeAtOffset.getStartOffset()+nodeAtOffset.getNodeName().length()+1;
		int len = nodeAtOffset.getLength()-nodeAtOffset.getNodeName().length()-2;

		ContentAssistRequest contentAssistRequest = new ContentAssistRequest(nodeAtOffset, node, getStructuredDocumentRegion(documentPosition), completionRegion, offset, len, matchString);
		String targetClassName = DozerPluginUtils.getMappingClassName(node);
		addGetterSetterMethodProposals(contentAssistRequest, matchString, targetClassName, onlySetter);
				
		return contentAssistRequest;		
	}
	
	@SuppressWarnings("restriction")
	protected ContentAssistRequest computeDozerBeanContentProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		int offset = nodeAtOffset.getStartOffset()+nodeAtOffset.getNodeName().length()+2;
		int len = nodeAtOffset.getLength()-nodeAtOffset.getNodeName().length()-3;
		
		ContentAssistRequest contentAssistRequest = new ContentAssistRequest(nodeAtOffset, node, getStructuredDocumentRegion(documentPosition), completionRegion, offset, len, matchString);
		IFile file = BeansEditorUtils.getFile(this.fTextViewer.getDocument());
		IBean bean = DozerPluginUtils.getDozerBeanForMappingFile(file);
		IModelElement[] ccwiPropertyMapEntries = DozerPluginUtils.getPossibleCCIMappingForMappingFile(bean);
		
		if (ccwiPropertyMapEntries != null) {
			for (IModelElement beansMapEntry : ccwiPropertyMapEntries) {
				BeansMapEntry entry = (BeansMapEntry)beansMapEntry;
				BeansTypedString key = (BeansTypedString)entry.getKey();
	
				if (key.getString() != null && !key.getString().equals("")) {
					DozerJavaCompletionProposal newProposal = new DozerJavaCompletionProposal(
																	"\""+key.getString(),
																	offset,
																	len,
																	0,
																	BeansModelImages.getImage(entry),
																	key.getString(),
																	(IContextInformation)null,
																	0,
																	true,
																	bean);
					contentAssistRequest.addProposal(newProposal);
				}
			}
		}
		
		return contentAssistRequest;		
	}	
	
	private void addGetterSetterMethodProposals(ContentAssistRequest contentAssistRequest, String prefix, final String className, boolean onlySetter) {
		if (BeansEditorUtils.getFile(contentAssistRequest) instanceof IFile) {
			final IFile file = BeansEditorUtils.getFile(contentAssistRequest);

			IMethodFilter filter = null;
			if (onlySetter) {
				filter = new FlagsMethodFilter(FlagsMethodFilter.NOT_INTERFACE
						| FlagsMethodFilter.NOT_CONSTRUCTOR 
						| FlagsMethodFilter.PUBLIC,1);
				
			} else {
				filter = new FlagsMethodFilter(FlagsMethodFilter.NOT_VOID
						| FlagsMethodFilter.NOT_INTERFACE
						| FlagsMethodFilter.NOT_CONSTRUCTOR
						| FlagsMethodFilter.PUBLIC);
			}
	

			IContentAssistCalculator calculator = new MethodContentAssistCalculator(filter) {

				@Override
				protected IType calculateType(IContentAssistContext context) {
					return JdtUtils.getJavaType(file.getProject(), className);
				}
			};
			
			IContentAssistProposalRecorder recorder = new DefaultContentAssistProposalRecorder(contentAssistRequest);
			IContentAssistContext context = new DefaultContentAssistContext(contentAssistRequest, "xyz", //FIXME
					prefix);					
			
			calculator.computeProposals(context, recorder); //request, prefix, null, null, null);
		}
	}
	
	@Override
	@SuppressWarnings("restriction")
	protected ContentAssistRequest computeContentProposals(int documentPosition, String matchString, ITextRegion completionRegion, IDOMNode nodeAtOffset, IDOMNode node) {
		if ("class-a".equals(node.getNodeName()) || "class-b".equals(node.getNodeName())) { 
			if (nodeAtOffset.getNodeType() == Node.TEXT_NODE) {
				return computeDozerClassContentProposals(documentPosition, nodeAtOffset.getNodeValue(), completionRegion, nodeAtOffset, node);
			}
		} else if ("a".equals(node.getNodeName()) || "b".equals(node.getNodeName())) { 
			if (nodeAtOffset.getNodeType() == Node.TEXT_NODE && "field".equals(node.getParentNode().getNodeName())) {
				return computeDozerPropertyContentProposals(documentPosition, nodeAtOffset.getNodeValue(), completionRegion, nodeAtOffset, node);
			}
		}
		
		return super.computeContentProposals(documentPosition, matchString, completionRegion, nodeAtOffset, node);
	}
	
	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return DozerPluginUtils.DOZER_CONTENT_ASSIST_CHARS;
	}
	
}
