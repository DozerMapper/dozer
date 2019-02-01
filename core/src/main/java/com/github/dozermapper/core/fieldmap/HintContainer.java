/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.fieldmap;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.util.MappingUtils;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Only intended for internal use.
 */
public class HintContainer {
    private String hintName;
    private List<Class<?>> hints;
    private final BeanContainer beanContainer;

    public HintContainer(BeanContainer beanContainer) {
        this.beanContainer = beanContainer;
    }

    public Class<?> getHint() {
        Class<?> result;
        if (hasMoreThanOneHint()) {
            return null;
        } else {
            result = getHints().get(0);
        }
        return result;
    }

    public Class<?> getHint(int index) {
        return getHints().get(index);
    }

    public boolean hasMoreThanOneHint() {
        return getHints().size() > 1;
    }

    public List<Class<?>> getHints() {
        if (hints == null) {
            List<Class<?>> list = new ArrayList<>();
            StringTokenizer st = new StringTokenizer(this.hintName, ",");
            while (st.hasMoreElements()) {
                String theHintName = st.nextToken().trim();

                Class<?> clazz = MappingUtils.loadClass(theHintName, beanContainer);
                list.add(clazz);
            }
            hints = list;
        }
        return hints;
    }

    //TODO: Refactor/Relocate.  This method doesn't seem to belong in this class
    public Class<?> getHint(Class<?> clazz, List<Class<?>> clazzHints) {
        List<Class<?>> hints = getHints();
        int hintsSize = hints.size();
        if (hintsSize == 1) {
            return getHint();
        }
        // validate sizes
        if (clazzHints.size() != hintsSize) {
            MappingUtils
                    .throwMappingException("When using multiple source and destination hints there must be exactly the same number of hints on the source and the destination.");
        }
        int count = 0;
        String myClazName = MappingUtils.getRealClass(clazz, beanContainer).getName();
        int size = clazzHints.size();
        for (int i = 0; i < size; i++) {
            Class<?> element = clazzHints.get(i);
            if (element.getName().equals(myClazName)) {
                return hints.get(count);
            }
            count++;
        }
        return clazz;
    }

    public void setHintName(String hintName) {
        this.hintName = hintName;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
