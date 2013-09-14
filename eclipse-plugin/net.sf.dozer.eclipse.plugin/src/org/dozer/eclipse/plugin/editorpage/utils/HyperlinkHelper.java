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
package org.dozer.eclipse.plugin.editorpage.utils;

import org.dozer.eclipse.plugin.editorpage.DozerFormEditor;
import org.dozer.eclipse.plugin.editorpage.Messages;
import org.dozer.eclipse.plugin.editorpage.pages.DozerConfigurationEditorPage;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.EditorPart;

public class HyperlinkHelper implements IHyperlinkListener {

	private FormPage formPage;
	private FormToolkit toolkit;
	
	public HyperlinkHelper(FormPage formPage, FormToolkit toolkit) {
		this.formPage = formPage;
		this.toolkit = toolkit;
	}
	
	public EditorPart getEditor() {
		return formPage.getEditor();
	}
	
	public FormText createFormText(Composite parent, String messagePrefix) {
		FormText ft = toolkit.createFormText(parent, true);
		try {
			ft.setText(Messages.getString(messagePrefix), true, false);
		} catch (SWTException e) {
			ft.setText(e.getMessage(), false, false);
		}
		ft.addHyperlinkListener(this);
		
		return ft;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.events.HyperlinkListener#linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkEntered(HyperlinkEvent e) {
		IStatusLineManager mng = getEditor().getEditorSite().getActionBars().getStatusLineManager();
		mng.setMessage(e.getLabel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.events.HyperlinkListener#linkExited(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkExited(HyperlinkEvent e) {
		IStatusLineManager mng = getEditor().getEditorSite().getActionBars().getStatusLineManager();
		mng.setMessage(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
	 */
	public void linkActivated(HyperlinkEvent e) {
		String href = (String) e.getHref();
		if ("jumpToGlobalPage".equals(href)) {
			DozerFormEditor dozerEditor = ((DozerFormEditor)formPage.getEditor());
			
			dozerEditor.setActivePage(DozerConfigurationEditorPage.PAGE_ID);
		}
	}

}
