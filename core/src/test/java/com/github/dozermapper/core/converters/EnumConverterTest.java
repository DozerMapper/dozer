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
package com.github.dozermapper.core.converters;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EnumConverterTest {
    @Test
    public void canConvertEnumWithOverriddenToStringSameType() {
        assertEquals(Type.ONE, new EnumConverter().convert(Type.class, Type.ONE));
        assertEquals(Type.TWO, new EnumConverter().convert(Type.class, Type.TWO));
    }

    @Test
    public void canConvertEnumWithOverriddenToStringDifferentTypes() {
        assertEquals(AlsoType.ONE, new EnumConverter().convert(AlsoType.class, Type.ONE));
        assertEquals(AlsoType.TWO, new EnumConverter().convert(AlsoType.class, Type.TWO));
    }

    public enum Type {
        ONE,
        TWO;

        @Override
        public String toString() {
            switch (this) {
                case ONE:
                    return "One (1)";
                case TWO:
                    return "Two (2)";
                default:
                    return "UNKNOWN";
            }
        }
    }

    public enum AlsoType {
        ONE,
        TWO;

        @Override
        public String toString() {
            switch (this) {
                case ONE:
                    return "(1)";
                case TWO:
                    return "(2)";
                default:
                    return "?";
            }
        }
    }
}
