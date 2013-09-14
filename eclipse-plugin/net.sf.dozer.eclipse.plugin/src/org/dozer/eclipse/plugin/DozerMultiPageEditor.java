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
package org.dozer.eclipse.plugin;

import org.dozer.eclipse.plugin.editorpage.DozerFormEditor;
import org.dozer.eclipse.plugin.editorpage.Messages;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;

@SuppressWarnings("restriction")
public class DozerMultiPageEditor extends XMLMultiPageEditorPart {

	private int fDozerPageIndex;
	private int fLastChangeIndex;
	private DozerFormEditor dozerFormEditor;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void createPages() {
		//create the default WST pages
		super.createPages();
		
		try {
			//create the dozer page
			dozerFormEditor = new DozerFormEditor(this);
			
			//add the new dozer page
			fDozerPageIndex = this.addPage(dozerFormEditor, this.getEditorInput());
			setPageText(fDozerPageIndex, Messages.getString("DozerMappingEditor.label")); //$NON-NLS-1$
			
			//show dozer mapping page by default
			setActivePage(fDozerPageIndex);
		}
		catch (PartInitException e) {
			Logger.logException(e);
		}
	}
	
	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		
		//if coming from dozer page, format the source
		/*if (fLastChangeIndex > 0 && fLastChangeIndex == fDozerPageIndex) {
			IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
			try {
				handlerService.executeCommand("org.eclipse.wst.sse.ui.format.document", null); //$NON-NLS-1$
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		//remember the last page
		fLastChangeIndex = newPageIndex;
	}
	
	/**
	 * Returns the WTP Source Editor
	 * 
	 * @return WTP Source Editor
	 */
	public StructuredTextEditor getSourceEditor() {
		return (StructuredTextEditor)this.getEditor(1);
	}
	
	public void changeToSourcePage() {
		this.setActivePage(1);
	}
	
}