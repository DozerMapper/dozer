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

import org.eclipse.core.dom.utils.DOMUtils;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.w3c.dom.Element;

public class ElementAsClassLabelProvider extends LabelProvider implements ILabelProvider, ITableLabelProvider {
	
	private boolean showShortNames = true;
	
	public boolean isShowShortNames() {
		return showShortNames;
	}

	public void setShowShortNames(boolean showShortNames) {
		this.showShortNames = showShortNames;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof Element))
			return null;
		
		Element node = (Element)element;
		return DozerUiUtils.getInstance().getImageFromClassName(getColumnText(node, columnIndex));
	}

	public String getColumnText(Object element, int columnIndex) {
		if (!(element instanceof Element))
			return null;
		
		Element node = (Element)element;
		String className = getColumnText(node, columnIndex);
		
		if (showShortNames)
			return getShortClassName(className);
		else
			return className;
	}	
	
	protected String getColumnText(Element node, int columnIndex) {
		return DOMUtils.getTextContent(node);
	}
	
	protected String getShortClassName(String fullClassName) {
		return DozerUtils.getShortClassName(fullClassName);
	}
	
	@Override
	public String getText(Object element) {
		if (!(element instanceof Element))
			return null;
		
		//FIXME
		String retVal = "";
		for(int i = 0; i<10; i++) {
			String s = this.getColumnText(element, i);
			if (s == null)
				break;
			retVal += s;
		}
		
		return retVal;
	}
	
}
