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

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.MappingException;
import org.dozer.loader.api.BeanMappingBuilder;
import org.junit.Test;

/**
 * @author dmitry.buzdin
 */
public class ExceptionHandlingFunctionalTest extends AbstractFunctionalTest {

  @Test(expected = MappingException.class)
  public void test_UnableToDetermineType() {
    Mapper mapper = getMapper("mappings/missingSetter.xml");
    mapper.map("", NoNothing.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailOnDuplicateMapping() {
    Mapper mapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(String.class, NoNothing.class);
              }
            })
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(String.class, NoNothing.class);
              }
            })
            .build();
    
    mapper.map("A", NoNothing.class);
  }

  public static class NoNothing {
  }

}
