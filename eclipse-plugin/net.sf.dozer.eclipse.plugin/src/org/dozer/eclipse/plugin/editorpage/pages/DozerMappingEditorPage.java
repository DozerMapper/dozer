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