/*
 * Copyright 2005-2024 Dozer Project
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
package com.github.dozermapper.core.loader.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Deprecated
public abstract class SimpleElementReader implements ElementReader {

    public String getAttribute(Element element, String attribute) {
        return element.getAttribute(attribute).trim();
    }

    public String getNodeValue(Element element) {
        Node child = element.getFirstChild();
        if (child == null) {
            return "";
        }
        String nodeValue = child.getNodeValue();
        if (nodeValue != null) {
            return nodeValue.trim();
        } else {
            return "";
        }
    }
}
