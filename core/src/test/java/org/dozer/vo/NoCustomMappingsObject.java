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

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class NoCustomMappingsObject extends BaseTestObject {
  private String stringDataType;
  private Map mapDataType;
  private Integer three;
  private long four;
  private float five;
  private Double six;
  private int seven;
  private Set setDataType;
  private Date date;

  public NoCustomMappingsObject() {
  }

  public NoCustomMappingsObject(String input) {
    this.stringDataType = input;
  }

  public void setStringDataType(String one) {
    this.stringDataType = one;
  }

  public String getStringDataType() {
    return stringDataType;
  }

  public Map getMapDataType() {
    return mapDataType;
  }

  public void setMapDataType(Map mapDataType) {
    this.mapDataType = mapDataType;
  }

  public float getFive() {
    return five;
  }

  public long getFour() {
    return four;
  }

  public Double getSix() {
    return six;
  }

  public Integer getThree() {
    return three;
  }

  public void setThree(Integer three) {
    this.three = three;
  }

  public void setSix(Double six) {
    this.six = six;
  }

  public void setFour(long four) {
    this.four = four;
  }

  public void setFive(float five) {
    this.five = five;
  }

  public int getSeven() {
    return seven;
  }

  public void setSeven(int seven) {
    this.seven = seven;
  }

  public Set getSetDataType() {
    return setDataType;
  }

  public void setSetDataType(Set setDataType) {
    this.setDataType = setDataType;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
