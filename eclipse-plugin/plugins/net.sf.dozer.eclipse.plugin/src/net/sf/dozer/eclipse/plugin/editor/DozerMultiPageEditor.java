package net.sf.dozer.eclipse.plugin.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;

public class DozerMultiPageEditor extends XMLMultiPageEditorPart {

	protected void createPages() {
		super.createPages();
		createDozerMappingPage();		
	}
	
	protected void createDozerMappingPage() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		/*GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 2;

		Button fontButton = new Button(composite, SWT.NONE);
		GridData gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 2;
		fontButton.setLayoutData(gd);
		fontButton.setText("Change Font...");
		
		fontButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				
			}
		});*/

		int index = addPage(composite);
		setPageText(index, "Mapping Wizard");
	}
}
