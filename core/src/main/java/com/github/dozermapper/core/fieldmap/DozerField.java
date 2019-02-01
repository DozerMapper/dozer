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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Only intended for internal use.
 */
public class DozerField implements Cloneable {

    private String type;
    private String name;
    private String dateFormat;
    private String theGetMethod;
    private String theSetMethod;
    private String key;
    private String mapSetMethod;
    private String mapGetMethod;
    private Boolean accessible;
    private String createMethod;
    private boolean indexed;
    private int index = -1;

    public DozerField(String name, String type) {
        this.type = type;
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    /**
     * Name of the field, which is the property name. Except if mapping a {@link java.util.Map}, the value is "this".
     *
     * @return name of field
     */
    public String getName() {
        return name;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTheGetMethod() {
        return theGetMethod;
    }

    public void setTheGetMethod(String theGetMethod) {
        this.theGetMethod = theGetMethod;
    }

    public String getTheSetMethod() {
        return theSetMethod;
    }

    public void setTheSetMethod(String theSetMethod) {
        this.theSetMethod = theSetMethod;
    }

    /**
     * Key of the field, used if mapping a {@link java.util.Map} "field", otherwise null.
     *
     * @return key of field
     */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMapGetMethod() {
        return mapGetMethod;
    }

    public void setMapGetMethod(String mapGetMethod) {
        this.mapGetMethod = mapGetMethod;
    }

    public String getMapSetMethod() {
        return mapSetMethod;
    }

    public void setMapSetMethod(String mapSetMethod) {
        this.mapSetMethod = mapSetMethod;
    }

    public Boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(Boolean isAccessible) {
        this.accessible = isAccessible;
    }

    public String getCreateMethod() {
        return createMethod;
    }

    public void setCreateMethod(String createMethod) {
        this.createMethod = createMethod;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean isIndexed) {
        this.indexed = isIndexed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCustomGetterSetterField() {
        return getTheGetMethod() != null || getTheSetMethod() != null;
    }

    public boolean isMapTypeCustomGetterSetterField() {
        return getMapGetMethod() != null || getMapSetMethod() != null;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
