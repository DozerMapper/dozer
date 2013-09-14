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
package org.dozer.spring.functional_tests.support;

import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.spring.vo.Destination;
import org.dozer.spring.vo.Source;

import static org.dozer.loader.api.FieldsMappingOptions.*;

/**
 * @author Maurus Cuelenaere
 */
public class SampleBeanMappingBuilder extends BeanMappingBuilder {

  @Override
  protected void configure() {
    mapping(Source.class, Destination.class)
      .fields("name", "value", copyByReference());
  }

}
