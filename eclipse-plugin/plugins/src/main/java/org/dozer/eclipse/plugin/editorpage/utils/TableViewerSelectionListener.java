package org.dozer.eclipse.plugin.editorpage.utils;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

abstract public class TableViewerSelectionListener extends SelectionAdapter {

	private TableViewer tableViewer;
	private IDOMModel model;
	
	public TableViewerSelectionListener(IDOMModel model) {
		this.model = model;
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		StructuredSelection structuredSelection = (StructuredSelection) tableViewer.getSelection();
		Object selected = structuredSelection.getFirstElement();
		
		invoceSelected(selected);
		
		if (tableViewer.getTable().getItemCount() > 0)
			tableViewer.getTable().select(0);
	}
	
	protected abstract void invoceSelected(Object selected);	

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public void setTableViewer(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public IDOMModel getModel() {
		return model;
	}

	public void setModel(IDOMModel model) {
		this.model = model;
	}
	
}
