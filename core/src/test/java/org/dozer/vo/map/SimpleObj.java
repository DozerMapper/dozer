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

import java.util.Calendar;
import java.util.Map;

import org.dozer.vo.BaseTestObject;


/**
 * @author tierney.matt
 */
public class SimpleObj extends BaseTestObject {

  private String field1;
  private String field2;
  private String simpleobjfield;
  private Integer field3;
  private Integer field4;
  private Calendar field5;
  private NestedObj nested;
  private Map nested2;

  public String getField1() {
    return field1;
  }

  public void setField1(String field1) {
    this.field1 = field1;
  }

  public NestedObj getNested() {
    return nested;
  }

  public void setNested(NestedObj nested) {
    this.nested = nested;
  }

  public Map getNested2() {
    return nested2;
  }

  public void setNested2(Map nested2) {
    this.nested2 = nested2;
  }

  public String getSimpleobjfield() {
    return simpleobjfield;
  }

  public void setSimpleobjfield(String simpleobjfield) {
    this.simpleobjfield = simpleobjfield;
  }

  public String getField2() {
    return field2;
  }

  public void setField2(String field2) {
    this.field2 = field2;
  }

  public Integer getField3() {
    return field3;
  }

  public void setField3(Integer field3) {
    this.field3 = field3;
  }

  public Integer getField4() {
    return field4;
  }

  public void setField4(Integer field4) {
    this.field4 = field4;
  }

  public Calendar getField5() {
    return field5;
  }

  public void setField5(Calendar field5) {
    this.field5 = field5;
  }

}
