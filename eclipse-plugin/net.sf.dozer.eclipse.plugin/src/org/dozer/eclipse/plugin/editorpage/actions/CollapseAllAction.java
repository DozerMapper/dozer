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
