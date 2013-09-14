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
package org.dozer.eclipse.plugin.editorpage.imagecombo;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ImageComboViewer extends AbstractListViewer {

    /**
     * This viewer's list control if this viewer is instantiated with a imagecombo control; otherwise
     * <code>null</code>.
     * 
     * @see #ComboViewer(ImageCombo)
     */
    private ImageCombo combo;
    
    /**
     * Creates a combo viewer on a newly-created combo control under the given parent.
     * The viewer has no input, no content provider, a default label provider, 
     * no sorter, and no filters.
     *
     * @param parent the parent control
     */
    public ImageComboViewer(Composite parent) {
        this(parent, SWT.READ_ONLY | SWT.BORDER);
    }

    /**
     * Creates a combo viewer on a newly-created combo control under the given parent.
     * The combo control is created using the given SWT style bits.
     * The viewer has no input, no content provider, a default label provider, 
     * no sorter, and no filters.
     *
     * @param parent the parent control
     * @param style the SWT style bits
     */
    public ImageComboViewer(Composite parent, int style) {
        this(new ImageCombo(parent, style));
    }

    /**
     * Creates a combo viewer on the given combo control.
     * The viewer has no input, no content provider, a default label provider, 
     * no sorter, and no filters.
     *
     * @param list the combo control
     */
    public ImageComboViewer(ImageCombo list) {
        this.combo = list;
        hookControl(list);
    }
    
    @Override
	protected void listAdd(String string, int index) {
        combo.add(string, null, index);
    }

    @Override
	protected void listSetItem(int index, String string) {
        combo.setItem(index, string, null);
    }

    @Override
	protected int[] listGetSelectionIndices() {
        return new int[] { combo.getSelectionIndex() };
    }

    @Override
	protected int listGetItemCount() {
        return combo.getItemCount();
    }

    @Override
	protected void listSetItems(String[] labels) {
        combo.setItems(labels);
    }

    @Override
	protected void listRemoveAll() {
        combo.removeAll();
    }

    @Override
	protected void listRemove(int index) {
        combo.remove(index);
    }

    /* (non-Javadoc)
     * Method declared on Viewer.
     */
    @Override
	public Control getControl() {
        return combo;
    }

    /**
     * Returns this list viewer's list control. If the viewer was not created on
	 * a Combo control, some kind of unchecked exception is thrown.
     *
     * @return the list control
     */
    public ImageCombo getCombo() {
    	Assert.isNotNull(combo);
        return combo;
    }
    
    /*
     * Do nothing -- combos only display the selected element, so there is no way
     * we can ensure that the given element is visible without changing the selection.
     * Method defined on StructuredViewer.
     */
    @Override
	public void reveal(Object element) {
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.AbstractListViewer#listSetSelection(int[])
     */
    @Override
	protected void listSetSelection(int[] ixs) {
       for (int idx = 0; idx < ixs.length; idx++) {
           combo.select(ixs[idx]);
       }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.AbstractListViewer#listDeselectAll()
     */
    @Override
	protected void listDeselectAll() {
        combo.deselectAll();
        combo.clearSelection();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.AbstractListViewer#listShowSelection()
     */
    @Override
	protected void listShowSelection() {
    }

}
