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
package org.dozer.eclipse.plugin.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.UIPlugin;
import org.eclipse.wst.xml.core.internal.Logger;

public class NewMappingFileWizard extends Wizard implements INewWizard {

	private IStructuredSelection selection;
	private NewMappingConfigFilePage mainPage;

	@Override
	public boolean performFinish() {
		try {
			createAndOpenNewConfig();
		} catch (PartInitException e) {
			Logger.logException(e);
		}

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		setNeedsProgressMonitor(true);
		setWindowTitle("Create new Dozer Mapping file");
		//setDefaultPageImageDescriptor(BeansUIImages.DESC_WIZ_CONFIG);		
	}

	@Override
	public void addPages() {
		super.addPages();
		mainPage = new NewMappingConfigFilePage("beansNewConfigPage", selection);
		addPage(mainPage);
	}
	
	private void createAndOpenNewConfig() throws PartInitException {
		IFile file = mainPage.createConfig();
		IDE.openEditor(UIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true);
	}	
}
