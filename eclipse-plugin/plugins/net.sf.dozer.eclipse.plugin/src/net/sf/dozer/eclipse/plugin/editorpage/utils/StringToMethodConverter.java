package net.sf.dozer.eclipse.plugin.editorpage.utils;

import java.util.List;

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jface.internal.databinding.viewers.ViewerSingleSelectionObservableValue;
import org.eclipse.jface.viewers.Viewer;

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
		
		if (methodName == null)
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