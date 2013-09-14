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

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Dmitry Buzdin
 */
public class ReferencingBean {

  @Autowired
  Mapper mapper;

  public Mapper getMapper() {
    return mapper;
  }

}
