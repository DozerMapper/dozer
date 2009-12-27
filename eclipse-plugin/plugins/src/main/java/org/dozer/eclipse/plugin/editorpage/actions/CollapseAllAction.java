package org.dozer.eclipse.plugin.editorpage.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;

public class CollapseAllAction extends Action {
	
	private boolean isCollapseAll = false;
	private TreeViewer treeViewer;
	
	public CollapseAllAction(String text, ImageDescriptor image, TreeViewer treeViewer, boolean isCollapseAll) {
		super(text, image);
		this.treeViewer = treeViewer;
		this.isCollapseAll = isCollapseAll;
	}
	
	@Override
	public void run() {
		if (isCollapseAll)
			treeViewer.collapseAll();
		else
			treeViewer.expandAll();
		notifyResult(true);
	}	
	
}
