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
