/**
 * Copyright 2005-2013 Dozer Project
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.dozer.DozerConverter;
import org.junit.Test;

/**
 * @author pqian
 */
public class CustomConverterMap2PrimeTest extends AbstractFunctionalTest {

  private static final String JSON_TEXT  = "{\"test\":\"success\"}";
  private static final String MAP_KEY  = "test";
  private static final String MAP_VALUE = "success";
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    mapper = getMapper("customConverterMap2Prime.xml");
  }

  @Test
  public void testMap2PrimeMappingUsingCustomConverter() {
    BeanA a = new BeanA();
    a.setMap(new HashMap<String, String>());

    BeanB b = mapper.map(a, BeanB.class);
    assertNotNull(b);
    assertEquals(JSON_TEXT, b.getText());

    // inverse mapping
    b = new BeanB();
    b.setText("dummy");

    a = mapper.map(b, BeanA.class);
    assertNotNull(a);
    assertNotNull(a.getMap());
    assertEquals(1, a.getMap().size());
    assertEquals(MAP_VALUE, a.getMap().get(MAP_KEY));
  }
  
  @Test
  public void testMap2PrimeMappingWithoutCustomConverter() {
    BeanA a = new BeanA();
    a.setMap(new HashMap<String, String>());

    BeanB b = mapper.map(a, BeanB.class, "no-custom-converter");
    assertNotNull(b);
    assertNull(b.getText());

    // inverse mapping
    b = new BeanB();
    b.setText("dummy");

    a = mapper.map(b, BeanA.class, "no-custom-converter");
    assertNotNull(a);
    assertNotNull(a.getMap());
    assertEquals(1, a.getMap().size());
    assertEquals("dummy", a.getMap().get("text"));
  }
  
  @SuppressWarnings("rawtypes")
  public static class Map2JsonConverter extends DozerConverter<Map, String> {

    public Map2JsonConverter() {
      super(Map.class, String.class);
    }

    @Override
    public String convertTo(Map source, String destination) {
      if (source == null) {
        return null;
      }
      // return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(source);
      return JSON_TEXT;
    }

    @Override
    public Map<String, String> convertFrom(String source, Map destination) {
      if (source == null) {
        return null;
      }
      // return new com.fasterxml.jackson.databind.ObjectMapper().readValue(source,
      //			new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
      Map<String,String> ret = new HashMap<String, String>();
      ret.put(MAP_KEY, MAP_VALUE);
      return ret;
    }
  }

  public static class BeanA {
    private Map<String,String> map;

    public Map<String,String> getMap() {
    	return map;
    }

    public void setMap(Map<String,String> map) {
      this.map = map;
    }
  }

  public static class BeanB {
    private String text;

    public String getText() {
        return text;
      }

    public void setText(String text) {
    	this.text = text;
    }
  }

}
