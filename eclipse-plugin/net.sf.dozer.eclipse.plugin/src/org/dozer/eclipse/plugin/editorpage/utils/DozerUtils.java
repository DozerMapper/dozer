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

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

public class DozerUtils {

	public static String getBeanFactoryInterfaceName(IDOMModel model) {    
		String modelId = model.getContentTypeIdentifier();
		
		if ("org.dozer.eclipse.plugin.contenttype.dozerMapping4".equals(modelId))
			return "org.dozer.util.mapping.BeanFactoryIF";
		else if ("org.dozer.eclipse.plugin.contenttype.dozerMapping5".equals(modelId))
			return "org.dozer.util.mapping.BeanFactory";
		
		return null;
	}

	public static String getCustomConverterInterfaceName(IDOMModel model) {
		String modelId = model.getContentTypeIdentifier();
		
		if ("org.dozer.eclipse.plugin.contenttype.dozerMapping4".equals(modelId))
			return "org.dozer.util.mapping.converters.CustomConverter";
		else if ("org.dozer.eclipse.plugin.contenttype.dozerMapping5".equals(modelId))
			return "org.dozer.CustomConverter";
		
		return null;
	}
	
    /**
     * Copied from org.apache.commons.lang.ClassUtils
     * 
     * <p>Gets the class name minus the package name from a String.</p>
     *
     * <p>The string passed in is assumed to be a class name - it is not checked.</p>
     *
     * @param className  the className to get the short name for
     * @return the class name of the class without the package name or an empty string
     */
    public static String getShortClassName(String className) {
        if (className == null) {
            return "";
        }
        if (className.length() == 0) {
            return "";
        }

        int lastDotIdx = className.lastIndexOf('.');
        int innerIdx = className.indexOf(
                "$", lastDotIdx == -1 ? 0 : lastDotIdx + 1);
        String out = className.substring(lastDotIdx + 1);
        if (innerIdx != -1) {
            out = out.replace('$', '.');
        }
        return out;
    }
	

}
