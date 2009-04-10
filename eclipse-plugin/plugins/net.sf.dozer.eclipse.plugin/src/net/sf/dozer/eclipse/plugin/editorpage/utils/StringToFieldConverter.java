package net.sf.dozer.eclipse.plugin.editorpage.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jface.internal.databinding.swt.CComboObservableValue;
import org.eclipse.jface.internal.databinding.viewers.ViewerSingleSelectionObservableValue;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.CCombo;

public class StringToFieldConverter extends Converter {

	private Viewer viewer;
	private List<IField> existingFields;
	private IField fakeField;
	
	public StringToFieldConverter(List<IField> existingFields, Viewer viewer) {
		super(String.class, IField.class);
		this.viewer = viewer;
		this.existingFields = existingFields;
	}
	
	public void setExistingFields(List<IField> existingFields) {
		this.existingFields = existingFields;
	}
		
	public Object convert(Object fromObject) {
		String fieldName = fromObject.toString();
		
		if (fieldName == null)
			return null;
		
		for (IField field : existingFields) {
			if (field.getElementName().equals(fieldName)) {
				return field;
			}
		}

		if (fakeField != null)
			existingFields.remove(fakeField);
		
		fakeField = new SourceField(null, fieldName) {};
		existingFields.add(fakeField);
		
		viewer.refresh();
		
		return fakeField;
	}

}