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
