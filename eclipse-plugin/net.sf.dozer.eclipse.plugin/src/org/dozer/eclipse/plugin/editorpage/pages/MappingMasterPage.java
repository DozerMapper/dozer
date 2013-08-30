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
import org.dozer.eclipse.plugin.editorpage.Messages;
import org.dozer.eclipse.plugin.editorpage.actions.CollapseAllAction;
import org.dozer.eclipse.plugin.editorpage.actions.PackageExtendAction;
import org.dozer.eclipse.plugin.editorpage.utils.DOMUtils;
import org.dozer.eclipse.plugin.editorpage.utils.DozerUiUtils;
import org.dozer.eclipse.plugin.editorpage.utils.ElementAsClassLabelProvider;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ufacekit.core.databinding.sse.dom.SSEDOMObservables;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.w3c.dom.Element;

/**
 *
 */
public class MappingMasterPage extends MasterDetailsBlock {

	private DozerMappingEditorPage page;
	private TreeViewer mappings;
	private FilteredTree filteredTree;
	
	private Action fAddClassMappingAction;
	private Action fAddFieldMappingAction;
	private Action fAddFieldExcludeMappingAction;
	private CollapseAllAction fExpandAllAction;
	private CollapseAllAction fCollapseAllAction;
	private PackageExtendAction fExtendAction;	
	
	/**
	 * constructor
	 * 
	 * @param page
	 */
	public MappingMasterPage(DozerMappingEditorPage page) {
		this.page = page;
	}
	
	@Override
	public void createContent(IManagedForm managedForm) {
		super.createContent(managedForm);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 5;
		layout.marginBottom = 5;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		managedForm.getForm().getBody().setLayout(layout);
				
		//let the details take the most space
		this.sashForm.setWeights(new int[]{35,65});		
	}	

	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		
		//classmapping section
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		section.setText(Messages.getString("MappingsSection.masterblock_sname")); //$NON-NLS-1$
		
		//FIXME clean up that mess
		Composite arl = toolkit.createComposite(section);

		GridLayout layout2 = new GridLayout();
		layout2.horizontalSpacing = 0;
		layout2.marginWidth = 0;
		layout2.marginLeft = 0;
		layout2.marginRight = 0;		

		arl.setLayout(layout2);
	
		section.setClient(arl);
		final SectionPart spart = new SectionPart(section);
		managedForm.addPart(spart);
	
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		arl.setLayoutData(gd);
		
		
	//Logic
		PatternFilter patternFilter = new PatternFilter();
		patternFilter.setIncludeLeadingWildcard(true);
		filteredTree = new FilteredTree(arl, SWT.FULL_SELECTION, patternFilter);
		mappings = filteredTree.getViewer();
		mappings.setUseHashlookup(true);
		final Tree tree = mappings.getTree(); 
		
		//Master/Detail relation
		mappings.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {	
				managedForm.fireSelectionChanged(spart, event.getSelection());
				fAddFieldMappingAction.setEnabled(!event.getSelection().isEmpty());
				fAddFieldExcludeMappingAction.setEnabled(!event.getSelection().isEmpty());
			}
		});	
		
		ITreeContentProvider contentProvider = new ITreeContentProvider() {

			public Object[] getChildren(Object parentElement) {
				return getElements(parentElement);
			}

			public Object getParent(Object element) {
				// TODO Auto-generated method stub
				return null;
			}

			public boolean hasChildren(Object element) {
				Element domElement = (Element)element;
				
				return "mapping".equals(domElement.getNodeName()) && DOMUtils.getElements(domElement).length > 0;
			}

			public Object[] getElements(Object inputElement) {
				IObservableList observableList = null;

				if (inputElement instanceof IObservableValue) {
					observableList = SSEDOMObservables.observeDetailList(Realm.getDefault(), (IObservableValue)inputElement, "mapping") ;
				} else if (inputElement instanceof Element) {
					Element element = (Element)inputElement;
					
					if ("mapping".equals(element.getNodeName())) {
						IObservableList observableListField = SSEDOMObservables.observeNodeList(element, "field", null);
						IObservableList observableListFieldExclude = SSEDOMObservables.observeNodeList(element, "field-exclude", null);
						
						//FIXME does that reflect everything?
						observableList = new WritableList();
						observableList.addAll(observableListField);
						observableList.addAll(observableListFieldExclude);
					}
				}
				
				return observableList.toArray();
			}

			public void dispose() {
				// TODO Auto-generated method stub
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// TODO Auto-generated method stub
				
			}
			
		};
		mappings.setContentProvider(contentProvider);
		
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 20;
		filteredTree.setLayoutData(gd);
		
		//TABLE
		tree.setHeaderVisible(true);
		
		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setText("From class");
        column.setWidth(200);
        TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
		column2.setText("To class");
        column2.setWidth(200);
        
        //FIXME lets show some common info about the fieldmappings?
        /*TreeColumn column3 = new TreeColumn(tree, SWT.LEFT);
        column3.setWidth(18);
        column3.setResizable(false);
        TreeColumn column4 = new TreeColumn(tree, SWT.LEFT);
        column4.setWidth(18);
        column4.setResizable(false);
        TreeColumn column5 = new TreeColumn(tree, SWT.LEFT);
        column5.setWidth(18);
        column5.setResizable(false);
        TreeColumn column6 = new TreeColumn(tree, SWT.LEFT);
        column6.setWidth(18);
        column6.setResizable(false);*/

		//Logic
        LabelProvider labelProvider = 
			new ElementAsClassLabelProvider() {
			
        	private Image attrImg = DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_ATTRIBUTE).createImage();
        	private Image attrExclImg = DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_EXCLUDE).createImage();
        	
        	/*private Image byRefImg = DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_BYREF).createImage();
        	private Image customConverterImg = DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_CUSTOMCONVERTER).createImage();
        	private Image oneWayImg = DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_ONEWAY).createImage();*/
        	
        	@Override
			public Image getColumnImage(Object element, int columnIndex) {
        		if (!(element instanceof Element))
        			return null;
        		
        		Element currentElement = (Element)element;
        		String nodeName = currentElement.getNodeName();
        		
        		if ("mapping".equals(nodeName) && columnIndex < 2) {
        			Element elementA = DOMUtils.getElement(currentElement, "class-a");
        			Element elementB = DOMUtils.getElement(currentElement, "class-b");

        			String classA = org.eclipse.core.dom.utils.DOMUtils.getTextContent(elementA);
	        		String classB = org.eclipse.core.dom.utils.DOMUtils.getTextContent(elementB);
	        		
	        		if (columnIndex == 0)
	        			return DozerUiUtils.getInstance().getImageFromClassName(classA);
	        		else if (columnIndex == 1)
	        			return DozerUiUtils.getInstance().getImageFromClassName(classB);
        		} else if ("field".equals(nodeName) || "field-exclude".equals(nodeName)) {
        			if (columnIndex < 2)
        				return "field".equals(nodeName)?attrImg:attrExclImg;
        			/*else {
        				if (columnIndex == 2 && MappingUtils.isExcluded(currentElement))
        					return excludeImg;
        				else if (columnIndex == 3 && MappingUtils.isCopyByRef(currentElement))
        					return byRefImg;
        				else if (columnIndex == 4 && MappingUtils.useCustomConverter(currentElement))
        					return customConverterImg;
        				else if (columnIndex == 4 && MappingUtils.isOneWay(currentElement))
        					return oneWayImg;
        			}*/
        		}
        		
        		return null;
        	}        	
        	
        	@Override
			public String getColumnText(Object element, int columnIndex) {
        		if (columnIndex > 1)
        			return null;
        		if (!(element instanceof Element))
        			return null;        		
        		
        		Element currentElement = (Element)element;
        		String nodeName = currentElement.getNodeName();
        		String returnValue = null;
        		
        		if ("mapping".equals(nodeName)) {
        			Element elementA = DOMUtils.getElement(currentElement, "class-a");
        			Element elementB = DOMUtils.getElement(currentElement, "class-b");

        			if (columnIndex == 0)
	        			returnValue = getColumnText(elementA, columnIndex);
	        		else if (columnIndex == 1)
	        			returnValue = getColumnText(elementB, columnIndex);
        		} else if ("field".equals(nodeName) || "field-exclude".equals(nodeName)) {
        			if (columnIndex == 0) {
        				Element elementA = DOMUtils.getElement(currentElement, "a");
        				returnValue = org.eclipse.core.dom.utils.DOMUtils.getTextContent(elementA);
        			} else if (columnIndex == 1) {
	        			Element elementB = DOMUtils.getElement(currentElement, "b");
        				returnValue = org.eclipse.core.dom.utils.DOMUtils.getTextContent(elementB);
        			}
        		}
        		
        		if (returnValue == null)
        			return null;
        		else if (isShowShortNames())
        			return getShortClassName(returnValue);
        		else
        			return returnValue;
        	}	
        	
		};
		
		mappings.setLabelProvider(labelProvider);
		
		//Jump to node in sourceview, if doubleclicked
		tree.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {

			@Override
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent e) {
				TreeItem[] selection = tree.getSelection();
				TreeItem selectionOne = selection[0];
				
				if (!(selectionOne.getData() instanceof Element))
					return;
				
				Element node = (Element)selectionOne.getData();
				if (node != null)
					DozerUiUtils.getInstance().jumpToElement(node);
			}
			
		});
		
		//Label for total count
		final Label countLabel = toolkit.createLabel(arl, "Total: 0");
		countLabel.setAlignment(SWT.RIGHT);
		gd = new GridData(SWT.FILL, 0, true, false);
		countLabel.setLayoutData(gd);
		
		tree.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				//refresh label
				countLabel.setText("Total: "+Integer.toString(tree.getItemCount()));
			}
		});
		
		//Delete Key
		tree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					deleteItem();
				}				
			}	
		});
		
		createActions();
		createSectionToolbar(section);
		
		final MenuManager popupMenuManager = new MenuManager();
		IMenuListener listener = new IMenuListener() {
			public void menuAboutToShow(IMenuManager mng) {
				fillContextMenu(popupMenuManager);
			}
		};
		popupMenuManager.addMenuListener(listener);
		
		Menu menu = popupMenuManager.createContextMenu(tree);
		tree.setMenu(menu);
	}

	protected void fillContextMenu(IMenuManager manager) {
		manager.removeAll();
		TreeSelection selection = (TreeSelection)filteredTree.getViewer().getSelection();
		
		Action fRemoveAction = new Action("Remove Class-Mapping") {
			@Override
			public void run() {
				StructuredSelection structuredSelection = (StructuredSelection)mappings.getSelection();
				Element selected = (Element)structuredSelection.getFirstElement();
				
				if ("mapping".equals(selected.getNodeName())) {
					deleteItem();
				} else if ("field".equals(selected.getNodeName()) || "field-exclude".equals(selected.getNodeName())) {
					//delete mapping
					Element parentNode = (Element)selected.getParentNode();					
					parentNode.getParentNode().removeChild(parentNode);
					mappings.refresh();
				}
			}
		};

		Action fFieldRemoveAction = new Action("Remove Field-Mapping") {
			@Override
			public void run() {
				deleteItem();
			}
		};

		
		if (!selection.isEmpty()) {
			manager.add(fAddClassMappingAction);
			manager.add(fRemoveAction);
			manager.add(new Separator());
			manager.add(fAddFieldMappingAction);
			manager.add(fAddFieldExcludeMappingAction);			

			Element element = (Element)selection.getFirstElement();
			if ("field".equals(element.getNodeName()) || "field-exclude".equals(element.getNodeName())) {
				manager.add(fFieldRemoveAction);
			}			
		}
	}
		
	private void deleteItem() {
		StructuredSelection structuredSelection = (StructuredSelection)mappings.getSelection();
		Element selected = (Element)structuredSelection.getFirstElement();
		
		//this.page.getModelManager().getModel().aboutToChangeModel();
		
		Element parentNode = (Element)selected.getParentNode();
		parentNode.removeChild(selected);
		
		mappings.refresh();
		//this.page.getModelManager().getModel().changedModel();
	}	
	
	private void addMappingItem() {
		StructuredSelection structuredSelection = (StructuredSelection)mappings.getSelection();
		Element selected = (Element)structuredSelection.getFirstElement();
		
		//this.page.getModelManager().getModel().aboutToChangeModel();
		
		Element mappingsNode = page.getModelManager().getModel().getDocument().getDocumentElement();
		Element mapping = mappingsNode.getOwnerDocument().createElement("mapping");
			
		if (selected!=null && "mapping".equals(selected.getNodeName())) {
			mappingsNode.insertBefore(mapping, selected);
		} else
			mappingsNode.appendChild(mapping);
		
		mappings.refresh();
		//this.page.getModelManager().getModel().changedModel();
	}		
	
	private void addFieldItem(boolean exclude) {
		StructuredSelection structuredSelection = (StructuredSelection)mappings.getSelection();
		Element selected = (Element)structuredSelection.getFirstElement();
		
		String elementName = exclude?"field-exclude":"field";
		
		//this.page.getModelManager().getModel().aboutToChangeModel();
		Element field = selected.getOwnerDocument().createElement(elementName);
		Element mappingNode = null;	
		
		if ("field".equals(selected.getNodeName()) || "field-exclude".equals(selected.getNodeName())) {
			mappingNode = (Element)selected.getParentNode();
			mappingNode.insertBefore(field, selected);
		} else {
			mappingNode = selected;
			mappingNode.appendChild(field);			
		}
		
		mappings.refresh();
		
		//this.page.getModelManager().getModel().changedModel();
	}			
	
	private void createActions() {
		fAddClassMappingAction = 
			new Action(
					"Add Class-Mapping...", 
					DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_ADD_CLASSMAPPING)) {
			
			@Override
			public void run() {
				addMappingItem();
			}
		};
		fAddFieldMappingAction = 
			new Action(
					"Add Field-Mapping...", 
					DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_ADD_FIELDMAPPING)) {
			
			@Override
			public void run() {
				addFieldItem(false);
			}
		};
		fAddFieldMappingAction.setEnabled(false);
		fAddFieldExcludeMappingAction = 
			new Action(
					"Add Field-Exclude-Mapping...", 
					DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_ADD_FIELDEXCLUDEMAPPING)) {
			
			@Override
			public void run() {
				addFieldItem(true);
			}
		};
		fAddFieldExcludeMappingAction.setEnabled(false);		
		
		fExpandAllAction = 
			new CollapseAllAction(
					"Expand All", 
					DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_EXPAND_ALL),
					mappings,
					false);		
		fCollapseAllAction = 
			new CollapseAllAction(
					"Collapse All", 
					DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_COLLAPSE_ALL), 
					mappings,
					true);
		fExtendAction = 
			new PackageExtendAction(
					"Show Qualified Type Names", 
					DozerPlugin.getDefault().getImageRegistry().getDescriptor(DozerPlugin.IMG_PACKAGE), 
					mappings);		
	}
	
	
	private void createSectionToolbar(Section section) {
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

		toolBarManager.add(fAddClassMappingAction);		
		toolBarManager.add(fAddFieldMappingAction);
		toolBarManager.add(fAddFieldExcludeMappingAction);		
		toolBarManager.add(fExpandAllAction);		
		toolBarManager.add(fCollapseAllAction);		
		toolBarManager.add(fExtendAction);		

		toolBarManager.update(true);

		section.setTextClient(toolbar);
	}	
	
	public TreeViewer getMappings() {
		return mappings;
	}
	
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		Action haction = new Action("hor", IAction.AS_RADIO_BUTTON) { //$NON-NLS-1$
			@Override
			public void run() {
				sashForm.setOrientation(SWT.HORIZONTAL);
				form.reflow(true);
			}
		};
		haction.setChecked(true);
		haction.setToolTipText(Messages.getString("MappingsPage.horizontal")); //$NON-NLS-1$
		haction.setImageDescriptor(DozerPlugin.getDefault()
				.getImageRegistry()
				.getDescriptor(DozerPlugin.IMG_HORIZONTAL));
		Action vaction = new Action("ver", IAction.AS_RADIO_BUTTON) { //$NON-NLS-1$
			@Override
			public void run() {
				sashForm.setOrientation(SWT.VERTICAL);
				form.reflow(true);
			}
		};
		vaction.setChecked(false);
		vaction.setToolTipText(Messages.getString("MappingsPage.vertical")); //$NON-NLS-1$
		vaction.setImageDescriptor(DozerPlugin.getDefault()
				.getImageRegistry().getDescriptor(DozerPlugin.IMG_VERTICAL));
		form.getToolBarManager().add(haction);
		form.getToolBarManager().add(vaction);
	}
	
	@Override
	protected void registerPages(final DetailsPart detailsPart) {
		detailsPart.setPageProvider( new IDetailsPageProvider() {

			public IDetailsPage getPage(Object key) {
				if ("mapping".equals(key.toString()))
					return new MappingDetailsPage(page, page.getModelManager());
				else if ("field".equals(key.toString()) || "field-exclude".equals(key.toString()))
					return new FieldDetailsPage(page, page.getModelManager());
				else
					return null;
			}

			public Object getPageKey(Object object) {
				if (!(object instanceof Element))
					return null;
				
				Element element = (Element)object;
				return element.getNodeName();
			}
			
		});
	}
}