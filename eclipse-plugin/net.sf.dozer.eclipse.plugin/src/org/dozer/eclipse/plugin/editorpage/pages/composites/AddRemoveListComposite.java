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
package org.dozer.eclipse.plugin.editorpage.pages.composites;

import org.dozer.eclipse.plugin.editorpage.Messages;
import org.dozer.eclipse.plugin.editorpage.utils.ElementAsClassLabelProvider;
import org.dozer.eclipse.plugin.editorpage.utils.TableViewerSelectionListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class AddRemoveListComposite extends Composite {

	private TableViewer tableViewer;
	private Table table;
	private Action fAddAction;
	private Action fRemoveAction;
	
	public AddRemoveListComposite(
			final Section section, 
			final String messagePrefix, 
			final FormToolkit toolkit,
			final TableViewerSelectionListener addBtnSelectionListener,
			final TableViewerSelectionListener removeBtnSelectionListener) {
		
		super(section, SWT.FILL);
		
		//this.setBackground(new Color(Display.getCurrent(), 255,0,0));
		
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		this.setLayout(layout);
		
	//TABLE
		table = toolkit.createTable(this, SWT.FULL_SELECTION);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(gd);
		toolkit.paintBordersFor(this);
		
	//BUTTONS
		Composite tableBtnClient = toolkit.createComposite(this);
		GridLayout tableBtnlayout = new GridLayout();
		tableBtnlayout.numColumns = 1;
		tableBtnlayout.marginWidth = 3;
		tableBtnlayout.marginHeight = 3;
		tableBtnClient.setLayout(tableBtnlayout);
		
		gd = new GridData(GridData.FILL_VERTICAL);
		tableBtnClient.setLayoutData(gd);
		
	//Logic
		tableViewer = new TableViewer(table);		
		tableViewer.setLabelProvider(new ElementAsClassLabelProvider());

		removeBtnSelectionListener.setTableViewer(tableViewer);
		addBtnSelectionListener.setTableViewer(tableViewer);
		
		Button btnAdd = toolkit.createButton(tableBtnClient, Messages.getString(messagePrefix+"_add"), SWT.PUSH); //$NON-NLS-1$
		gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		btnAdd.setLayoutData(gd);
		btnAdd.addSelectionListener(addBtnSelectionListener);

		Button btnRemove = toolkit.createButton(tableBtnClient, Messages.getString(messagePrefix+"_remove"), SWT.PUSH); //$NON-NLS-1$
		gd = new GridData(SWT.END, SWT.END, false, false);
		btnRemove.setLayoutData(gd);
		btnRemove.addSelectionListener(removeBtnSelectionListener);

		//Label for total count
		final Label countLabel = toolkit.createLabel(tableBtnClient, "Total: 0");
		gd = new GridData(SWT.BEGINNING, SWT.END, false, true);
		countLabel.setLayoutData(gd);	
		
		table.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				//refresh label
				countLabel.setText("Total: "+Integer.toString(table.getItemCount()));
			}
		});
		
		//FIXME thats ugly, but there is no listener i can use
		//data is used in ContentProvider to expand or collapse 
		tableViewer.setData("section", section);
		
		//Delete Key
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					removeBtnSelectionListener.widgetSelected(null);
				}				
			}			
		});
		
		fAddAction = new Action("Add") {
			@Override
			public void run() {
				addBtnSelectionListener.widgetSelected(null);
			}
		};
		fRemoveAction = new Action("Remove") {
			@Override
			public void run() {
				removeBtnSelectionListener.widgetSelected(null);
			}
		};
		
		final MenuManager popupMenuManager = new MenuManager();
		IMenuListener listener = new IMenuListener() {
			public void menuAboutToShow(IMenuManager mng) {
				fillContextMenu(popupMenuManager);
			}
		};
		popupMenuManager.addMenuListener(listener);
		
		Menu menu = popupMenuManager.createContextMenu(table);
		table.setMenu(menu);
	}
	
	protected void fillContextMenu(IMenuManager manager) {
		manager.removeAll();
		ISelection selection = tableViewer.getSelection();
		manager.add(fAddAction);
		//manager.add(new Separator());
		if (!selection.isEmpty())
			manager.add(fRemoveAction);
	}

	
	public TableViewer getTableViewer() {
		return tableViewer;
	}
	
	public Table getTable() {
		return table;
	}

}
