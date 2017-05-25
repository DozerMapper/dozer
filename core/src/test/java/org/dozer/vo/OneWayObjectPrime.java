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

import java.util.ArrayList;
import java.util.List;

public class OneWayObjectPrime extends BaseTestObject {
  private String oneWayPrimeField;
  private List stringList = new ArrayList();
  private String setOnlyField;

  public String getOneWayPrimeField() {
    return oneWayPrimeField;
  }

  public void setOneWayPrimeField(String oneWayField) {
    this.oneWayPrimeField = oneWayField;
  }

  public List getStringList() {
    return stringList;
  }

  public void setStringList(List stringList) {
    this.stringList = stringList;
  }

  public void addValue(String value) {
    this.stringList.add(value);
  }

  public void setSetOnlyField(String setOnly) {
    this.setOnlyField = setOnly;
  }
}