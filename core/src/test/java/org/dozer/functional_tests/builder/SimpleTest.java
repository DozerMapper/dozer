/**
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
package org.dozer.functional_tests.builder;

import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Buzdin
 */
public class SimpleTest extends Assert {

  private DozerBeanMapper beanMapper;

  @Before
  public void setUp() {
    beanMapper = new DozerBeanMapper();

  }

  @Test
  public void shouldPerformSimpleMapping() {
    beanMapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(
                type(Source.class),
                type(Destination.class)
        )
                .fields(
                        field("stringValue").accessible(true),
                        field("destStringValue")
                );
      }
    });
    Source source = new Source();
    source.setStringValue("A");

    Destination result = beanMapper.map(source, Destination.class);
    assertEquals("A", result.getDestStringValue());
  }

  @Test
  public void shouldPerformMapBasedMapping() {
    beanMapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(
                Source.class,
                Map.class
        ).fields(
                field("stringValue").accessible(true),
                this_().mapKey("key").mapMethods("get", "put")
        );
      }
    });
    Source source = new Source();
    source.setStringValue("A");

    Map result = beanMapper.map(source, HashMap.class);
    assertEquals("A", result.get("key"));
  }


  public static class Source {
    private String stringValue;
    
    public void setStringValue(String stringValue) {
      this.stringValue = stringValue;
    }
  }

  public static class Destination {
    private String destStringValue;

    public String getDestStringValue() {
      return destStringValue;
    }

    public void setDestStringValue(String destStringValue) {
      this.destStringValue = destStringValue;
    }
  }

}
