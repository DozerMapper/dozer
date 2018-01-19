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
package org.dozer.vo;

import org.dozer.CustomConverter;

/**
 * @author wojtek.kiersztyn
 * @author dominic.peciuch
 * 
 */
public class CToStringConverter implements CustomConverter {

  public Object convert(Object dest, Object source, Class destClass, Class sourceClass) {

    if (source instanceof C) {
      C c = (C) source;
      return c.getValue();

    } else if (source instanceof String) {
      C c = new C();
      c.setValue((String) source);
      return c;
    } else {
      System.out.println("wrong class type!!!!!!!!!!!!!!");
      return null;
    }
  }

}
