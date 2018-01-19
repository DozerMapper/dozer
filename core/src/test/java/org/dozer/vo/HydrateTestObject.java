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
import java.util.Iterator;
import java.util.List;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class HydrateTestObject extends BaseTestObject {
  private List apples = new ArrayList();
  private List oranges = new ArrayList();
  private List vehicles = new ArrayList();
  private List computers = new ArrayList();
  private List iterateCars = new ArrayList();
  private Car[] carArray;

  public List getApples() {
    return apples;
  }

  public void setApples(List apples) {
    this.apples = apples;
  }

  public List getOranges() {
    return oranges;
  }

  public void setOranges(List oranges) {
    this.oranges = oranges;
  }

  public List getVehicles() {
    return vehicles;
  }

  public void setVehicles(List vehicles) {
    this.vehicles = vehicles;
  }

  public void addComputer(AppleComputer car) {
    computers.add(car);
  }

  public void addCar(Car car) {
    this.vehicles.add(car);

  }

  public List getComputers() {
    return computers;
  }

  public void setComputers(List computers) {
    this.computers = computers;
  }

  public Iterator getIterateCars() {
    return iterateCars.iterator();
  }

  public void setIterateCars(List iterateCars) {
    this.iterateCars = iterateCars;
  }

  public void addIterateCar(Car car) {
    this.iterateCars.add(car);
  }

  public Car[] getCarArray() {
    return carArray;
  }

  public void setCarArray(Car[] carArray) {
    this.carArray = carArray;
  }
}
