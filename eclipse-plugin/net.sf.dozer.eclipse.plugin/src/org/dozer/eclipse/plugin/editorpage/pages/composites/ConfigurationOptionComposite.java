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

import org.dozer.eclipse.plugin.editorpage.utils.DozerUiUtils;
import org.dozer.eclipse.plugin.editorpage.utils.DozerUtils;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class ConfigurationOptionComposite extends Composite {

	//Ui elements of dozer global configs
	private IObservableValue relationshipType;
	private IObservableValue stopOnErrors;
	private IObservableValue wildcard;
	private IObservableValue trimStrings;
	private IObservableValue dateFormat;
	private IObservableValue beanFactory;

	private IObservableValue mappingType;
	private IObservableValue mapNull;
	private IObservableValue mapEmpty;
	private IObservableValue mapId;
	
	public ConfigurationOptionComposite(Composite parent, FormToolkit toolkit, boolean isMapping, IDOMModel model) {
		super(parent, SWT.NULL);
		
		//FIXME otherwise its transparent!?!?
		this.setBackground(new Color(Display.getCurrent(), 255,255,255));
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		this.setLayout(layout);
		
		dateFormat = DozerUiUtils.createLabelText(this, "ConfigSection.dateformat"); //$NON-NLS-1$	
		beanFactory = DozerUiUtils.createLabelClassBrowse(
				this, 
				"ConfigSection.beanfactory", 
				DozerUtils.getBeanFactoryInterfaceName(model),
				false); 
		
		DozerUiUtils.createLabel(this, "ConfigSection.relationshiptype");
		
		Composite multiComboComposite = toolkit.createComposite(this);
		TableWrapLayout multiComboLayout = new TableWrapLayout();
		multiComboLayout.numColumns = isMapping?3:1;
		multiComboLayout.bottomMargin = 0;
		multiComboLayout.horizontalSpacing = 5;
		multiComboLayout.leftMargin = 0;
		multiComboLayout.rightMargin = 0;
		multiComboLayout.topMargin = 0;
		multiComboComposite.setLayout(multiComboLayout);
		
		TableWrapData td = new TableWrapData();
		multiComboComposite.setLayoutData(td);
		
		relationshipType = DozerUiUtils.createCombo(
				multiComboComposite,
				"ConfigSection.relationshiptype", new String[] { "", "cumulative", "non-cumulative" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$


		DozerUiUtils.createLabel(this, "ConfigSection.stoponerrors");
		
		Composite comboComposite = toolkit.createComposite(this);
		TableWrapLayout comboLayout = new TableWrapLayout();
		comboLayout.numColumns = isMapping?9:5;
		comboLayout.bottomMargin = 0;
		comboLayout.horizontalSpacing = 5;
		comboLayout.leftMargin = 0;
		comboLayout.rightMargin = 0;
		comboLayout.topMargin = 0;
		comboComposite.setLayout(comboLayout);
		
		td = new TableWrapData();
		comboComposite.setLayoutData(td);
		
		
		stopOnErrors = DozerUiUtils.createCombo(
				comboComposite, 
				"ConfigSection.stoponerrors", new String[] { "", "true", "false" }); //$NON-NLS-1$
		
		wildcard = DozerUiUtils.createLabelCombo(
				comboComposite, 
				"ConfigSection.wildcard", new String[] { "", "true", "false" }); //$NON-NLS-1$
		
		trimStrings = DozerUiUtils.createLabelCombo(
				comboComposite, 
				"ConfigSection.trimstrings", new String[] { "", "true", "false" }); //$NON-NLS-1$
		
		if (isMapping) {
			mappingType = DozerUiUtils.createLabelCombo(
					multiComboComposite,
					"MappingSection.mappingtype", new String[] { "", "one-way", "bi-directional" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			mapNull = DozerUiUtils.createLabelCombo(
					comboComposite, 
					"MappingSection.mapnull", new String[] { "", "true", "false" }); //$NON-NLS-1$
			
			mapEmpty = DozerUiUtils.createLabelCombo(
					comboComposite, 
					"MappingSection.mapempty", new String[] { "", "true", "false" }); //$NON-NLS-1$
			
			mapId = DozerUiUtils.createLabelText(this, "MappingSection.mapid");
		}
	}

	public IObservableValue getRelationshipType() {
		return relationshipType;
	}

	public IObservableValue getStopOnErrors() {
		return stopOnErrors;
	}

	public IObservableValue getWildcard() {
		return wildcard;
	}

	public IObservableValue getTrimStrings() {
		return trimStrings;
	}

	public IObservableValue getDateFormat() {
		return dateFormat;
	}

	public IObservableValue getBeanFactory() {
		return beanFactory;
	}

	public IObservableValue getMappingType() {
		return mappingType;
	}

	public IObservableValue getMapNull() {
		return mapNull;
	}

	public IObservableValue getMapEmpty() {
		return mapEmpty;
	}

	public IObservableValue getMapId() {
		return mapId;
	}

}
