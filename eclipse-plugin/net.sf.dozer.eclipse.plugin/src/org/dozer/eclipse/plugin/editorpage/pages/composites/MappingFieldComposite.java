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

import org.dozer.eclipse.plugin.editorpage.DozerModelManager;
import org.dozer.eclipse.plugin.editorpage.utils.DozerUiUtils;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class MappingFieldComposite extends Composite {

	private IObservableValue dateFormat;
	private IObservableValue type;
	private IObservableValue key;
	private IObservableValue isAccessible;
	private ObservableSingleSelectionObject fieldName;
	private ObservableSingleSelectionObject setMethod;
	private ObservableSingleSelectionObject getMethod;
	private ObservableSingleSelectionObject mapSetMethod;
	private ObservableSingleSelectionObject mapGetMethod;
	private ObservableSingleSelectionObject createMethod;
	
	private AbstractListViewer fieldComboViewer;
	private AbstractListViewer getComboViewer;
	private AbstractListViewer setComboViewer;	
	private AbstractListViewer mapGetComboViewer;
	private AbstractListViewer mapSetComboViewer;
	private AbstractListViewer createComboViewer;	
	
	private Section optionSection;
	
	public MappingFieldComposite(Composite parent, FormToolkit toolkit, final DozerModelManager modelManager) {
		super(parent, SWT.NULL);
		
		//FIXME otherwise its transparent!?!?
		this.setBackground(new Color(Display.getCurrent(), 255,255,255));
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		this.setLayout(layout);

		fieldComboViewer = DozerUiUtils.createLabelFieldCombobox(
				this, 
				"MappingSection.fieldName"); //$NON-NLS-1$		
		fieldName = new ObservableSingleSelectionObject(fieldComboViewer);

		optionSection = toolkit.createSection(this, ExpandableComposite.TWISTIE | ExpandableComposite.SHORT_TITLE_BAR);
		optionSection.setText("Field Options");
		
		Composite sectionComposite = toolkit.createComposite(optionSection);
		optionSection.setClient(sectionComposite);
		
		TableWrapData td = new TableWrapData(TableWrapData.FILL);
		td.colspan = 2;
		optionSection.setLayoutData(td);
	
		TableWrapLayout layout2 = new TableWrapLayout();
		layout2.numColumns = 2;
		sectionComposite.setLayout(layout2);
		
		dateFormat = DozerUiUtils.createLabelText(sectionComposite, "ConfigSection.dateformat"); //$NON-NLS-1$	
		
		DozerUiUtils.createLabel(sectionComposite, "FieldSection.type");
		Composite comboComposite = toolkit.createComposite(sectionComposite);
		TableWrapLayout comboLayout = new TableWrapLayout();
		comboLayout.numColumns = 5;
		comboLayout.bottomMargin = 0;
		comboLayout.horizontalSpacing = 5;
		comboLayout.leftMargin = 0;
		comboLayout.rightMargin = 0;
		comboLayout.topMargin = 0;
		comboComposite.setLayout(comboLayout);
		
		td = new TableWrapData();
		comboComposite.setLayoutData(td);
		
		type = DozerUiUtils.createCombo(
				comboComposite, 
				"FieldSection.type", new String[] { "", "generic", "iterate" }); //$NON-NLS-1$
		key = DozerUiUtils.createLabelText(
				comboComposite, 
				"FieldSection.key"); //$NON-NLS-1$
		isAccessible = DozerUiUtils.createLabelCombo(
				comboComposite, 
				"FieldSection.isAccessible", new String[] { "", "true", "false" }); //$NON-NLS-1$
		
		getComboViewer = DozerUiUtils.createLabelMethodCombobox(
				sectionComposite, 
				"MappingSection.getMethod"); //$NON-NLS-1$
		getMethod = new ObservableSingleSelectionObject(getComboViewer);
		
		setComboViewer = DozerUiUtils.createLabelMethodCombobox(
				sectionComposite, 
				"MappingSection.setMethod"); //$NON-NLS-1$	
		setMethod = new ObservableSingleSelectionObject(setComboViewer);
		
		mapGetComboViewer = DozerUiUtils.createLabelMethodCombobox(
				sectionComposite, 
				"MappingSection.mapGetMethod"); //$NON-NLS-1$
		mapGetMethod = new ObservableSingleSelectionObject(mapGetComboViewer);
		
		mapSetComboViewer = DozerUiUtils.createLabelMethodCombobox(
				sectionComposite, 
				"MappingSection.mapSetMethod"); //$NON-NLS-1$	
		mapSetMethod = new ObservableSingleSelectionObject(mapSetComboViewer);

		createComboViewer = DozerUiUtils.createLabelMethodCombobox(
				sectionComposite, 
				"MappingSection.createMethod"); //$NON-NLS-1$
		createMethod =  new ObservableSingleSelectionObject(createComboViewer);	
	}
	
	public ObservableSingleSelectionObject getMapSetMethod() {
		return mapSetMethod;
	}

	public ObservableSingleSelectionObject getMapGetMethod() {
		return mapGetMethod;
	}

	public ObservableSingleSelectionObject getCreateMethod() {
		return createMethod;
	}

	public Section getOptionSection() {
		return optionSection;
	}

	public ObservableSingleSelectionObject getFieldName() {
		return fieldName;
	}

	public IObservableValue getDateFormat() {
		return dateFormat;
	}

	public IObservableValue getType() {
		return type;
	}

	public ObservableSingleSelectionObject getSetMethod() {
		return setMethod;
	}

	public ObservableSingleSelectionObject getGetMethod() {
		return getMethod;
	}

	public IObservableValue getKey() {
		return key;
	}

	public IObservableValue getIsAccessible() {
		return isAccessible;
	}

	public AbstractListViewer getFieldComboViewer() {
		return fieldComboViewer;
	}

	public AbstractListViewer getGetComboViewer() {
		return getComboViewer;
	}

	public AbstractListViewer getSetComboViewer() {
		return setComboViewer;
	}

	public AbstractListViewer getMapGetComboViewer() {
		return mapGetComboViewer;
	}

	public AbstractListViewer getMapSetComboViewer() {
		return mapSetComboViewer;
	}

	public AbstractListViewer getCreateComboViewer() {
		return createComboViewer;
	}
}