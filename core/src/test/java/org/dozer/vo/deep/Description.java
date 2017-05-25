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
package org.dozer.vo.deep;

import java.util.ArrayList;
import java.util.List;

import org.dozer.vo.BaseTestObject;
import org.dozer.vo.Van;


/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class Description extends BaseTestObject {

  private Room[] rooms;
  private String ownerName;
  private String street;
  private String city;
  private String myName;
  private List someOwners;
  private List customSetGetMethod = new ArrayList();

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public Room[] getRooms() {
    return rooms;
  }

  public void setRooms(Room[] rooms) {
    this.rooms = rooms;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getMyName() {
    return myName;
  }

  public void setMyName(String myName) {
    this.myName = myName;
  }

  public List getSomeOwners() {
    return someOwners;
  }

  public void setSomeOwners(List someOwners) {
    this.someOwners = someOwners;
  }

  public List getCustomSetGetMethod() {
    return customSetGetMethod;
  }

  public void setCustom(Van van) {
    this.customSetGetMethod.add(van);
  }

}
