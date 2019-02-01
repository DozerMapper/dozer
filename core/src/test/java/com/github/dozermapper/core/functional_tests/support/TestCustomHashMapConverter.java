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
package com.github.dozermapper.core.functional_tests.support;

import java.util.HashMap;

import com.github.dozermapper.core.CustomConverter;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.vo.BaseTestObject;
import com.github.dozermapper.core.vo.TestCustomConverterHashMapObject;
import com.github.dozermapper.core.vo.TestCustomConverterHashMapPrimeObject;
import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.TestObjectPrime;

public class TestCustomHashMapConverter implements CustomConverter {

    public Object convert(Object destination, Object source, Class<?> destClass, Class<?> sourceClass) {

        if (source instanceof TestCustomConverterHashMapObject) {
            TestCustomConverterHashMapPrimeObject dest = null;
            TestCustomConverterHashMapObject testCustomConverterHashMapObject = (TestCustomConverterHashMapObject)source;
            if (destination == null) {
                dest = new TestCustomConverterHashMapPrimeObject();
            } else {
                dest = (TestCustomConverterHashMapPrimeObject)destination;
            }
            HashMap<Object, BaseTestObject> map = new HashMap<>();
            map.put("object1", testCustomConverterHashMapObject.getTestObject());
            map.put("object2", testCustomConverterHashMapObject.getTestObjectPrime());
            dest.setTestObjects(map);
            return dest;
        } else if (source instanceof TestCustomConverterHashMapPrimeObject) {
            TestCustomConverterHashMapObject dest = null;

            TestCustomConverterHashMapPrimeObject testCustomConverterHashMapPrimeObject = (TestCustomConverterHashMapPrimeObject)source;
            if (destination == null) {
                dest = new TestCustomConverterHashMapObject();
            } else {
                dest = (TestCustomConverterHashMapObject)destination;
            }
            dest.setTestObject((TestObject)testCustomConverterHashMapPrimeObject.getTestObjects().get("object1"));
            dest.setTestObjectPrime((TestObjectPrime)testCustomConverterHashMapPrimeObject.getTestObjects().get("object2"));
            return dest;
        } else {
            throw new MappingException("Converter TestCustomHashMapConverter used incorrectly. Arguments passed in were:" + destination
                                       + " and " + source);
        }
    }

}
