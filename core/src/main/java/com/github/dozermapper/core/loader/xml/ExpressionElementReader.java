/*
 * Copyright 2005-2018 Dozer Project
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

import com.github.dozermapper.core.el.ELEngine;

@Deprecated
public class ExpressionElementReader extends SimpleElementReader {

    private ELEngine elEngine;

    public ExpressionElementReader(ELEngine elEngine) {
        this.elEngine = elEngine;
    }

    @Override
    public String getAttribute(Element element, String attribute) {
        String expression = super.getAttribute(element, attribute);
        return elEngine.resolve(expression);
    }

    @Override
    public String getNodeValue(Element element) {
        String expression = super.getNodeValue(element);
        return elEngine.resolve(expression);
    }
}
