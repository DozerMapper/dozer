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

import java.util.Map;

import org.dozer.vo.BaseTestObject;
import org.dozer.vo.TestObject;


/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class PropertyToMap extends BaseTestObject {

  private String stringProperty;
  private String stringProperty2;
  private String nullStringProperty;
  private String stringProperty3;
  private String stringProperty4;
  private String stringProperty5;
  private String stringProperty6;
  private String excludeMe;
  private Map reverseMap;
  private String reverseClassLevelMapString;
  private TestObject testObject;
  private String integerProperty;

  public String getStringProperty() {
    return stringProperty;
  }

  public void setStringProperty(String stringProperty) {
    this.stringProperty = stringProperty;
  }

  public String getStringProperty2() {
    return stringProperty2;
  }

  public void setStringProperty2(String stringProperty2) {
    this.stringProperty2 = stringProperty2;
  }

  public void addStringProperty2(String stringProperty2) {
    this.stringProperty2 = stringProperty2;
  }

  public String getNullStringProperty() {
    return nullStringProperty;
  }

  public void setNullStringProperty(String nullStringProperty) {
    this.nullStringProperty = nullStringProperty;
  }

  public String getStringProperty3() {
    return stringProperty3;
  }

  public void setStringProperty3(String stringProperty3) {
    this.stringProperty3 = stringProperty3;
  }

  public String getStringProperty4() {
    return stringProperty4;
  }

  public void setStringProperty4(String stringProperty4) {
    this.stringProperty4 = stringProperty4;
  }

  public String getStringProperty5() {
    return stringProperty5;
  }

  public void setStringProperty5(String stringProperty5) {
    this.stringProperty5 = stringProperty5;
  }

  public String getExcludeMe() {
    return excludeMe;
  }

  public void setExcludeMe(String excludeMe) {
    this.excludeMe = excludeMe;
  }

  public Map getReverseMap() {
    return reverseMap;
  }

  public void setReverseMap(Map reverseMap) {
    this.reverseMap = reverseMap;
  }

  public String getReverseClassLevelMapString() {
    return reverseClassLevelMapString;
  }

  public void setReverseClassLevelMapString(String reverseClassLevelMapString) {
    this.reverseClassLevelMapString = reverseClassLevelMapString;
  }

  public TestObject getTestObject() {
    return testObject;
  }

  public void setTestObject(TestObject testObject) {
    this.testObject = testObject;
  }

  public String getIntegerProperty() {
    return integerProperty;
  }

  public void setIntegerProperty(String integerProperty) {
    this.integerProperty = integerProperty;
  }

  public String getStringProperty6() {
    return stringProperty6;
  }

  public void setStringProperty6(String stringProperty6) {
    this.stringProperty6 = stringProperty6;
  }

}
