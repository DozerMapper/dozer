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
