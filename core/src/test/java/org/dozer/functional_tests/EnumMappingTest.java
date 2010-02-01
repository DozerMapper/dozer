/*
 * Copyright 2005-2010 the original author or authors.
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

import org.dozer.vo.enumtest.MyBean;
import org.dozer.vo.enumtest.MyBeanPrime;
import org.dozer.vo.enumtest.SrcType;
import org.dozer.vo.enumtest.SrcTypeWithOverride;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Functional test for enum mapping as described 
 * <a href=http://dozer.sourceforge.net/documentation/enum.html>here</a>.
 * 
 * In this functional test, Enum is categorized into two types: Based Enum and Overrided Enum.
 * Based Enum refers to those enum without any overrided methods, including constructors.  A 
 * typical Based Enum would look as below.
 * <code><pre>
 * public enum SrcType {
 *   FOO , BAR;
 * }
 * </pre></code>
 * On the contrary, Overrided Enum refers to those enum with overrided methods, including 
 * constructors. A typical Overrided Enum would look as below.
 * <code><pre>
 * public enum SrcTypeWithOverride {
 *   FOO { public String display() { return "Src.FOO"; } },
 *   BAR { public String display() { return "Src.BAR"; } };
 *   public abstract String display();
 * }
 * </pre></code>
 * 
 * @author cchou.hung
 *
 */
public class EnumMappingTest extends AbstractFunctionalTest {

  /**
   * Test on a mapping from Overrided Enum to Based Enum. 
   */
  @Test
  public void testOverridedEnumMapsToBasedEnum() {
    mapper = getMapper(new String[] { "enumMappingOverriedEnumToBasedEnum.xml" });
    MyBean src = newInstance(MyBean.class);
    src.setSrcTypeWithOverride(SrcTypeWithOverride.FOO);
    MyBeanPrime dest = mapper.map(src, MyBeanPrime.class);
    assertEquals(src.getSrcTypeWithOverride().toString(), dest.getDestType().toString());
  }

  /**
   * Test on a mapping from Based Enum to Overrided Enum.
   */
  @Test
  public void testBasedEnumMapsToOverridedEnum() {
    mapper = getMapper(new String[] { "enumMappingOverriedEnumToBasedEnum.xml" });
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
    mapper = getMapper(new String[] { "enumMapping.xml" });
    MyBean src = newInstance(MyBean.class);
    src.setSrcType(SrcType.FOO);
    MyBeanPrime dest = mapper.map(src, MyBeanPrime.class);
    assertEquals(src.getSrcType().toString(), dest.getDestType().toString());
  }

  /**
   * Test on a mapping from Overrided Enum to Overrided Enum.
   */
  @Test
  public void testOverridedEnumMapsToOverridedEnum() {
    mapper = getMapper(new String[] { "enumMapping.xml" });
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
   * Test on if mapping to nonexist enum value would throw exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testEnumMapsToNonexistEnumValue() {
    mapper = getMapper(new String[] { "enumMapping.xml" });
    MyBean src = newInstance(MyBean.class);
    src.setSrcType(SrcType.BAR);
    mapper.map(src, MyBeanPrime.class);
  }

}
