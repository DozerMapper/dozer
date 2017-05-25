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
package org.dozer.vo.inheritance.cc;

import org.dozer.CustomConverter;

/**
 * This is the base class for creating all dummy-objects.
 * If you need a concrete implementation, create it and implement
 * the method createBo, where you must return the concrete dummy-object.
 * 
 * @author kammermannf
 */
public class InheritanceBugConverter implements CustomConverter {

  /**
   * The convert-method
   * 
   * @param destination
   * @param source
   * @param destClass
   * @param sourceClass
   * @return
   */
  public Object convert(Object destination, Object source, Class destClass, Class sourceClass) {
    return "customConverter";
  }
}
