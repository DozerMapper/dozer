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

import java.util.HashMap;
import java.util.Map;

import org.dozer.vo.BaseTestObject;


/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class MapToProperty extends BaseTestObject {

  private Map hashMap = new HashMap();
  private Map nullHashMap;
  private CustomMapIF customMap = new CustomMap();
  private CustomMapIF nullCustomMap;
  private CustomMapIF customMapWithDiffSetMethod = new CustomMap();
  private String reverseMapString;
  private String reverseMapInteger;

  public Map getHashMap() {
    return hashMap;
  }

  public void setHashMap(Map hashMap) {
    this.hashMap = hashMap;
  }

  public CustomMapIF getCustomMap() {
    return customMap;
  }

  public void setCustomMap(CustomMapIF customMap) {
    this.customMap = customMap;
  }

  public Map getNullHashMap() {
    return nullHashMap;
  }

  public void setNullHashMap(Map nullHashMap) {
    this.nullHashMap = nullHashMap;
  }

  public CustomMapIF getNullCustomMap() {
    return nullCustomMap;
  }

  public void setNullCustomMap(CustomMapIF nullCustomMap) {
    this.nullCustomMap = nullCustomMap;
  }

  public CustomMapIF getCustomMapWithDiffSetMethod() {
    return customMapWithDiffSetMethod;
  }

  public void setCustomMapWithDiffSetMethod(CustomMapIF customMapWithDiffSetMethod) {
    this.customMapWithDiffSetMethod = customMapWithDiffSetMethod;
  }

  public String getReverseMapString() {
    return reverseMapString;
  }

  public void setReverseMapString(String reverseMapString) {
    this.reverseMapString = reverseMapString;
  }

  public String getReverseMapInteger() {
    return reverseMapInteger;
  }

  public void setReverseMapInteger(String reverseMapInteger) {
    this.reverseMapInteger = reverseMapInteger;
  }
}
