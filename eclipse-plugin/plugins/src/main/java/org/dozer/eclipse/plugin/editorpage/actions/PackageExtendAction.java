package org.dozer.eclipse.plugin.editorpage.actions;

import org.dozer.eclipse.plugin.editorpage.utils.ElementAsClassLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewer;

public class PackageExtendAction extends Action {

	private ColumnViewer columnViewer;
	
	public PackageExtendAction(String text, ImageDescriptor image, ColumnViewer columnViewer) {
		super(text, IAction.AS_CHECK_BOX);
		this.setImageDescriptor(image);
		this.columnViewer = columnViewer;
	}
	
	@Override
	public void run() {
		ElementAsClassLabelProvider labelProvider = (ElementAsClassLabelProvider)columnViewer.getLabelProvider();
		
		labelProvider.setShowShortNames(!this.isChecked());
		columnViewer.refresh();		
		notifyResult(true);
	}	

}
