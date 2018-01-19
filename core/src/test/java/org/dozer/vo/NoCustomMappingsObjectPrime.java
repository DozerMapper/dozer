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
public class NoCustomMappingsObjectPrime extends BaseTestObject {

  private String stringDataType;
  private Map mapDataType;
  private double three;
  private Integer four;
  private Long five;
  private String six;
  private String seven;
  private Set setDataType;
  private Date date;

  public void setStringDataType(String one) {
    this.stringDataType = one;
  }

  public String getStringDataType() {
    return stringDataType;
  }

  public Map getMapDataType() {
    return mapDataType;
  }

  public void setMapDataType(Map two) {
    this.mapDataType = two;
  }

  public Long getFive() {
    return five;
  }

  public Integer getFour() {
    return four;
  }

  public String getSeven() {
    return seven;
  }

  public String getSix() {
    return six;
  }

  public double getThree() {
    return three;
  }

  public void setThree(double three) {
    this.three = three;
  }

  public void setSix(String six) {
    this.six = six;
  }

  public void setSeven(String seven) {
    this.seven = seven;
  }

  public void setFour(Integer four) {
    this.four = four;
  }

  public void setFive(Long five) {
    this.five = five;
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
