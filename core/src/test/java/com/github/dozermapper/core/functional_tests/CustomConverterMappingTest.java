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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.functional_tests.support.StringAppendCustomConverter;
import com.github.dozermapper.core.vo.AnotherTestObject;
import com.github.dozermapper.core.vo.AnotherTestObjectPrime;
import com.github.dozermapper.core.vo.ArrayCustConverterObj;
import com.github.dozermapper.core.vo.ArrayCustConverterObjPrime;
import com.github.dozermapper.core.vo.Bus;
import com.github.dozermapper.core.vo.Car;
import com.github.dozermapper.core.vo.CustomDoubleObject;
import com.github.dozermapper.core.vo.CustomDoubleObjectIF;
import com.github.dozermapper.core.vo.Moped;
import com.github.dozermapper.core.vo.SimpleObj;
import com.github.dozermapper.core.vo.SimpleObjPrime2;
import com.github.dozermapper.core.vo.TestCustomConverterHashMapObject;
import com.github.dozermapper.core.vo.TestCustomConverterHashMapPrimeObject;
import com.github.dozermapper.core.vo.TestCustomConverterObject;
import com.github.dozermapper.core.vo.TestCustomConverterObjectPrime;
import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.TestObjectPrime;
import com.github.dozermapper.core.vo.Van;
import com.github.dozermapper.core.vo.map.CustomMap;
import com.github.dozermapper.core.vo.map.MapToProperty;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CustomConverterMappingTest extends AbstractFunctionalTest {

    @Test
    public void testSimpleCustomConverter() {
        mapper = getMapper("mappings/simpleCustomConverter.xml");
        SimpleObj src = newInstance(SimpleObj.class);
        src.setField1(String.valueOf(System.currentTimeMillis()));

        SimpleObjPrime2 dest = mapper.map(src, SimpleObjPrime2.class);

        // Custom converter specified for the field1 mapping, so verify custom converter was actually used
        assertNotNull("dest field1 should not be null", dest.getField1Prime());
        StringTokenizer st = new StringTokenizer(dest.getField1Prime(), "-");
        assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
        String token1 = st.nextToken();
        assertEquals("1st portion of dest field1 value should equal src field value", src.getField1(), token1);
        String token2 = st.nextToken();
        assertEquals("dest field1 value should have been appended to by the cust converter",
                     StringAppendCustomConverter.APPENDED_VALUE, token2);
    }

    // Defect #1728385
    @Test
    public void testSimpleCustomConverter_ImplicitMapping() {
        mapper = getMapper("mappings/simpleCustomConverter.xml");

        AnotherTestObject src = newInstance(AnotherTestObject.class);
        src.setField3(String.valueOf(System.currentTimeMillis()));

        AnotherTestObjectPrime dest = mapper.map(src, AnotherTestObjectPrime.class);

        // Custom converter specified for the field mapping, so verify custom converter was actually used
        assertNotNull("dest field should not be null", dest.getField3());
        StringTokenizer st = new StringTokenizer(dest.getField3(), "-");
        assertEquals("dest field value should contain a hyphon", 2, st.countTokens());
        String token1 = st.nextToken();
        assertEquals("1st portion of dest field value should equal src field value", src.getField3(), token1);
        String token2 = st.nextToken();
        assertEquals("dest field value should have been appended to by the cust converter", StringAppendCustomConverter.APPENDED_VALUE,
                     token2);
    }

    @Test
    public void testSimpleCustomConverter_ImplicitMappingWithInheritance() {
        mapper = getMapper("mappings/simpleCustomConverter.xml");

        Car car = newInstance(Car.class);
        Van van = mapper.map(car, Van.class);
        assertEquals("defaultValueSetByCustomConverter", van.getName());
        // map back
        Car carDest = mapper.map(van, Car.class);
        assertEquals("defaultValueSetByCustomConverter", carDest.getName());

        // test that we get customconverter even though it wasn't defined in the mapping file
        Moped moped = newInstance(Moped.class);
        Bus bus = mapper.map(moped, Bus.class);
        assertEquals("defaultValueSetByCustomConverter", bus.getName());

        // map back
        Moped mopedDest = mapper.map(bus, Moped.class);
        assertEquals("defaultValueSetByCustomConverter", mopedDest.getName());
    }

    @Test
    public void testSimpleCustomConverter_NullSrcValue() {
        // Test that custom converter gets invoked even if the src field value is NULL
        mapper = getMapper("mappings/simpleCustomConverter.xml");
        SimpleObj src = newInstance(SimpleObj.class);
        src.setField1(null);

        SimpleObjPrime2 dest = mapper.map(src, SimpleObjPrime2.class);

        // Custom converter specified for the field1 mapping, so verify custom converter was actually used
        assertNotNull("dest field1 should not be null", dest.getField1Prime());
        StringTokenizer st = new StringTokenizer(dest.getField1Prime(), "-");
        assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
        String token1 = st.nextToken();
        assertEquals("dest field1 value should contain the explicit null string", "null", token1);
        String token2 = st.nextToken();
        assertEquals("dest field1 value should have been appended to by the cust converter",
                     StringAppendCustomConverter.APPENDED_VALUE, token2);
    }

    @Test
    public void testArrayToStringCustomConverter() {
        // Test that custom converter is used when src is an Array and dest is a String
        mapper = getMapper("mappings/arrayToStringCustomConverter.xml");
        SimpleObj simple = newInstance(SimpleObj.class);
        simple.setField1(String.valueOf(System.currentTimeMillis()));

        ArrayCustConverterObj src = newInstance(ArrayCustConverterObj.class);
        src.setField1(new SimpleObj[] {simple});

        ArrayCustConverterObjPrime dest = mapper.map(src, ArrayCustConverterObjPrime.class);

        // Custom converter specified for the field1 mapping, so verify custom converter was actually used
        assertNotNull("dest field1 should not be null", dest.getField1Prime());
        StringTokenizer st = new StringTokenizer(dest.getField1Prime(), "-");
        assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
        st.nextToken();
        String token2 = st.nextToken();
        assertEquals("dest field1 value should have been appended to by the cust converter",
                     StringAppendCustomConverter.APPENDED_VALUE, token2);
    }

    @Test
    public void testCustomConverterMapping() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        TestCustomConverterObject obj = newInstance(TestCustomConverterObject.class);
        CustomDoubleObjectIF doub = newInstance(CustomDoubleObject.class);
        doub.setTheDouble(15);
        CustomDoubleObjectIF doub2 = newInstance(CustomDoubleObject.class);
        doub2.setTheDouble(15);
        obj.setAttribute(doub);

        Collection<CustomDoubleObjectIF> list = newInstance(ArrayList.class);
        list.add(doub2);

        obj.setNames(list);

        TestCustomConverterObjectPrime dest = mapper.map(obj, TestCustomConverterObjectPrime.class);

        assertEquals("Custom Converter failed", dest.getDoubleAttribute().doubleValue() + "", "15.0");
        assertEquals("Custom Converter failed", ((Double)dest.getNames().iterator().next()).doubleValue() + "", "15.0");

        TestCustomConverterObjectPrime objp = newInstance(TestCustomConverterObjectPrime.class);

        objp.setDoubleAttribute(new Double(15));

        Collection<Double> list2 = newInstance(ArrayList.class);
        list2.add(new Double(10));
        objp.setNames(list2);

        TestCustomConverterObject destp = mapper.map(objp, TestCustomConverterObject.class);

        assertEquals("Custom Converter failed", destp.getAttribute().getTheDouble() + "", "15.0");
        assertEquals("Custom Converter failed", ((CustomDoubleObjectIF)destp.getNames().iterator().next()).getTheDouble() + "", "10.0");

        destp.getAttribute().setName("testName");

        // pass by reference
        mapper.map(objp, destp);

        assertEquals("Custom Converter failed", destp.getAttribute().getTheDouble() + "", "15.0");
        assertEquals("testName", destp.getAttribute().getName());

        // test primitive double
        TestCustomConverterObjectPrime prime = newInstance(TestCustomConverterObjectPrime.class);
        prime.setPrimitiveDoubleAttribute(25.00);
        TestCustomConverterObject obj2 = mapper.map(prime, TestCustomConverterObject.class);
        CustomDoubleObjectIF customDouble = obj2.getPrimitiveDoubleAttribute();
        assertNotNull(customDouble);
        assertTrue(prime.getPrimitiveDoubleAttribute() == obj2.getPrimitiveDoubleAttribute().getTheDouble());

        // test conversion in the other direction
        prime = mapper.map(obj2, TestCustomConverterObjectPrime.class);
        assertTrue(prime.getPrimitiveDoubleAttribute() == obj2.getPrimitiveDoubleAttribute().getTheDouble());

    }

    @Test
    public void testCustomConverterWithPrimitive() {
        // test primitive double
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        TestCustomConverterObjectPrime prime = newInstance(TestCustomConverterObjectPrime.class);
        prime.setPrimitiveDoubleAttribute(25.00);
        prime.setDoubleAttribute(new Double(30.00));
        TestCustomConverterObject obj2 = mapper.map(prime, TestCustomConverterObject.class);
        CustomDoubleObjectIF customDouble = obj2.getPrimitiveDoubleAttribute();
        assertNotNull(customDouble);
        assertTrue(prime.getPrimitiveDoubleAttribute() == obj2.getPrimitiveDoubleAttribute().getTheDouble());

        // test conversion in the other direction
        prime = mapper.map(obj2, TestCustomConverterObjectPrime.class);
        assertTrue(prime.getPrimitiveDoubleAttribute() == obj2.getPrimitiveDoubleAttribute().getTheDouble());

    }

    @Test
    public void testCustomConverterHashMapMapping() {
        mapper = getMapper("mappings/testDozerBeanMapping.xml");
        TestCustomConverterHashMapObject testCustomConverterHashMapObject = newInstance(TestCustomConverterHashMapObject.class);
        TestObject to = newInstance(TestObject.class);
        to.setOne("one");
        testCustomConverterHashMapObject.setTestObject(to);
        TestObjectPrime top = newInstance(TestObjectPrime.class);
        top.setOnePrime("onePrime");
        testCustomConverterHashMapObject.setTestObjectPrime(top);
        TestCustomConverterHashMapPrimeObject dest = mapper.map(testCustomConverterHashMapObject,
                                                                TestCustomConverterHashMapPrimeObject.class);
        assertEquals(to, dest.getTestObjects().get("object1"));
        assertEquals(top, dest.getTestObjects().get("object2"));

    }

    @Test
    public void testFieldCustomConverter() {
        mapper = getMapper("mappings/fieldCustomConverter.xml");
        SimpleObj src = newInstance(SimpleObj.class);
        src.setField1(String.valueOf(System.currentTimeMillis()));

        SimpleObjPrime2 dest = mapper.map(src, SimpleObjPrime2.class);

        // Custom converter specified for the field1 mapping, so verify custom converter was actually used
        assertNotNull("dest field1 should not be null", dest.getField1Prime());
        StringTokenizer st = new StringTokenizer(dest.getField1Prime(), "-");
        assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
        String token1 = st.nextToken();
        assertEquals("1st portion of dest field1 value should equal src field value", src.getField1(), token1);
        String token2 = st.nextToken();
        assertEquals("dest field1 value should have been appended to by the cust converter",
                     StringAppendCustomConverter.APPENDED_VALUE, token2);
    }

    @Test
    public void testFieldCustomConverter_NullSrcValue() {
        // Test that custom converter gets invoked even if the src field value is NULL
        mapper = getMapper("mappings/fieldCustomConverter.xml");
        SimpleObj src = newInstance(SimpleObj.class);
        src.setField1(null);

        SimpleObjPrime2 dest = mapper.map(src, SimpleObjPrime2.class);

        // Custom converter specified for the field1 mapping, so verify custom converter was actually used
        assertNotNull("dest field1 should not be null", dest.getField1Prime());
        StringTokenizer st = new StringTokenizer(dest.getField1Prime(), "-");
        assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
        String token1 = st.nextToken();
        assertEquals("dest field1 value should contain the explicit null string", "null", token1);
        String token2 = st.nextToken();
        assertEquals("dest field1 value should have been appended to by the cust converter",
                     StringAppendCustomConverter.APPENDED_VALUE, token2);
    }

    @Test
    public void testFieldCustomConverter_CustomMapType() {
        // Test that custom field converter works for Custom Map Types
        mapper = getMapper("mappings/fieldCustomConverter.xml");
        CustomMap src = newInstance(CustomMap.class);
        src.putValue("fieldA", "someStringValue");

        SimpleObj dest = mapper.map(src, SimpleObj.class);

        // Custom converter specified for the field1 mapping, so verify custom converter was actually used
        assertNotNull("dest field1 should not be null", dest.getField1());
        StringTokenizer st = new StringTokenizer(dest.getField1(), "-");
        assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
        String token1 = st.nextToken();
        assertEquals("1st portion of dest field1 value should equal src field value", src.getValue("fieldA"), token1);
        String token2 = st.nextToken();
        assertEquals("dest field1 value should have been appended to by the cust converter",
                     StringAppendCustomConverter.APPENDED_VALUE, token2);
    }

    @Test
    public void testFieldCustomConverter_HashMapField() {
        // Test that custom field converter works for Map type fields
        mapper = getMapper("mappings/fieldCustomConverter.xml");
        MapToProperty src = newInstance(MapToProperty.class);
        Map<String, String> hashMap = newInstance(HashMap.class);
        hashMap.put("fieldA", "someStringValue");
        src.setHashMap(hashMap);

        SimpleObj dest = mapper.map(src, SimpleObj.class);

        // Custom converter specified for the field1 mapping, so verify custom converter was actually used
        assertNotNull("dest field1 should not be null", dest.getField1());
        StringTokenizer st = new StringTokenizer(dest.getField1(), "-");
        assertEquals("dest field1 value should contain a hyphon", 2, st.countTokens());
        String token1 = st.nextToken();
        assertEquals("1st portion of dest field1 value should equal src field value", src.getHashMap().get("fieldA"), token1);
        String token2 = st.nextToken();
        assertEquals("dest field1 value should have been appended to by the cust converter",
                     StringAppendCustomConverter.APPENDED_VALUE, token2);
    }

    @Test
    public void testFieldCustomConverter_WithCustomConverterId() {
        mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/fieldCustomConverter.xml")
                .withCustomConverterWithId("CustomConverterWithId", new StringAppendCustomConverter())
                .build();

        AnotherTestObject src = newInstance(AnotherTestObject.class);
        src.setField3("field3");

        SimpleObj dest = mapper.map(src, SimpleObj.class);

        assertEquals("dest field1 value should have been appended to by the cust converter", src.getField3() + "-"
                                                                                             + StringAppendCustomConverter.APPENDED_VALUE, dest.getField1());
    }

    @Test
    public void testCustomConverter_MapNullFalse() {
        mapper = getMapper("mappings/custom-converter-map-null.xml");
        SimpleObj src = newInstance(SimpleObj.class);
        src.setField1(null);

        SimpleObjPrime2 dest = null;
        try {
            dest = mapper.map(src, SimpleObjPrime2.class);
        } catch (Exception e) {
            fail("custom converter should not have been invoked");
        }

        assertNull("dest value should be null", dest.getField1Prime());
    }

}
