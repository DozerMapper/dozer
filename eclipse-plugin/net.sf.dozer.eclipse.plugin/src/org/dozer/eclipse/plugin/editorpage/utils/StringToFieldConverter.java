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

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.internal.core.SourceField;
import org.eclipse.jface.viewers.Viewer;

import java.util.List;

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
		
		if (fieldName == null || "".equals(fieldName))
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