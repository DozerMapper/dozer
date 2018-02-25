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
package org.dozer.functional_tests;

import java.util.Map;

import org.dozer.MappingException;
import org.dozer.vo.enumtest.MyBean;
import org.dozer.vo.enumtest.MyBeanPrime;
import org.dozer.vo.enumtest.MyBeanPrimeByte;
import org.dozer.vo.enumtest.MyBeanPrimeInteger;
import org.dozer.vo.enumtest.MyBeanPrimeLong;
import org.dozer.vo.enumtest.MyBeanPrimeShort;
import org.dozer.vo.enumtest.MyBeanPrimeString;
import org.dozer.vo.enumtest.SrcType;
import org.dozer.vo.enumtest.SrcTypeWithOverride;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;

/**
 * Functional test for enum mapping as described 
 * <a href=https://github.com/DozerMapper/dozer/blob/master/docs/asciidoc/documentation/enum.adoc>here</a>.
 * 
 * In this functional test, Enum is categorized into two types: Based Enum and Overridden Enum.
 * Based Enum refers to those enum without any overridden methods, including constructors.  A 
 * typical Based Enum would look as below.
 * <code>
 * public enum SrcType {
 *   FOO , BAR;
 * }
 * </code>
 * On the contrary, Overridden Enum refers to those enum with overridden methods, including 
 * constructors. A typical Overridden Enum would look as below.
 * <code>
 * public enum SrcTypeWithOverride {
 *   FOO { public String display() { return "Src.FOO"; } },
 *   BAR { public String display() { return "Src.BAR"; } };
 *   public abstract String display();
 * }
 * </code>
 * 
 * @author cchou.hung
 *
 */
public class EnumMappingTest extends AbstractFunctionalTest {

  @Rule
  public ExpectedException testExpectedException = ExpectedException.none();

  /**
   * Test on a mapping from Overridden Enum to Based Enum. 
   */
  @Test
  public void testOverriddenEnumMapsToBasedEnum() {
    mapper = getMapper("mappings/enumMappingOverriedEnumToBasedEnum.xml");
    MyBean src = newInstance(MyBean.class);
    src.setSrcTypeWithOverride(SrcTypeWithOverride.FOO);
    MyBeanPrime dest = mapper.map(src, MyBeanPrime.class);
    assertEquals(src.getSrcTypeWithOverride().toString(), dest.getDestType().toString());
  }

  /**
   * Test on a mapping from Based Enum to Overridden Enum.
   */
  @Test
  public void testBasedEnumMapsToOverriddenEnum() {
    mapper = getMapper("mappings/enumMappingOverriedEnumToBasedEnum.xml");
    MyBean src = newInstance(MyBean.class);
    src.setSrcType(SrcType.FOO);
    MyBeanPrime dest = mapper.map(src, MyBeanPrime.class);
    assertEquals(src.getSrcType().toString(), dest.getDestTypeWithOverride().toString());
  }

  /**
   * Test on a mapping from Based Enum to Based Enum.
   */
  @Test
  public void testBasedEnumMapsToBasedEnum() {
    mapper = getMapper("mappings/enumMapping.xml");
    MyBean src = newInstance(MyBean.class);
    src.setSrcType(SrcType.FOO);
    MyBeanPrime dest = mapper.map(src, MyBeanPrime.class);
    assertEquals(src.getSrcType().toString(), dest.getDestType().toString());
  }

  /**
   * Test on a mapping from Overridden Enum to Overridden Enum.
   */
  @Test
  public void testOverriddenEnumMapsToOverriddenEnum() {
    mapper = getMapper("mappings/enumMapping.xml");
    MyBean src = newInstance(MyBean.class);
    src.setSrcTypeWithOverride(SrcTypeWithOverride.FOO);
    MyBeanPrime dest = mapper.map(src, MyBeanPrime.class);
    assertEquals(src.getSrcTypeWithOverride().toString(), dest.getDestTypeWithOverride().toString());
  }

  /**
   * Test on a mapping from Enum to itself. This test is used to reproduce bug#1806780.
   */
  @Test
  public void testEnumMapsToItself() {
    MyBean src = newInstance(MyBean.class);
    src.setSrcType(SrcType.FOO);
    MyBean dest = mapper.map(src, MyBean.class);
    assertEquals(src.getSrcType(), dest.getSrcType());
    assertEquals(src.getSrcTypeWithOverride(), dest.getSrcTypeWithOverride());
  }

  /**
   * Test on a mapping from enum to {@link String}.
   */
  @Test
  public void testEnumMapsToString() {
    mapper = getMapper("mappings/enumMapping.xml");
    MyBean src = new MyBean();
    src.setSrcType(SrcType.FOO);
    MyBeanPrimeString dest = mapper.map(src, MyBeanPrimeString.class);
    assertEquals("FOO", dest.getDestType());
  }

  /**
   * Test on a mapping from {@link String} to enum.
   */
  @Test
  public void testStringMapsToEnum() {
    mapper = getMapper("mappings/enumMapping.xml");
    MyBeanPrimeString src = new MyBeanPrimeString();
    src.setDestType("FOO");
    src.setDestTypeWithOverride("BAR");
    MyBean dest = mapper.map(src, MyBean.class);
    assertEquals(SrcType.FOO, dest.getSrcType());
    assertEquals(SrcTypeWithOverride.BAR, dest.getSrcTypeWithOverride());
  }

  /**
   * Test on a mapping from byte types to enum.
   */
  @Test
  public void testByteMapsToEnum() {
    mapper = getMapper("mappings/enumMapping.xml");
    MyBeanPrimeByte src = new MyBeanPrimeByte();
    src.setFirst((byte) 0);
    src.setSecond((byte) 1);
    MyBean dest = mapper.map(src, MyBean.class);
    assertEquals(SrcType.FOO, dest.getSrcType());
    assertEquals(SrcTypeWithOverride.BAR, dest.getSrcTypeWithOverride());
  }

  /**
   * Test on a mapping from short types to enum.
   */
  @Test
  public void testShortMapsToEnum() {
    mapper = getMapper("mappings/enumMapping.xml");
    MyBeanPrimeShort src = new MyBeanPrimeShort();
    src.setFirst((short) 0);
    src.setSecond((short) 1);
    MyBean dest = mapper.map(src, MyBean.class);
    assertEquals(SrcType.FOO, dest.getSrcType());
    assertEquals(SrcTypeWithOverride.BAR, dest.getSrcTypeWithOverride());
  }

  /**
   * Test on a mapping from integer types to enum.
   */
  @Test
  public void testIntegerMapsToEnum() {
    mapper = getMapper("mappings/enumMapping.xml");
    MyBeanPrimeInteger src = new MyBeanPrimeInteger();
    src.setFirst(0);
    src.setSecond(1);
    MyBean dest = mapper.map(src, MyBean.class);
    assertEquals(SrcType.FOO, dest.getSrcType());
    assertEquals(SrcTypeWithOverride.BAR, dest.getSrcTypeWithOverride());
  }

  /**
   * Test on a mapping from long types to enum.
   */
  @Test
  public void testLongMapsToEnum() {
    mapper = getMapper("mappings/enumMapping.xml");
    MyBeanPrimeLong src = new MyBeanPrimeLong();
    src.setFirst(0L);
    src.setSecond(1L);
    MyBean dest = mapper.map(src, MyBean.class);
    assertEquals(SrcType.FOO, dest.getSrcType());
    assertEquals(SrcTypeWithOverride.BAR, dest.getSrcTypeWithOverride());
  }

  /**
   * Test on a mapping from enum types to byte.
   */
  @Test
  public void testEnumMapsToByte() {
    mapper = getMapper("mappings/enumMapping.xml");
    MyBean src = new MyBean();
    src.setSrcType(SrcType.FOO);
    src.setSrcTypeWithOverride(SrcTypeWithOverride.BAR);
    MyBeanPrimeByte dest = mapper.map(src, MyBeanPrimeByte.class);
    assertEquals(0, dest.getFirst());
    assertEquals(Byte.valueOf((byte) 1), dest.getSecond());
  }
  /**
   * Test on a mapping from enum types to short.
   */
  @Test
  public void testEnumMapsToShort() {
    mapper = getMapper("mappings/enumMapping.xml");
    MyBean src = new MyBean();
    src.setSrcType(SrcType.FOO);
    src.setSrcTypeWithOverride(SrcTypeWithOverride.BAR);
    MyBeanPrimeShort dest = mapper.map(src, MyBeanPrimeShort.class);
    assertEquals(0, dest.getFirst());
    assertEquals(Short.valueOf((short) 1), dest.getSecond());
  }

  /**
   * Test on a mapping from enum types to integer.
   */
  @Test
  public void testEnumMapsToInteger() {
    mapper = getMapper("mappings/enumMapping.xml");
    MyBean src = new MyBean();
    src.setSrcType(SrcType.FOO);
    src.setSrcTypeWithOverride(SrcTypeWithOverride.BAR);
    MyBeanPrimeInteger dest = mapper.map(src, MyBeanPrimeInteger.class);
    assertEquals(0, dest.getFirst());
    assertEquals(Integer.valueOf(1), dest.getSecond());
  }

  /**
   * Test on a mapping from enum types to long.
   */
  @Test
  public void testEnumMapsToLong() {
    mapper = getMapper("mappings/enumMapping.xml");
    MyBean src = new MyBean();
    src.setSrcType(SrcType.FOO);
    src.setSrcTypeWithOverride(SrcTypeWithOverride.BAR);
    MyBeanPrimeLong dest = mapper.map(src, MyBeanPrimeLong.class);
    assertEquals(0, dest.getFirst());
    assertEquals(Long.valueOf(1L), dest.getSecond());
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testBasedEnumMapsToMap() {
    mapper = getMapper();
    MyBean src = newInstance(MyBean.class);
    src.setSrcType(SrcType.FOO);
    Map<String, ?> mappedBean = mapper.map(src, Map.class);
    assertEquals(SrcType.FOO, mappedBean.get("srcType"));
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testOverriddenEnumMapsToMap() {
    mapper = getMapper();
    MyBean src = newInstance(MyBean.class);
    src.setSrcTypeWithOverride(SrcTypeWithOverride.FOO);
    Map<String, ?> mappedBean = mapper.map(src, Map.class);
    assertEquals(SrcTypeWithOverride.FOO, mappedBean.get("srcTypeWithOverride"));
  }

  /**
   * Test on a mapping from byte types to enum.
   */
  @Test
  public void testByteMapsToEnumOutOfOrdinalRange() {
    testExpectedException.expect(MappingException.class);
    testExpectedException.expectMessage(startsWith("Cannot convert"));
    mapper = getMapper("mappings/enumMapping.xml");
    MyBeanPrimeByte src = new MyBeanPrimeByte();
    src.setFirst((byte) 0);
    src.setSecond((byte) 3);
    mapper.map(src, MyBean.class);
  }

  /**
   * Test on a mapping from short types to enum.
   */
  @Test
  public void testShortMapsToEnumOutOfOrdinalRange() {
    testExpectedException.expect(MappingException.class);
    testExpectedException.expectMessage(startsWith("Cannot convert"));
    mapper = getMapper("mappings/enumMapping.xml");
    MyBeanPrimeShort src = new MyBeanPrimeShort();
    src.setFirst((short) 0);
    src.setSecond((short) 3);
    mapper.map(src, MyBean.class);
  }

  /**
   * Test on a mapping from integer types to enum.
   */
  @Test
  public void testIntegerMapsToEnumOutOfOrdinalRange() {
    testExpectedException.expect(MappingException.class);
    testExpectedException.expectMessage(startsWith("Cannot convert"));
    mapper = getMapper("mappings/enumMapping.xml");
    MyBeanPrimeInteger src = new MyBeanPrimeInteger();
    src.setFirst(0);
    src.setSecond(3);
    mapper.map(src, MyBean.class);
  }

  /**
   * Test on a mapping from long types to enum.
   */
  @Test
  public void testLongMapsToEnumOutOfOrdinalRange() {
    testExpectedException.expect(MappingException.class);
    testExpectedException.expectMessage(startsWith("Cannot convert")); 
    mapper = getMapper("mappings/enumMapping.xml");
    MyBeanPrimeLong src = new MyBeanPrimeLong();
    src.setFirst(0L);
    src.setSecond(3L);
    mapper.map(src, MyBean.class);
  }

  /**
   * Test on a mapping from {@link String} to enum with non-existing enum value.
   */
  @Test
  public void testStringMapsToEnumNonexistEnumValue() {
    testExpectedException.expect(MappingException.class);
    testExpectedException.expectMessage(startsWith("Cannot convert"));
    mapper = getMapper("mappings/enumMapping.xml");
    MyBeanPrimeString src = new MyBeanPrimeString();
    src.setDestType("BAZ");
    mapper.map(src, MyBean.class);
  }
}
