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
package org.dozer.vo.map;

import org.dozer.vo.BaseTestObject;

/**
 * @author tierney.matt
 */
public class SimpleObjPrime extends BaseTestObject {

  private String field1;
  private String field2;
  private String simpleobjprimefield;
  private NestedObjPrime nested;
  private NestedObjPrime nested2;

  public String getField1() {
    return field1;
  }

  public void setField1(String field1) {
    this.field1 = field1;
  }

  public NestedObjPrime getNested() {
    return nested;
  }

  public void setNested(NestedObjPrime nested) {
    this.nested = nested;
  }

  public NestedObjPrime getNested2() {
    return nested2;
  }

  public void setNested2(NestedObjPrime nested2) {
    this.nested2 = nested2;
  }

  public String getSimpleobjprimefield() {
    return simpleobjprimefield;
  }

  public void setSimpleobjprimefield(String simpleobjprimefield) {
    this.simpleobjprimefield = simpleobjprimefield;
  }

  public String getField2() {
    return field2;
  }

  public void setField2(String field2) {
    this.field2 = field2;
  }
}
