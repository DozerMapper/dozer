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
package org.dozer.eclipse.plugin.editorpage;

import org.dozer.eclipse.plugin.DozerMultiPageEditor;
import org.dozer.eclipse.plugin.DozerPlugin;
import org.dozer.eclipse.plugin.editorpage.pages.DozerConfigurationEditorPage;
import org.dozer.eclipse.plugin.editorpage.pages.DozerMappingEditorPage;
import org.dozer.eclipse.plugin.editorpage.utils.DozerUiUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.ui.internal.Logger;

public class DozerFormEditor extends FormEditor {
	
	private DozerModelManager modelManager = new DozerModelManager();	
	private DozerConfigurationEditorPage configurationPage;
	private DozerMappingEditorPage mappingPage;
	private DozerMultiPageEditor dozerEditor;
	
	private int configurationPageIndex;
	private int mappingPageIndex;
	
	/**
	 *  Constructor
	 */
	public DozerFormEditor(DozerMultiPageEditor dozerEditor) {
		this.dozerEditor = dozerEditor;
	}
	
	/**
	 * @return the wtp source editor
	 */
	public StructuredTextEditor getWtpEditor() {
		return dozerEditor.getSourceEditor();
	}
	
	/**
	 * @return the manager instance that takes care on the model
	 */
	public DozerModelManager getModelManager() {
		return modelManager;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#createToolkit(org.eclipse.swt.widgets.Display)
	 */
	@Override
	protected FormToolkit createToolkit(Display display) {
		// Create a toolkit that shares colors between editors.
		return new FormToolkit(DozerPlugin.getDefault().getFormColors(display));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages() {
		try {			
			DozerUiUtils.getInstance().setEditorInput(getEditorInput(), getToolkit());
			DozerUiUtils.getInstance().setMultiEditor(dozerEditor);
			
			//create pages
			configurationPage = new DozerConfigurationEditorPage(this);
			mappingPage = new DozerMappingEditorPage(this, this.getModelManager());
			
			//create controls so that refresh() can bind values to model
			configurationPage.createPartControl(getContainer());
			mappingPage.createPartControl(getContainer());
			
			//add pages
			configurationPageIndex = this.addPage(configurationPage);
			mappingPageIndex = this.addPage(mappingPage);
			
			//mapping page is the interesting one
			this.setActivePage(mappingPageIndex);
			
			//initialize binding
			modelManager.createBindings(configurationPage, mappingPage);
			modelManager.updateUI(configurationPage, mappingPage);			
		}
		catch (PartInitException e) {
			Logger.logException(e);
		}
	}
	
	// Listener used to observe change of DOM (change is done with another
	// editor).
	private IModelStateListener listener = new IModelStateListener() {

		public void modelAboutToBeChanged(IStructuredModel model) {}
		public void modelAboutToBeReinitialized(IStructuredModel model) {}

		public void modelChanged(IStructuredModel model) {
			//Update UI From DOM Model which has changed
			modelManager.updateUI(configurationPage, mappingPage);
		}

		public void modelDirtyStateChanged(IStructuredModel model,
				boolean isDirty) {
			// dirty from DOM Model has changed (the XML content was changed
			// with anothher editor), fire the dirty property change to
			// indicate to the editor that dirty has changed.
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}

		public void modelReinitialized(IStructuredModel model) {}
		public void modelResourceDeleted(IStructuredModel model) {}
		public void modelResourceMoved(IStructuredModel oldModel, IStructuredModel newModel) {}

	};		
	
	/**
	 * Delegate the Input to the Modelmanager
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		modelManager.setInput(input, listener);
	}	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
}