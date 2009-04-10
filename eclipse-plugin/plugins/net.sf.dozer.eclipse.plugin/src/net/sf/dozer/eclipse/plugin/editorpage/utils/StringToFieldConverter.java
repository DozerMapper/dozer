package net.sf.dozer.eclipse.plugin.editorpage.utils;

import java.util.List;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jface.viewers.Viewer;

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