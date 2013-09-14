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
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jface.viewers.Viewer;

import java.util.List;

public class StringToMethodConverter extends Converter {

	private Viewer viewer;
	private List<IMethod> existingMethods;
	private IMethod fakeMethod;
	
	public StringToMethodConverter(List<IMethod> existingMethods, Viewer viewer) {
		super(String.class, IMethod.class);
		this.viewer = viewer;
		this.existingMethods = existingMethods;
	}
	
	public void setExistingMethods(List<IMethod> existingMethods) {
		this.existingMethods = existingMethods;
	}

	public Object convert(Object fromObject) {
		String methodName = fromObject.toString();
		
		if (methodName == null || "".equals(methodName))
			return null;
		
		for (IMethod method : existingMethods) {
			if (method.getElementName().equals(methodName)) {
				return method;
			}
		}
		
		if (fakeMethod != null)
			existingMethods.remove(fakeMethod);
		
		fakeMethod = new SourceMethod(null, methodName, null) {};
		
		existingMethods.add(fakeMethod);
		viewer.refresh();
		
		return fakeMethod;
	}

}