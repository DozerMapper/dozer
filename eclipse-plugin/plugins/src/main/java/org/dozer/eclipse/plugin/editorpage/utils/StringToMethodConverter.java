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