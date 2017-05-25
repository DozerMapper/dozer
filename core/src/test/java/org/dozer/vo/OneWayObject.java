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

import org.dozer.vo.deep.SrcNestedDeepObj;

public class OneWayObject extends BaseTestObject {
  private String oneWayField;
  private SrcNestedDeepObj nested;
  private String stringToList;
  private String setOnly = "setOnly";

  public String getOneWayField() {
    return oneWayField;
  }

  public void setOneWayField(String oneWayField) {
    this.oneWayField = oneWayField;
  }

  public SrcNestedDeepObj getNested() {
    return nested;
  }

  public void setNested(SrcNestedDeepObj nested) {
    this.nested = nested;
  }

  public String getStringToList() {
    return stringToList;
  }

  public void setStringToList(String stringToList) {
    this.stringToList = stringToList;
  }

  public String getSetOnly() {
    return setOnly;
  }

}