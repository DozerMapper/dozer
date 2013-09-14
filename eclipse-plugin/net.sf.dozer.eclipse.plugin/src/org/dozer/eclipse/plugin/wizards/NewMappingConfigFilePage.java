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
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.wst.sse.core.internal.encoding.CommonEncodingPreferenceNames;
import org.eclipse.wst.xml.core.internal.XMLCorePlugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

@SuppressWarnings("restriction")
public class NewMappingConfigFilePage extends WizardNewFileCreationPage {

	private Combo dozerVersionCombo;
	
	public NewMappingConfigFilePage(String pageName,
			IStructuredSelection selection) {
		super(pageName, selection);
		setTitle("Create new Dozer Mapping file");
		setDescription("Create new Dozer Mapping file");
		setFileName("dozerMapping.xml");
	}
	
	@Override
	protected void createAdvancedControls(Composite parent) {
		Composite dozerSelectorLevel = new Composite(parent, SWT.NONE);
		
		GridLayout gl = new GridLayout(2, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		dozerSelectorLevel.setLayout(gl);
		dozerSelectorLevel.setFont(parent.getFont());
		
		Label lbl = new Label(dozerSelectorLevel, SWT.NONE);
		lbl.setText("Compatibility of the new mapping file:");
		
		dozerVersionCombo = new Combo(dozerSelectorLevel, SWT.DROP_DOWN | SWT.READ_ONLY);
		dozerVersionCombo.add("Dozer 4 (DTD)");
		dozerVersionCombo.add("Dozer 5 (XSD)");
		
		dozerVersionCombo.select(1);
		super.createAdvancedControls(parent);
	}
	
	protected InputStream createXMLDocument() throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		String charSet = getUserPreferredCharset();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, charSet));
		writer.println("<?xml version=\"1.0\" encoding=\"" + charSet + "\"?>"); //$NON-NLS-1$ //$NON-NLS-2$
		
		//DTD
		if (dozerVersionCombo.getSelectionIndex() == 0) {
			writer.println("<!DOCTYPE mappings PUBLIC \"-//DOZER//DTD MAPPINGS//EN\" \"http://dozer.sourceforge.net/dtd/dozerbeanmapping.dtd\">");
			writer.println("<mappings>");

		//XSD
		} else if (dozerVersionCombo.getSelectionIndex() == 1) {
			writer.println("<mappings xmlns=\"http://dozer.sourceforge.net\"");
			writer.println("      xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			writer.println("      xsi:schemaLocation=\"http://dozer.sourceforge.net http://dozer.sourceforge.net/schema/beanmapping.xsd\">");
		}

		writer.println("</mappings>");
		writer.flush();
		outputStream.close();

		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				outputStream.toByteArray());
		return inputStream;
	}
	
	public IFile createConfig() {
		IFile file = createNewFile();
		return file;
	}

	@Override
	protected InputStream getInitialContents() {
		try {
			return createXMLDocument();
		}
		catch (Exception e) {
		}
		return null;
	}

	private String getUserPreferredCharset() {
		Preferences preference = XMLCorePlugin.getDefault()
				.getPluginPreferences();
		String charSet = preference
				.getString(CommonEncodingPreferenceNames.OUTPUT_CODESET);
		return charSet;
	}
}
