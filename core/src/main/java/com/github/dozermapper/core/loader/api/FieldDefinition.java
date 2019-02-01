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
package com.github.dozermapper.core.loader.api;

import com.github.dozermapper.core.loader.DozerBuilder;

public class FieldDefinition {

    private String value;

    private Boolean accessible;
    private String createMethod;
    private String key;
    private String mapGetMethod;
    private String mapSetMethod;
    private String getMethod;
    private String setMethod;
    private boolean iterate;

    public FieldDefinition(String value) {
        this.value = value;
    }

    public void build(DozerBuilder.FieldDefinitionBuilder builder) {
        builder.accessible(this.accessible);
        builder.createMethod(this.createMethod);

        builder.key(this.key);
        builder.mapGetMethod(this.mapGetMethod);
        builder.mapSetMethod(this.mapSetMethod);

        builder.theGetMethod(this.getMethod);
        builder.theSetMethod(this.setMethod);

        if (this.iterate) {
            builder.iterate();
        }
    }

    public FieldDefinition iterate() {
        this.iterate = true;
        return this;
    }

    public FieldDefinition accessible() {
        return accessible(true);
    }

    public FieldDefinition accessible(boolean value) {
        this.accessible = value;
        return this;
    }

    public FieldDefinition createMethod(String method) {
        this.createMethod = method;
        return this;
    }

    public FieldDefinition mapKey(String key) {
        this.key = key;
        return this;
    }

    public FieldDefinition mapMethods(String getMethod, String setMethod) {
        this.mapGetMethod = getMethod;
        this.mapSetMethod = setMethod;
        return this;
    }

    public FieldDefinition getMethod(String getMethod) {
        this.getMethod = getMethod;
        return this;
    }

    public FieldDefinition setMethod(String setMethod) {
        this.setMethod = setMethod;
        return this;
    }

    String resolve() {
        return value;
    }

}
