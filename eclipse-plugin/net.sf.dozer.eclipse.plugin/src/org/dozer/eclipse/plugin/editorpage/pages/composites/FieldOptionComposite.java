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

public class FieldOptionComposite extends Composite {

	//Ui elements of dozer global configs
	private IObservableValue relationshipType;
	private IObservableValue removeOrphans;
	private IObservableValue mappingType;
	private IObservableValue mapId;
	private IObservableValue copyByReference;
	private IObservableValue customConverter;
	private IObservableValue customConverterId;
	private IObservableValue customConverterParam;
	//private IObservableValue excluded;
	
	private boolean excluded;

	public FieldOptionComposite(Composite parent, FormToolkit toolkit, IDOMModel model, boolean isExcluded) {
		super(parent, SWT.NULL);
	
		excluded = isExcluded;
		//FIXME otherwise its transparent!?!?
		this.setBackground(new Color(Display.getCurrent(), 255,255,255));
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		this.setLayout(layout);
		
		if (isExcluded) {
			mappingType = DozerUiUtils.createLabelCombo(
					this,
					"MappingSection.mappingtype", new String[] { "", "one-way", "bi-directional" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$			
		} else {
			DozerUiUtils.createLabel(this, "ConfigSection.relationshiptype");
			
			Composite multiComboComposite = toolkit.createComposite(this);
			TableWrapLayout multiComboLayout = new TableWrapLayout();
			multiComboLayout.numColumns = 3;
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
	
			mappingType = DozerUiUtils.createLabelCombo(
					multiComboComposite,
					"MappingSection.mappingtype", new String[] { "", "one-way", "bi-directional" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			DozerUiUtils.createLabel(this, "FieldSection.removeOrphans");
					
			Composite multiComboComposite2 = toolkit.createComposite(this);
			TableWrapLayout multiComboLayout2 = new TableWrapLayout();
			multiComboLayout2.numColumns = 3;
			multiComboLayout2.bottomMargin = 0;
			multiComboLayout2.horizontalSpacing = 5;
			multiComboLayout2.leftMargin = 0;
			multiComboLayout2.rightMargin = 0;
			multiComboLayout2.topMargin = 0;
			multiComboComposite2.setLayout(multiComboLayout2);
			
			TableWrapData td2 = new TableWrapData();
			multiComboComposite.setLayoutData(td2);
			
			removeOrphans = DozerUiUtils.createCombo(
					multiComboComposite2,
					"FieldSection.removeOrphans", new String[] { "", "true", "false" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			copyByReference = DozerUiUtils.createLabelCombo(
					multiComboComposite2,
					"FieldSection.copyByRef", new String[] { "", "true", "false" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					
			mapId = DozerUiUtils.createLabelText(this, "MappingSection.mapid");
			
			customConverter = DozerUiUtils.createLabelClassBrowse(
					this, 
					"FieldSection.customConverter", 
					DozerUtils.getCustomConverterInterfaceName(model),
					false); 
			customConverterId = DozerUiUtils.createLabelText(this, "FieldSection.customConverterId");
			customConverterParam = DozerUiUtils.createLabelText(this, "FieldSection.customConverterParam");
		}
		
		//excluded = DozerUiUtils.createLabelCheckbox(this, "FieldSection.isExcluded");
	}

	public IObservableValue getRelationshipType() {
		return relationshipType;
	}

	public IObservableValue getMappingType() {
		return mappingType;
	}

	public IObservableValue getMapId() {
		return mapId;
	}

	public IObservableValue getRemoveOrphans() {
		return removeOrphans;
	}

	public IObservableValue getCopyByReference() {
		return copyByReference;
	}

	public IObservableValue getCustomConverter() {
		return customConverter;
	}

	public IObservableValue getCustomConverterId() {
		return customConverterId;
	}

	public IObservableValue getCustomConverterParam() {
		return customConverterParam;
	}

	public boolean isExcluded() {
		return excluded;
	}
	
	/*public IObservableValue getExcluded() {
		return excluded;
	}*/

}
