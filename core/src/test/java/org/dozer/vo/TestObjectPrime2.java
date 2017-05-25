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

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class TestObjectPrime2 extends BaseTestObject {
  private String one;
  private Integer two;
  private InsideTestObject three;
  private InsideTestObject insideTestObject;
  private java.util.List equalNamedList;
  private java.util.List unequalNamedList;
  private int thePrimitive;
  private int theMappedPrimitive;
  private int[] anArray;
  private Integer[] arrayForLists;
  private BigDecimal bigDecimalToInt;
  private int intToBigDecimal;
  private Date date;

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public InsideTestObject getInsideTestObject() {
    return insideTestObject;
  }

  public void setInsideTestObject(InsideTestObject insideTestObject) {
    this.insideTestObject = insideTestObject;
  }

  public int getThePrimitive() {
    return thePrimitive;
  }

  public void setThePrimitive(int primitive) {
    thePrimitive = primitive;
  }

  public java.util.List getEqualNamedList() {
    return equalNamedList;
  }

  public void setEqualNamedList(java.util.List equalNamedList) {
    this.equalNamedList = equalNamedList;
  }

  public String getOne() {
    return one;
  }

  public void setOne(String one) {
    this.one = one;
  }

  public InsideTestObject getThree() {
    return three;
  }

  public void setThree(InsideTestObject three) {
    this.three = three;
  }

  public Integer getTwo() {
    return two;
  }

  public void setTwo(Integer two) {
    this.two = two;
  }

  public java.util.List getUnequalNamedList() {
    return unequalNamedList;
  }

  public void setUnequalNamedList(java.util.List unequalNamedList) {
    this.unequalNamedList = unequalNamedList;
  }

  public int getTheMappedPrimitive() {
    return theMappedPrimitive;
  }

  public void setTheMappedPrimitive(int mappedPrimitive) {
    theMappedPrimitive = mappedPrimitive;
  }

  public int[] getAnArray() {
    return anArray;
  }

  public void setAnArray(int[] anArray) {
    this.anArray = anArray;
  }

  public Integer[] getArrayForLists() {
    return arrayForLists;
  }

  public void setArrayForLists(Integer[] arrayForLists) {
    this.arrayForLists = arrayForLists;
  }

  public BigDecimal getBigDecimalToInt() {
    return bigDecimalToInt;
  }

  public void setBigDecimalToInt(BigDecimal bigDecimalToInt) {
    this.bigDecimalToInt = bigDecimalToInt;
  }

  public int getIntToBigDecimal() {
    return intToBigDecimal;
  }

  public void setIntToBigDecimal(int intToBigDecimal) {
    this.intToBigDecimal = intToBigDecimal;
  }
}
