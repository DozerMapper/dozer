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
package com.github.dozermapper.core.functional_tests;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImmutableMappingTest extends AbstractFunctionalTest {

    private static final String TEST_STRING = "testString";
    private static final Integer TEST_INTEGER = 13;

    @Before
    public void setUp() {
        mapper = getMapper("mappings/immutable.xml");
    }

    @Test
    public void shouldMapImmutableTypes() {
        Immutable source = new Immutable(TEST_STRING);

        Immutable result = mapper.map(source, Immutable.class);

        assertEquals(TEST_STRING, result.getValue());
    }

    @Test
    public void shouldMapImmutableTypesWithInheritance() {
        ImmutableChild source = new ImmutableChild(TEST_STRING, TEST_INTEGER);

        ImmutableChild result = mapper.map(source, ImmutableChild.class);

        assertEquals(TEST_STRING, result.getValue());
        assertEquals(TEST_INTEGER, result.getChildValue());
    }

    @Test
    public void shouldMapFromMutableToImmutable() {
        MutableClass source = new MutableClass(TEST_STRING, TEST_INTEGER);

        Immutable result = mapper.map(source, Immutable.class);

        assertEquals(TEST_STRING, result.getValue());
    }

    @Test
    public void shouldMapFromImmutableToMutable() {
        Immutable source = new Immutable(TEST_STRING);

        MutableClass result = mapper.map(source, MutableClass.class);

        assertEquals(TEST_STRING, result.getStringVar());
        assertEquals(null, result.getIntegerVar());
    }

    @Test
    public void shouldInitializeUnmappedFields() {
        MutableClass source = new MutableClass(TEST_STRING, TEST_INTEGER);

        ImmutableChild result = mapper.map(source, ImmutableChild.class);

        assertEquals(TEST_STRING, result.getValue());
        assertEquals(null, result.getChildValue());
    }

    public static class Immutable {

        private final String value;

        public Immutable(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static class ImmutableChild extends Immutable {

        private final Integer childValue;

        public ImmutableChild(String value, Integer childValue) {
            super(value);
            this.childValue = childValue;
        }

        public Integer getChildValue() {
            return childValue;
        }
    }

    public static class MutableClass {

        private String stringVar;
        private Integer integerVar;

        public MutableClass() {
        }

        public MutableClass(String stringVar, Integer integerVar) {
            this.stringVar = stringVar;
            this.integerVar = integerVar;
        }

        public String getStringVar() {
            return stringVar;
        }

        public void setStringVar(String stringVar) {
            this.stringVar = stringVar;
        }

        public Integer getIntegerVar() {
            return integerVar;
        }

        public void setIntegerVar(Integer integerVar) {
            this.integerVar = integerVar;
        }
    }

}
