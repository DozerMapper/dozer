package org.dozer.eclipse.plugin.editorpage.pages.composites;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.Viewer;

public class ObservableSingleSelectionObject {
	private final IObservableValue value;
	private final Viewer viewer;
	
	public ObservableSingleSelectionObject(final Viewer viewer) {
		this.value = ViewersObservables.observeSingleSelection(viewer);
		this.viewer = viewer;
	}

	public IObservableValue getValue() {
		return value;
	}

	public Viewer getViewer() {
		return viewer;
	}
}
