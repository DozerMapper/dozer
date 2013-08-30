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

import org.dozer.eclipse.plugin.editorpage.DozerModelManager;
import org.dozer.eclipse.plugin.editorpage.Messages;
import org.dozer.eclipse.plugin.editorpage.pages.composites.FieldOptionComposite;
import org.dozer.eclipse.plugin.editorpage.pages.composites.MappingFieldComposite;
import org.dozer.eclipse.plugin.editorpage.utils.DOMUtils;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.w3c.dom.Element;

public class FieldDetailsPage implements IDetailsPage {
	
	private FormPage page;
	private IManagedForm mform;
	private FormToolkit toolkit;
	private DozerModelManager modelManager;

	private Element selectedMapping;
	private Element selectedField;
	
	private FieldOptionComposite configComposite;
	private MappingFieldComposite fieldAComposite;
	private MappingFieldComposite fieldBComposite;
	
	private Section mappingConfigSection;
	
	private IObservableValue className;
	private Composite parent;
	
	public FieldDetailsPage(FormPage page, DozerModelManager modelManager) {
		super();
		
		this.page = page;
		this.modelManager = modelManager;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IDetailsPage#initialize(org.eclipse.ui.forms.IManagedForm)
	 */
	public void initialize(IManagedForm mform) {
		this.mform = mform;
	}
	
	public void createContents(Composite parent) {
		toolkit = mform.getToolkit();
		
		GridLayout layout = new GridLayout();
		layout.marginTop = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 5;
		layout.marginRight = 0;
		parent.setLayout(layout);
		
		this.parent = parent;
	}
	
	private void createConfigurationSection(Composite parent, boolean isExcluded) {
		mappingConfigSection = toolkit.createSection(
				parent, 
				ExpandableComposite.TITLE_BAR | 
				ExpandableComposite.TWISTIE |
				ExpandableComposite.EXPANDED);
		mappingConfigSection.setText(Messages.getString("FieldSection.mappingconfig_sname")); //$NON-NLS-1$		
		GridData gd = new GridData(SWT.FILL, 0, true, false);
		mappingConfigSection.setLayoutData(gd);
		
		configComposite = new FieldOptionComposite(mappingConfigSection, toolkit, modelManager.getModel(), isExcluded);
		mappingConfigSection.setClient(configComposite);
	}

	private MappingFieldComposite createFieldSection(Composite parent, String suffix) {
		Section mappingFieldSection = toolkit.createSection(
				parent, 
				ExpandableComposite.TITLE_BAR);
		mappingFieldSection.setText(Messages.getString("MappingsSection.mappingfield_sname_"+suffix)); //$NON-NLS-1$		
		GridData gd = new GridData(SWT.FILL, 0, true, false);
		mappingFieldSection.setLayoutData(gd);
		
		MappingFieldComposite fieldAOrBComposite = new MappingFieldComposite(mappingFieldSection, toolkit, modelManager);		
		mappingFieldSection.setClient(fieldAOrBComposite);
		
		return fieldAOrBComposite;
	}	

	public void commit(boolean onSave) {
		// TODO Auto-generated method stub
		
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}	
	
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public boolean setFormInput(Object input) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Recieves selection from master
	 */
	public void selectionChanged(IFormPart part, ISelection selection) {
		StructuredSelection sel = (StructuredSelection) selection;
		selectedField = (Element)sel.getFirstElement();
		selectedMapping = (Element)selectedField.getParentNode();
		Element selectedA = DOMUtils.getElement(selectedField, "a");
		Element selectedB = DOMUtils.getElement(selectedField, "b");
		boolean isExcluded = "field-exclude".equals(selectedField.getNodeName());
		
		if (mappingConfigSection != null)
			mappingConfigSection.dispose();
		if (fieldAComposite != null)
			fieldAComposite.getParent().dispose();
		if (fieldBComposite != null)
			fieldBComposite.getParent().dispose();
		
		createConfigurationSection(parent, isExcluded);
		fieldAComposite = createFieldSection(parent, "a");
		fieldBComposite = createFieldSection(parent, "b");		

		
		modelManager.createFieldBindings(configComposite, fieldAComposite, fieldBComposite, selectedField);
		
		//FIXME can this also be done with databinding?
		mappingConfigSection.setExpanded(selectedField.getAttributes().getLength() > 0);
		
		fieldAComposite.getOptionSection().setExpanded(selectedA != null && selectedA.getAttributes().getLength() > 0);
		fieldBComposite.getOptionSection().setExpanded(selectedB != null && selectedB.getAttributes().getLength() > 0);
	}
	
}
