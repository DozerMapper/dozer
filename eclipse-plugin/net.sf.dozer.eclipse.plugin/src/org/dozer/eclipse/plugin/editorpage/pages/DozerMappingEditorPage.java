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
package org.dozer.eclipse.plugin.editorpage.pages;

import org.dozer.eclipse.plugin.DozerPlugin;
import org.dozer.eclipse.plugin.editorpage.DozerModelManager;
import org.dozer.eclipse.plugin.editorpage.Messages;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class DozerMappingEditorPage extends FormPage {
	
	public static final String PAGE_ID = "org.dozer.eclipse.plugin.mapping.page";
	private MappingMasterPage dozerMappingListBlock;
	private FormToolkit toolkit;
	private ScrolledForm form;
	private DozerModelManager modelManager;

	public DozerMappingEditorPage(FormEditor editor, DozerModelManager modelManager) {
		super(	editor, 
				PAGE_ID, 
				Messages.getString("DozerMappingEditorMapping.label")); //$NON-NLS-1$ 
		
		this.modelManager = modelManager;
		dozerMappingListBlock = new MappingMasterPage(this);
	}
	
	public DozerModelManager getModelManager() {
		return modelManager;
	}
	
	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		form.setText(Messages.getString("DozerMappingEditorMapping.title")); //$NON-NLS-1$
		form.setImage(DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_MAPPINGS_PAGE).createImage());
		toolkit.decorateFormHeading(form.getForm());
		
		dozerMappingListBlock.createContent(managedForm);		
	}
	
	public MappingMasterPage getDozerMappingListBlock() {
		return dozerMappingListBlock;
	}
}