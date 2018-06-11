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
package com.github.dozermapper.core.functional_tests;

import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.vo.enumtest.MyBean;
import com.github.dozermapper.core.vo.enumtest.MyBeanPrime;
import com.github.dozermapper.core.vo.enumtest.MyBeanPrimeByte;
import com.github.dozermapper.core.vo.enumtest.MyBeanPrimeInteger;
import com.github.dozermapper.core.vo.enumtest.MyBeanPrimeLong;
import com.github.dozermapper.core.vo.enumtest.MyBeanPrimeShort;
import com.github.dozermapper.core.vo.enumtest.MyBeanPrimeString;
import com.github.dozermapper.core.vo.enumtest.SrcType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumMapping_WithExceptionsLoggedTest extends AbstractFunctionalTest {

    @Rule
    public ExpectedException testEnumMapsToNonexistEnumValueEE = ExpectedException.none();

    @Rule
    public ExpectedException testByteMapsToEnumOutOfOrdinalRangeEE = ExpectedException.none();

    @Rule
    public ExpectedException testShortMapsToEnumOutOfOrdinalRangeEE = ExpectedException.none();

    @Rule
    public ExpectedException testIntegerMapsToEnumOutOfOrdinalRangeEE = ExpectedException.none();

    @Rule
    public ExpectedException testLongMapsToEnumOutOfOrdinalRangeEE = ExpectedException.none();

    @Rule
    public ExpectedException testStringMapsToEnumNonexistEnumValueEE = ExpectedException.none();

    private final Logger LOG = LoggerFactory.getLogger(EnumMapping_WithExceptionsLoggedTest.class);

    /**
     * Test on if mapping to nonexist enum value would throw exception.
     */
    @Test
    public void testEnumMapsToNonexistEnumValue() {
        LOG.error("WithExceptionsLoggedTest; 'IllegalArgumentException: No enum constant com.github.dozermapper.core.vo.enumtest.DestType.BAR'");

        testEnumMapsToNonexistEnumValueEE.expectMessage("Cannot convert [BAR] to enum of type class com.github.dozermapper.core.vo.enumtest.DestType");
        testEnumMapsToNonexistEnumValueEE.expect(MappingException.class);

        mapper = getMapper("mappings/enumMapping.xml");

        MyBean src = newInstance(MyBean.class);
        src.setSrcType(SrcType.BAR);

        mapper.map(src, MyBeanPrime.class);
    }

    /**
     * Test on a mapping from byte types to enum.
     */
    @SuppressWarnings("unused")
    @Test
    public void testByteMapsToEnumOutOfOrdinalRange() {
        LOG.error("WithExceptionsLoggedTest; 'MappingException: Cannot convert [3] to enum of type class com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride'");

        testByteMapsToEnumOutOfOrdinalRangeEE.expect(MappingException.class);
        testByteMapsToEnumOutOfOrdinalRangeEE.expectMessage("Cannot convert [3] to enum of type class com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride");

        mapper = getMapper("mappings/enumMapping.xml");

        MyBeanPrimeByte src = new MyBeanPrimeByte();
        src.setFirst((byte)0);
        src.setSecond((byte)3);

        MyBean dest = mapper.map(src, MyBean.class);
    }

    /**
     * Test on a mapping from short types to enum.
     */
    @SuppressWarnings("unused")
    @Test
    public void testShortMapsToEnumOutOfOrdinalRange() {
        LOG.error("WithExceptionsLoggedTest; 'MappingException: Cannot convert [3] to enum of type class com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride'");

        testShortMapsToEnumOutOfOrdinalRangeEE.expect(MappingException.class);
        testShortMapsToEnumOutOfOrdinalRangeEE.expectMessage("Cannot convert [3] to enum of type class com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride");

        mapper = getMapper("mappings/enumMapping.xml");

        MyBeanPrimeShort src = new MyBeanPrimeShort();
        src.setFirst((short)0);
        src.setSecond((short)3);

        MyBean dest = mapper.map(src, MyBean.class);
    }

    /**
     * Test on a mapping from integer types to enum.
     */
    @SuppressWarnings("unused")
    @Test
    public void testIntegerMapsToEnumOutOfOrdinalRange() {
        LOG.error("WithExceptionsLoggedTest; 'MappingException: Cannot convert [3] to enum of type class com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride'");

        testIntegerMapsToEnumOutOfOrdinalRangeEE.expectMessage("Cannot convert [3] to enum of type class com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride");
        testIntegerMapsToEnumOutOfOrdinalRangeEE.expect(MappingException.class);

        mapper = getMapper("mappings/enumMapping.xml");

        MyBeanPrimeInteger src = new MyBeanPrimeInteger();
        src.setFirst(0);
        src.setSecond(3);

        MyBean dest = mapper.map(src, MyBean.class);
    }

    /**
     * Test on a mapping from long types to enum.
     */
    @SuppressWarnings("unused")
    @Test
    public void testLongMapsToEnumOutOfOrdinalRange() {
        LOG.error("WithExceptionsLoggedTest; 'MappingException: Cannot convert [3] to enum of type class com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride'");

        testLongMapsToEnumOutOfOrdinalRangeEE.expectMessage("Cannot convert [3] to enum of type class com.github.dozermapper.core.vo.enumtest.SrcTypeWithOverride");
        testLongMapsToEnumOutOfOrdinalRangeEE.expect(MappingException.class);

        mapper = getMapper("mappings/enumMapping.xml");

        MyBeanPrimeLong src = new MyBeanPrimeLong();
        src.setFirst(0L);
        src.setSecond(3L);

        MyBean dest = mapper.map(src, MyBean.class);
    }

    /**
     * Test on a mapping from {@link String} to enum with non-existing enum value.
     */
    @SuppressWarnings("unused")
    @Test
    public void testStringMapsToEnumNonexistEnumValue() {
        LOG.error("WithExceptionsLoggedTest; 'MappingException: Cannot convert [BAZ] to enum of type class com.github.dozermapper.core.vo.enumtest.SrcType'");

        testLongMapsToEnumOutOfOrdinalRangeEE.expectMessage("Cannot convert [BAZ] to enum of type class com.github.dozermapper.core.vo.enumtest.SrcType");
        testLongMapsToEnumOutOfOrdinalRangeEE.expect(MappingException.class);

        mapper = getMapper("mappings/enumMapping.xml");

        MyBeanPrimeString src = new MyBeanPrimeString();
        src.setDestType("BAZ");

        MyBean dest = mapper.map(src, MyBean.class);
    }
}
