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
package org.dozer.vo.inheritance;

import org.dozer.vo.BaseTestObject;

public class BaseSubClassCombined extends BaseTestObject {

  private String subAttribute2;
  private String baseAttribute2;

  public String getBaseAttribute2() {
    return baseAttribute2;
  }

  public void setBaseAttribute2(String baseAttribute) {
    this.baseAttribute2 = baseAttribute;
  }

  public String getSubAttribute2() {
    return subAttribute2;
  }

  public void setSubAttribute2(String subAttribute) {
    this.subAttribute2 = subAttribute;
  }

}
