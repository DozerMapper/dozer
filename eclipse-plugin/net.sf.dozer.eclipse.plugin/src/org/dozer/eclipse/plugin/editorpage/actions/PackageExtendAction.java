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
package org.dozer.eclipse.plugin.editorpage.actions;

import org.dozer.eclipse.plugin.editorpage.utils.ElementAsClassLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewer;

public class PackageExtendAction extends Action {

	private ColumnViewer columnViewer;
	
	public PackageExtendAction(String text, ImageDescriptor image, ColumnViewer columnViewer) {
		super(text, IAction.AS_CHECK_BOX);
		this.setImageDescriptor(image);
		this.columnViewer = columnViewer;
	}
	
	@Override
	public void run() {
		ElementAsClassLabelProvider labelProvider = (ElementAsClassLabelProvider)columnViewer.getLabelProvider();
		
		labelProvider.setShowShortNames(!this.isChecked());
		columnViewer.refresh();		
		notifyResult(true);
	}	

}
