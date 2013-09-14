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

import org.dozer.eclipse.plugin.editorpage.utils.DozerUiUtils;
import org.dozer.eclipse.plugin.editorpage.utils.DozerUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class CustomConverterAddDialog extends TrayDialog {

	public class CustomConverterData {
		private String customConverterName;
		private String classA;
		private String classB;
		
		public String getCustomConverterName() {
			return customConverterName;
		}
		public void setCustomConverterName(String customConverterName) {
			this.customConverterName = customConverterName;
		}
		public String getClassA() {
			return classA;
		}
		public void setClassA(String classA) {
			this.classA = classA;
		}
		public String getClassB() {
			return classB;
		}
		public void setClassB(String classB) {
			this.classB = classB;
		}
		
	}
	
	private IDOMModel model;
	private DataBindingContext dataBindingContext = new DataBindingContext();
	private CustomConverterData data = new CustomConverterData();
	
	public CustomConverterAddDialog(Shell shell, IDOMModel model) {
		super(shell);
		
		this.model = model;
		this.setHelpAvailable(false);
	}



	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridData gridData = new GridData();
	    gridData.widthHint = 500;
	    composite.setLayoutData(gridData);
		
		TableWrapLayout compositeLayout = new TableWrapLayout();
		compositeLayout.numColumns = 2;
		compositeLayout.bottomMargin = 0;
		compositeLayout.horizontalSpacing = 5;
		compositeLayout.leftMargin = 0;
		compositeLayout.rightMargin = 0;
		compositeLayout.topMargin = 0;
		composite.setLayout(compositeLayout);
		
		this.getShell().setText("Add custom converter");
		
		//FIXME thats way too ugly. have to figure out how this works better
		FormToolkit oldToolkit = DozerUiUtils.getToolKit();
		FormToolkit toolkit = new FormToolkit(new FormColors(parent.getDisplay()));
		toolkit.setBackground(parent.getBackground());
		DozerUiUtils.setToolKit(toolkit);
		
		IObservableValue customConverter = DozerUiUtils.createLabelClassBrowse(
				composite, 
				"ConfigSection.customconverters_browsecc",
				DozerUtils.getCustomConverterInterfaceName(model),
				false);
		
		IObservableValue fromClass = DozerUiUtils.createLabelClassBrowse(
				composite, 
				"ConfigSection.customconverters_browsefrom",
				null,
				false);
		
		IObservableValue toClass = DozerUiUtils.createLabelClassBrowse(
				composite, 
				"ConfigSection.customconverters_browseto",
				null,
				false);	
		
		dataBindingContext.bindValue(
				customConverter,
				PojoObservables.observeValue(data, "customConverterName"),
				null,
				null);
		dataBindingContext.bindValue(
				fromClass,
				PojoObservables.observeValue(data, "classA"),
				null,
				null);
		dataBindingContext.bindValue(
				toClass,
				PojoObservables.observeValue(data, "classB"),
				null,
				null);	
		
		
		//FIXME thats way too ugly. have to figure out how this works better
		DozerUiUtils.setToolKit(oldToolkit);
		
		return composite;
	}
	
	public CustomConverterData getData() {
		return data;
	}

}