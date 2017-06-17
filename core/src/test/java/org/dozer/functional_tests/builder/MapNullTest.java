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
package org.dozer.functional_tests.builder;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.functional_tests.AbstractFunctionalTest;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeMappingOptions;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Dmitry Buzdin
 */
public class MapNullTest extends AbstractFunctionalTest {

  private Source source;
  private Destination destination;

  @Before
  public void setUp() {
    source = new Source();
    destination = new Destination();
  }

  @Test
  public void shouldMapNull() {
    Mapper beanMapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(Source.class, Destination.class, TypeMappingOptions.mapNull(true))
                        .fields("a", "b");
              }
            })
            .build();

    source.setA(null);
    destination.setB("notNull");

    beanMapper.map(source, destination);
    
    assertThat(destination.getB(), nullValue());
  }

  @Test
  public void shouldMapEmptyString() {
    Mapper beanMapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(Source.class, Destination.class, TypeMappingOptions.mapEmptyString(true))
                        .fields("a", "b");
              }
            })
            .build();

    source.setA("");
    destination.setB("notNull");

    beanMapper.map(source, destination);

    assertThat(destination.getB(), equalTo(""));
  }
  
  public static class Source {
    private String a;

    public String getA() {
      return a;
    }

    public void setA(String a) {
      this.a = a;
    }
  }

  public static class Destination {
    private String b;

    public String getB() {
      return b;
    }

    public void setB(String b) {
      this.b = b;
    }
  }

}
