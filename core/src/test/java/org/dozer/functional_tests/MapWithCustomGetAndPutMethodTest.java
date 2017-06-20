/*
 * Copyright 2005-2017 Dozer Project
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

import java.util.HashMap;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.MappingException;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeDefinition;
import org.hamcrest.number.IsCloseTo;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class MapWithCustomGetAndPutMethodTest extends AbstractFunctionalTest {


  @Test
  public void testDefaultMapBehaviour_UseDefaultGetAndPutMethod() {
    Mapper defaultMapper = DozerBeanMapperBuilder.buildDefault();
    
    // Map to Object, should use "get"
    MapWithCustomGetAndPut input1 = MapWithCustomGetAndPut.createInput();
    ValueContainer output1 = defaultMapper.map(input1, ValueContainer.class);

    assertThat(output1.getEmptyString(), is(""));
    assertThat(output1.getStringValue(), is("String"));
    assertThat(output1.getIntValue(), is(123));
    assertThat(output1.getDoubleValue(), is(IsCloseTo.closeTo(123.456, 0.00001)));
    
    
    // Object to Map, should use "put"
    ValueContainer input2 = ValueContainer.createInput();
    MapWithCustomGetAndPut output2 = defaultMapper.map(input2, MapWithCustomGetAndPut.class);

    assertThat(output2.get("emptyString"), is(""));
    assertThat(output2.get("stringValue"), is("Different String"));
    assertThat(output2.get("intValue"), is(-987));
    assertThat(output2.get("doubleValue"), is(-987.654));
  }

  @Test
  public void testMapWithCustomMethods_UseSpecifiedMethods() {
    Mapper customMapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(
                        ValueContainer.class,
                        new TypeDefinition(MapWithCustomGetAndPut.class).mapMethods("customGet", "customPut")
                );
              }
            })
            .build();
    
    // Map to Object, should use "customGet"
    MapWithCustomGetAndPut input1 = MapWithCustomGetAndPut.createInput();
    ValueContainer output1 = customMapper.map(input1, ValueContainer.class);

    assertThat(output1.getEmptyString(), is("Map contains EMPTY"));
    assertThat(output1.getStringValue(), is("String"));
    assertThat(output1.getIntValue(), is(123));
    assertThat(output1.getDoubleValue(), is(IsCloseTo.closeTo(123.456, 0.00001)));

    // Object to Map, should use "customPut"
    ValueContainer input = ValueContainer.createInput();
    MapWithCustomGetAndPut output = customMapper.map(input, MapWithCustomGetAndPut.class);

    assertThat(output.get("emptyString"), is("Tried to insert EMPTY"));
    assertThat(output.get("stringValue"), is("Different String"));
    assertThat(output.get("intValue"), is(987)); // only the Int should be absolute
    assertThat(output.get("doubleValue"), is(-987.654));
  }

  @Test
  public void testMapWithNullGetAndPutMethods_FallbackToDefaultMethods() {
    Mapper nullMapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(
                        ValueContainer.class,
                        new TypeDefinition(MapWithCustomGetAndPut.class).mapMethods(null, null)
                );
              }
            })
            .build();
    
    // Map to Object, should use "get"
    MapWithCustomGetAndPut input1 = MapWithCustomGetAndPut.createInput();
    ValueContainer output1 = nullMapper.map(input1, ValueContainer.class);
    
    assertThat(output1.getEmptyString(), is(""));
    assertThat(output1.getStringValue(), is("String"));
    assertThat(output1.getIntValue(), is(123));
    assertThat(output1.getDoubleValue(), is(IsCloseTo.closeTo(123.456, 0.00001)));
    
    // Object to Map, should use "put"
    ValueContainer input2 = ValueContainer.createInput();
    MapWithCustomGetAndPut output2 = nullMapper.map(input2, MapWithCustomGetAndPut.class);
    
    assertThat(output2.get("emptyString"), is(""));
    assertThat(output2.get("stringValue"), is("Different String"));
    assertThat(output2.get("intValue"), is(-987));
    assertThat(output2.get("doubleValue"), is(-987.654));
  }
  
  @Test
  public void testMapWithEmptyGetAndPutMethods_FallbackToDefaultMethods() {
    Mapper emptyMapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(
                        ValueContainer.class,
                        new TypeDefinition(MapWithCustomGetAndPut.class).mapMethods("", "")
                );
              }
            })
            .build();
    
    // Map to Object, should use "get"
    MapWithCustomGetAndPut input1 = MapWithCustomGetAndPut.createInput();
    ValueContainer output1 = emptyMapper.map(input1, ValueContainer.class);
    
    assertThat(output1.getEmptyString(), is(""));
    assertThat(output1.getStringValue(), is("String"));
    assertThat(output1.getIntValue(), is(123));
    assertThat(output1.getDoubleValue(), is(IsCloseTo.closeTo(123.456, 0.00001)));
    
    // Object to Map, should use "put"
    ValueContainer input2 = ValueContainer.createInput();
    MapWithCustomGetAndPut output2 = emptyMapper.map(input2, MapWithCustomGetAndPut.class);
    
    assertThat(output2.get("emptyString"), is(""));
    assertThat(output2.get("stringValue"), is("Different String"));
    assertThat(output2.get("intValue"), is(-987));
    assertThat(output2.get("doubleValue"), is(-987.654));
  }
  
  /**
   * This is simply to make sure we are trying to be 'too intelligent' and still throwing exceptions
   * if non-existent methods are configured.
   */
  @Test(expected=MappingException.class)
  public void testMapWithInvalidGetMethod_ThrowsMappingException() {
    Mapper invalidMapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(
                        ValueContainer.class,
                        new TypeDefinition(MapWithCustomGetAndPut.class).mapMethods("invalidGetMethod", "invalidPutMethod")
                );
              }
            })
            .build();
    
    // Map to Object, will try to  use "invalidGetMethod"
    MapWithCustomGetAndPut input = MapWithCustomGetAndPut.createInput();
    invalidMapper.map(input, ValueContainer.class);
    
    fail("Expected MappingException");
  }
  
  /**
   * This is simply to make sure we are trying to be 'too intelligent' and still throwing exceptions
   * if non-existent methods are configured.
   */
  @Test(expected=MappingException.class)
  public void testMapWithInvalidPutMethod_ThrowsMappingException() {
    Mapper invalidMapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(
                        ValueContainer.class,
                        new TypeDefinition(MapWithCustomGetAndPut.class).mapMethods("invalidGetMethod", "invalidPutMethod")
                );
              }
            })
            .build();

    // Object to Map, will try to  use "invalidPutMethod"
    ValueContainer input = ValueContainer.createInput();
    invalidMapper.map(input, MapWithCustomGetAndPut.class);

    fail("Expected MappingException");
  }

  protected static class MapWithCustomGetAndPut extends HashMap<String, Object> {

    private static final long serialVersionUID = 3085080961637343951L;

    public Object customGet(Object key) {
      Object value = super.get(key);

      if (value.equals("")) {
        return "Map contains EMPTY";
      } else {
        return value;
      }
    }

    public Object customPut(String key, Object value) {
      Object valueToInsert = value;

      if (value.equals("") ) {
        valueToInsert = "Tried to insert EMPTY";
      } else if (value instanceof Integer) {
        // for testing purposes, we convert integers to absolute value
        valueToInsert = Math.abs((Integer)value);
      }

      super.put(key, valueToInsert);

      return valueToInsert;
    }

    public static MapWithCustomGetAndPut createInput() {
      MapWithCustomGetAndPut result = new MapWithCustomGetAndPut();
      result.put("emptyString", "");
      result.put("stringValue", "String");
      result.put("intValue", 123);
      result.put("doubleValue", 123.456);
      return result;
    }
  }

  protected static class ValueContainer {

    private String emptyString;
    private String stringValue;
    private int intValue;
    private double doubleValue;

    public String getEmptyString() {
      return emptyString;
    }
    public void setEmptyString(String nullString) {
      this.emptyString = nullString;
    }

    public String getStringValue() {
      return stringValue;
    }
    public void setStringValue(String stringValue) {
      this.stringValue = stringValue;
    }

    public int getIntValue() {
      return intValue;
    }
    public void setIntValue(int intValue) {
      this.intValue = intValue;
    }

    public double getDoubleValue() {
      return doubleValue;
    }
    public void setDoubleValue(double doubleValue) {
      this.doubleValue = doubleValue;
    }

    public static ValueContainer createInput() {
      ValueContainer result = new ValueContainer();
      result.emptyString = "";
      result.stringValue = "Different String";
      result.intValue = -987;
      result.doubleValue = -987.654;
      return result;
    }
  }

}
