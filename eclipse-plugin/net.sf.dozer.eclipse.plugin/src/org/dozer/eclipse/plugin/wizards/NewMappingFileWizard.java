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
