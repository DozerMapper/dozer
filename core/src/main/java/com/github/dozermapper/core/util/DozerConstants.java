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
package com.github.dozermapper.core.util;

import com.github.dozermapper.core.classmap.RelationshipType;
import com.github.dozermapper.core.factory.XMLBeanFactory;
import com.github.dozermapper.core.fieldmap.DozerField;

/**
 * Internal constants file containing a variety of constants used throughout the code base. Only intended for internal
 * use.
 */
public final class DozerConstants {

    private DozerConstants() {
    }

    public static final String CURRENT_VERSION = "6.5.2";

    public static final boolean DEFAULT_WILDCARD_POLICY = true;
    public static final boolean DEFAULT_WILDCARD_CASE_INSENSITIVE_POLICY = false;
    public static final boolean DEFAULT_ERROR_POLICY = true;
    public static final boolean DEFAULT_MAP_NULL_POLICY = true;
    public static final boolean DEFAULT_MAP_EMPTY_STRING_POLICY = true;
    public static final boolean DEFAULT_TRIM_STRINGS_POLICY = false;
    public static final RelationshipType DEFAULT_RELATIONSHIP_TYPE_POLICY = RelationshipType.CUMULATIVE;
    public static final String DEFAULT_MAPPING_FILE = "dozerBeanMapping.xml";

    public static final String XSD_NAME = "bean-mapping.xsd";
    public static final String DEBUG_SYS_PROP = "dozer.debug";// i.e)-Ddozer.debug=true
    public static final String ITERATE = "iterate";
    public static final String DEEP_FIELD_DELIMITER = ".";
    public static final String DEEP_FIELD_DELIMITER_REGEXP = "\\.";
    /**
     * Constant used in {@link DozerField#getName()} to mark the field as belonging to a {@link java.util.Map} instead of
     * an ordinary class. The name of the Key is then set in {@link DozerField#getKey()}.
     */
    public static final String SELF_KEYWORD = "this";
    public static final String CGLIB_ID = "$$EnhancerByCGLIB$$";
    public static final String BASE_CLASS = Object.class.getName();
    public static final String XML_BEAN_FACTORY = XMLBeanFactory.class.getName();

    public static final String JAVASSIST_PACKAGE = "org.javassist.tmp.";
    public static final String JAVASSIST_SYMBOL = "_$$_javassist_";
    public static final String JAVASSIST_SYMBOL_2 = "_$$_jvst";

}
