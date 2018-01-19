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
package org.dozer.functional_tests.support;

import org.dozer.DozerConverter;

/**
 * @author dmitry.buzdin
 */
public class MyDozerConverter extends DozerConverter<String, Boolean> {

  public MyDozerConverter() {
    super(String.class, Boolean.class);
  }

  public Boolean convertTo(String source, Boolean destination) {
    if ("yes".equals(source)) {
      return Boolean.TRUE;
    } else if ("no".equals(source)) {
      return Boolean.FALSE;
    } else if (getParameter().equals(source)) {
      return null;
    }
    throw new IllegalStateException("Unknown value!");
  }

  public String convertFrom(Boolean source, String destination) {
    if (Boolean.TRUE.equals(source)) {
      return "yes";
    } else if (Boolean.FALSE.equals(source)) {
      return "no";
    } else if (source == null) {
      return getParameter();
    }
    throw new IllegalStateException("Unknown value!");
  }

}
