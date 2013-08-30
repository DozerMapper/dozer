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
import org.dozer.eclipse.plugin.editorpage.pages.composites.ConfigurationOptionComposite;
import org.dozer.eclipse.plugin.editorpage.pages.composites.MappingClassComposite;
import org.dozer.eclipse.plugin.editorpage.utils.DOMUtils;
import org.dozer.eclipse.plugin.editorpage.utils.HyperlinkHelper;
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
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.w3c.dom.Element;

public class MappingDetailsPage implements IDetailsPage {
	
	private FormPage page;
	private IManagedForm mform;
	private FormToolkit toolkit;
	private Element selectedMapping;
	private DozerModelManager modelManager;
	
	private ConfigurationOptionComposite configComposite;
	private MappingClassComposite classAComposite;
	private MappingClassComposite classBComposite;
	
	private Section mappingConfigSection;
	private Section mappingClassSection;
	
	public MappingDetailsPage(FormPage page, DozerModelManager modelManager) {
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
		
		createConfigurationSection(parent);
		classAComposite = createClassSection(parent, "a");
		classBComposite = createClassSection(parent, "b");
	}
	
	private void createConfigurationSection(Composite parent) {
		mappingConfigSection = toolkit.createSection(
				parent, 
				ExpandableComposite.TITLE_BAR | 
				ExpandableComposite.TWISTIE |
				ExpandableComposite.EXPANDED);
		mappingConfigSection.setText(Messages.getString("MappingsSection.mappingconfig_sname")); //$NON-NLS-1$		
		GridData gd = new GridData(SWT.FILL, 0, true, false);
		mappingConfigSection.setLayoutData(gd);
		
		//description
		HyperlinkHelper hh = new HyperlinkHelper(page, toolkit);
		FormText desc = hh.createFormText(mappingConfigSection, "MappingsSection.mappingconfig_sdesc");
		mappingConfigSection.setDescriptionControl(desc);

		configComposite = new ConfigurationOptionComposite(mappingConfigSection, toolkit, true, modelManager.getModel());
		mappingConfigSection.setClient(configComposite);
	}

	private MappingClassComposite createClassSection(Composite parent, String suffix) {
		mappingClassSection = toolkit.createSection(
				parent, 
				ExpandableComposite.TITLE_BAR);
		mappingClassSection.setText(Messages.getString("MappingsSection.mappingclass_sname_"+suffix)); //$NON-NLS-1$		
		GridData gd = new GridData(SWT.FILL, 0, true, false);
		mappingClassSection.setLayoutData(gd);
		
		MappingClassComposite classAOrBComposite = new MappingClassComposite(mappingClassSection, toolkit, modelManager);
		
		mappingClassSection.setClient(classAOrBComposite);
		return classAOrBComposite;
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
		selectedMapping = (Element)sel.getFirstElement();
		
		modelManager.createMappingBindings(configComposite, classAComposite, classBComposite, selectedMapping);
		
		//FIXME can this also be done with databinding?
		mappingConfigSection.setExpanded(selectedMapping.getAttributes().getLength() > 0);
		
		Element classA = DOMUtils.getElement(selectedMapping, "class-a");
		Element classB = DOMUtils.getElement(selectedMapping, "class-b");
		boolean expA = (classA != null) && classA.getAttributes().getLength() > 0;
		boolean expB = (classB != null) && classB.getAttributes().getLength() > 0;
		
		classAComposite.getOptionSection().setExpanded(expA);
		classBComposite.getOptionSection().setExpanded(expB);
	}

	public MappingClassComposite getClassAComposite() {
		return classAComposite;
	}

	public MappingClassComposite getClassBComposite() {
		return classBComposite;
	}
	
}