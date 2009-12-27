package org.dozer.eclipse.plugin.editorpage.utils;

import org.w3c.dom.Element;

public class MappingUtils {

	public static boolean isOneWay(Element fieldNode) {
		return "one-way".equals(fieldNode.getAttributes().getNamedItem("type")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	public static boolean useCustomConverter(Element fieldNode) {
		return (fieldNode.getAttributes().getNamedItem("custom-converter") != null || 
				fieldNode.getAttributes().getNamedItem("custom-converter-id") != null); //$NON-NLS-1$ 
	}
	public static boolean isCopyByRef(Element fieldNode) {
		return (fieldNode.getAttributes().getNamedItem("copy-by-reference") != null && 
				"true".equals(fieldNode.getAttributes().getNamedItem("copy-by-reference").getNodeValue())); //$NON-NLS-1$ //$NON-NLS-2$ 
	}
	public static boolean isExcluded(Element fieldNode) {
		return "field-exclude".equals(fieldNode.getNodeName()); //$NON-NLS-1$
	}
	
}
