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
package org.dozer.eclipse.plugin.sourcepage.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.springframework.ide.eclipse.beans.ui.BeansUIUtils;
import org.springframework.ide.eclipse.core.model.ISourceModelElement;

public class SourceModelHyperlink implements IHyperlink {

	private final IRegion region;

	private final ISourceModelElement modelElement;

	/**
	 * Creates a new Java element hyperlink.
	 */
	public SourceModelHyperlink(ISourceModelElement bean, IRegion region) {
		this.region = region;
		this.modelElement = bean;
	}

	public IRegion getHyperlinkRegion() {
		return this.region;
	}

	public String getTypeLabel() {
		return null;
	}

	public String getHyperlinkText() {
		return null;
	}

	public void open() {
		BeansUIUtils.openInEditor(modelElement);
	}

}
