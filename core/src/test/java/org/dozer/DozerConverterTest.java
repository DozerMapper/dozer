/*
 * Copyright 2005-2009 the original author or authors.
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
package org.dozer;

import junit.framework.TestCase;

import java.math.BigDecimal;

/**
 * @author dmitry.buzdin
 */
public class DozerConverterTest extends TestCase {

  private DozerConverter<String, Integer> converter;

  protected void setUp() throws Exception {
    super.setUp();
    converter = new DozerConverter<String, Integer>(String.class, Integer.class) {

      public Integer convertTo(String source, Integer destination) {
        return Integer.parseInt(source);
      }

      public String convertFrom(Integer source, String destination) {
        return source.toString();
      }
    };
  }

  public void test_parameterNotSet() {
    try {
      converter.getParameter();
      fail();
    } catch (IllegalStateException e) {
    }
  }

  public void test_convert_exception() {
    try {
      converter.convert(Boolean.TRUE, new BigDecimal(1), Boolean.class, BigDecimal.class);
      fail();
    } catch (MappingException e) {
      assertTrue(e.getMessage().contains(this.getClass().getName()));
    }
  }

  public void test_gettingParameter() {
    converter.setParameter("A");
    assertEquals("A", converter.getParameter());
  }

  public void test_convertFromTo() {
    assertEquals("1", converter.convertFrom(new Integer(1)));
    assertEquals(new Integer(2), converter.convertTo("2"));

    assertEquals("1", converter.convertFrom(new Integer(1), "0"));
    assertEquals(new Integer(2), converter.convertTo("2", new Integer(0)));
  }

  public void test_FullCycle() {
    assertEquals(1, converter.convert(null, "1", Integer.class, String.class));
    assertEquals("1", converter.convert(null, new Integer(1), String.class, Integer.class));
  }

  public void testObjectType() {
    assertEquals(1, converter.convert(null, "1", Object.class, String.class));
    assertEquals("1", converter.convert(null, new Integer(1), Object.class, Integer.class));
  }

  public void test_hierarchy() {
    DozerConverter<Number, Integer> converter = new DozerConverter<Number, Integer>(Number.class, Integer.class) {

      public Integer convertTo(Number source, Integer destination) {
        return source.intValue();
      }

      public Number convertFrom(Integer source, Number destination) {
        return source;
      }
    };


    assertEquals(new Integer(1), converter.convert(null, new Integer(1), Number.class, Integer.class));

    assertEquals(new Integer(1), converter.convert(null, new Double(1), Integer.class, Number.class));
  }

}
