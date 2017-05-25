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

import java.util.List;
import java.util.Set;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class TestReferenceObject extends BaseTestObject {
  List listA;
  private Object[] arrayToArrayCumulative;
  private Object[] arrayToArrayNoncumulative;
  private List listToArray;
  private int[] primitiveArray;
  private Integer[] primitiveArrayWrapper;
  private Set setToSet;
  private Car[] cars;
  private List vehicles;

  public Car[] getCars() {
    return cars;
  }

  public void setCars(Car[] cars) {
    this.cars = cars;
  }

  public Set getSetToSet() {
    return setToSet;
  }

  public void setSetToSet(Set setToSet) {
    this.setToSet = setToSet;
  }

  public Integer[] getPrimitiveArrayWrapper() {
    return primitiveArrayWrapper;
  }

  public void setPrimitiveArrayWrapper(Integer[] primitiveArrayWrapper) {
    this.primitiveArrayWrapper = primitiveArrayWrapper;
  }

  public int[] getPrimitiveArray() {
    return primitiveArray;
  }

  public void setPrimitiveArray(int[] primitiveArray) {
    this.primitiveArray = primitiveArray;
  }

  public List getListA() {
    return listA;
  }

  public void setListA(List listA) {
    this.listA = listA;
  }

  public Object[] getArrayToArrayCumulative() {
    return arrayToArrayCumulative;
  }

  public void setArrayToArrayCumulative(Object[] arrayToArray) {
    this.arrayToArrayCumulative = arrayToArray;
  }

  public List getListToArray() {
    return listToArray;
  }

  public void setListToArray(List listToArray) {
    this.listToArray = listToArray;
  }

  public Object[] getArrayToArrayNoncumulative() {
    return arrayToArrayNoncumulative;
  }

  public void setArrayToArrayNoncumulative(Object[] arrayToArrayNoncumulative) {
    this.arrayToArrayNoncumulative = arrayToArrayNoncumulative;
  }

  public List getVehicles() {
    return vehicles;
  }

  public void setVehicles(List vehicles) {
    this.vehicles = vehicles;
  }
}