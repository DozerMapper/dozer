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
import org.dozer.eclipse.plugin.editorpage.DozerFormEditor;
import org.dozer.eclipse.plugin.editorpage.Messages;
import org.dozer.eclipse.plugin.editorpage.actions.PackageExtendAction;
import org.dozer.eclipse.plugin.editorpage.pages.CustomConverterAddDialog.CustomConverterData;
import org.dozer.eclipse.plugin.editorpage.pages.composites.AddRemoveListComposite;
import org.dozer.eclipse.plugin.editorpage.pages.composites.ConfigurationOptionComposite;
import org.dozer.eclipse.plugin.editorpage.utils.DozerUiUtils;
import org.dozer.eclipse.plugin.editorpage.utils.ElementAsClassLabelProvider;
import org.dozer.eclipse.plugin.editorpage.utils.TableViewerSelectionListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.dom.utils.DOMUtils;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DozerConfigurationEditorPage extends FormPage {

	public static final String PAGE_ID = "org.dozer.eclipse.plugin.configuration.page";

	private FormToolkit toolkit;
	private ScrolledForm form;
	
	private ConfigurationOptionComposite configComposite;
	
	private TableViewer allowedExceptions;
	private TableViewer copyByReferences;
	private TableViewer customConverters;
	
	private DozerFormEditor editor;
	
	public DozerConfigurationEditorPage(DozerFormEditor editor) {
		super(	editor,
				PAGE_ID, 
				Messages.getString("DozerMappingEditorConfiguration.label")); //$NON-NLS-1$
		this.editor = editor;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		//make the form pretty
		form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		form.setText(Messages.getString("DozerMappingEditorConfiguration.title")); //$NON-NLS-1$
		form.setImage(DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_CONFIG_PAGE).createImage());
		toolkit.decorateFormHeading(form.getForm());
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.makeColumnsEqualWidth = true;
		form.getBody().setLayout(layout);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.verticalAlignment = SWT.BEGINNING;
		form.getBody().setLayoutData(gd);
		
		Composite leftClient = createColComposite(form.getBody());
		Composite rightClient = createColComposite(form.getBody());
		
		// Configuration section (left)
		createSingleValuesSection(leftClient);

		//Exceptions (right)
		createExceptionsSection(leftClient);
		
		//CopyByRef (left)
		createCopyByRefSection(rightClient);

		//CustomConverter (left)
		createCustomConverterSection(rightClient);
	}
	
	private Composite createColComposite(Composite parentComposite) {
		Composite colComposite = toolkit.createComposite(parentComposite);

		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		colComposite.setLayout(layout);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.BEGINNING;
		colComposite.setLayoutData(gd);
		
		return colComposite;
	}
	
	/**
	 * This section edits all the values that are not lists
	 * 
	 * @param managedForm
	 */	
	protected void createSingleValuesSection(Composite parentClient) {
		Section section = toolkit.createSection(parentClient,
				Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
		section.setText(Messages.getString("ConfigSection.stext")); //$NON-NLS-1$
		section.setDescription(Messages.getString("ConfigSection.sdesc")); //$NON-NLS-1$
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		section.setLayoutData(gd);
		
		configComposite = new ConfigurationOptionComposite(section, toolkit, false, editor.getModelManager().getModel());
		
		TableWrapData td = new TableWrapData();
		configComposite.setLayoutData(td);
		
		
		section.setClient(configComposite);
	}
	
	protected void createCopyByRefSection(Composite parentClient) {
		Section section = DozerUiUtils.createTwistieSection(parentClient, "ConfigSection.copybyreferences"); //$NON-NLS-1$

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.BEGINNING;
		section.setLayoutData(gd);
		
		TableViewerSelectionListener addBtnSelAdapter = 
			DozerUiUtils.createAddClassSelectionListener(
					editor.getModelManager().getModel(), 
					"copy-by-reference",
					null);
		
		TableViewerSelectionListener removeBtnSelAdapter = 
			DozerUiUtils.createDeleteSelectionListener(
					editor.getModelManager().getModel());
		
		copyByReferences = createLabelList(
				section, 
				"ConfigSection.copybyreferences",
				addBtnSelAdapter,
				removeBtnSelAdapter); 
		createSectionToolbar(section, copyByReferences);		
	}
	
	private void createSectionToolbar(Section section, TableViewer tableViewer) {
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(section);
		final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);
		// Cursor needs to be explicitly disposed
		toolbar.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if ((handCursor != null) && (handCursor.isDisposed() == false)) {
					handCursor.dispose();
				}
			}
		});
		PackageExtendAction fExtendAction = 
			new PackageExtendAction(
					"Show Qualified Type Names", 
					DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_PACKAGE), 
					tableViewer);
		toolBarManager.add(fExtendAction);

		toolBarManager.update(true);

		section.setTextClient(toolbar);
	}

	
	protected void createExceptionsSection(Composite parentClient) {
		Section section = DozerUiUtils.createTwistieSection(parentClient, "ConfigSection.allowedexceptions"); //$NON-NLS-1$

		TableViewerSelectionListener addBtnSelAdapter = 
			DozerUiUtils.createAddClassSelectionListener(
					editor.getModelManager().getModel(), 
					"exception", 
					"java.lang.RuntimeException");
		
		TableViewerSelectionListener removeBtnSelAdapter = 
			DozerUiUtils.createDeleteSelectionListener(
					editor.getModelManager().getModel());
		
		allowedExceptions = createLabelList(
				section,
				"ConfigSection.allowedexceptions",
				addBtnSelAdapter,
				removeBtnSelAdapter);
		
		createSectionToolbar(section, allowedExceptions);		
	}	

	protected void createCustomConverterSection(Composite parentClient) {
		Section section = DozerUiUtils.createTwistieSection(parentClient, "ConfigSection.customconverters"); //$NON-NLS-1$

		customConverters = createCustomConverterTable(
				section, 
				"ConfigSection.customconverters");
		
		createSectionToolbar(section, customConverters);
	}
	
	protected TableViewer createLabelList(
			Section section, 
			String messagePrefix, 
			TableViewerSelectionListener addBtnSelectionListener,
			TableViewerSelectionListener delBtnSelectionListener) {

		AddRemoveListComposite arl = 
			new AddRemoveListComposite(
					section,
					messagePrefix, 
					toolkit, 
					addBtnSelectionListener, 
					delBtnSelectionListener);
		
		section.setClient(arl);
		return arl.getTableViewer();
	}
	
	protected TableViewer createCustomConverterTable(
			Section section, 
			String messagePrefix) {
		
		TableViewerSelectionListener addBtnSelAdapter = new TableViewerSelectionListener(editor.getModelManager().getModel()) {
			@Override
			public void invoceSelected(Object selected) {
				final CustomConverterAddDialog dialog = 
					new CustomConverterAddDialog(
							PlatformUI.getWorkbench().getDisplay().getActiveShell(),
							this.getModel());
				if (dialog.open() == Window.OK) {
					BusyIndicator.showWhile(
							Display.getCurrent(), 
							new Runnable() {
								public void run() {
									CustomConverterData data = dialog.getData();
									
									Element element = getModel().getDocument().createElement("converter");
									element.setAttribute("type", data.getCustomConverterName());
									
									Element classA = getModel().getDocument().createElement("class-a");
									element.appendChild(classA);
									org.eclipse.core.dom.utils.DOMUtils.setTextContent(classA, data.getClassA());

									Element classB = getModel().getDocument().createElement("class-b");
									element.appendChild(classB);
									org.eclipse.core.dom.utils.DOMUtils.setTextContent(classB, data.getClassB());

									IObservableList values = (IObservableList)getTableViewer().getInput();
									values.add(element);		
								}
						});
				}
			}
		};			
				
		TableViewerSelectionListener removeBtnSelAdapter = 
			DozerUiUtils.createDeleteSelectionListener(
					editor.getModelManager().getModel());
		
		AddRemoveListComposite arl = 
			new AddRemoveListComposite(
					section, 
					messagePrefix, 
					toolkit,
					addBtnSelAdapter,
					removeBtnSelAdapter);
		
		//TABLE
		Table table = arl.getTable();
		table.setHeaderVisible(true);
		
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText("Converter");
        TableColumn column2 = new TableColumn(table, SWT.LEFT);
		column2.setText("From");
        TableColumn column3 = new TableColumn(table, SWT.LEFT);
		column3.setText("To");
		
		TableLayout tableLayout = new TableLayout();
		ColumnWeightData columnData1 = new ColumnWeightData(1);
		columnData1.minimumWidth = 100;
		ColumnWeightData columnData2 = new ColumnWeightData(1);
		columnData2.minimumWidth = 100;
		ColumnWeightData columnData3 = new ColumnWeightData(1);
		columnData3.minimumWidth = 100;
		
		tableLayout.addColumnData(columnData1);
		tableLayout.addColumnData(columnData2);		
		tableLayout.addColumnData(columnData3);		
		table.setLayout(tableLayout);
		
	//Logic
		ElementAsClassLabelProvider labelProvider = 
			new ElementAsClassLabelProvider() {
			
			@Override
			protected String getColumnText(Element node, int columnIndex) {
				Element child;
				NodeList nodeList;
				
				switch (columnIndex) {
				case 0:
					String attrValue = node.getAttribute("type");
					if (attrValue != null)
						return DozerUiUtils.nullSafeTrimString(attrValue);

					break;
				case 1:
					nodeList = node.getElementsByTagName("class-a");
					child = (Element)nodeList.item(0);
					if (child != null)
						return DOMUtils.getTextContent(child);

					break;
				case 2:
					nodeList = node.getElementsByTagName("class-b");
					child = (Element)nodeList.item(0);
					if (child != null)
						return DOMUtils.getTextContent(child);

					break;
				}
				
				return null;
			}	
		};
		
		final TableViewer tableViewer = arl.getTableViewer();
		tableViewer.setLabelProvider(labelProvider);
		
		TableWrapData td = new TableWrapData();
		arl.setLayoutData(td);
		
		section.setClient(arl);
		return arl.getTableViewer();
	}

	public IObservableValue getRelationshipType() {
		return configComposite.getRelationshipType();
	}

	public IObservableValue getStopOnErrors() {
		return configComposite.getStopOnErrors();
	}

	public IObservableValue getWildcard() {
		return configComposite.getWildcard();
	}

	public IObservableValue getTrimStrings() {
		return configComposite.getTrimStrings();
	}

	public IObservableValue getDateFormat() {
		return configComposite.getDateFormat();
	}

	public IObservableValue getBeanFactory() {
		return configComposite.getBeanFactory();
	}

	public TableViewer getAllowedExceptions() {
		return allowedExceptions;
	}

	public TableViewer getCopyByReferences() {
		return copyByReferences;
	}

	public TableViewer getCustomConverters() {
		return customConverters;
	}

}
