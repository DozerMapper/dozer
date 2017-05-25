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

import java.util.List;

import org.dozer.vo.BaseTestObject;
import org.dozer.vo.MetalThingyIF;
import org.dozer.vo.Van;


/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class House extends BaseTestObject {
  private List rooms;
  private Person owner;
  private int price;
  private Van van;
  private Address address;
  private MetalThingyIF thingy;
  private List customSetGetMethod;

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Person getOwner() {
    return owner;
  }

  public void setOwner(Person owner) {
    this.owner = owner;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public List getRooms() {
    return rooms;
  }

  public void setRooms(List rooms) {
    this.rooms = rooms;
  }

  public Van getVan() {
    return van;
  }

  public void setVan(Van van) {
    this.van = van;
  }

  public MetalThingyIF getThingy() {
    return thingy;
  }

  public void setThingy(MetalThingyIF thingy) {
    this.thingy = thingy;
  }

  public List getCustomSetGetMethod() {
    return customSetGetMethod;
  }

  public void setCustomSetGetMethod(List customSetMethod) {
    this.customSetGetMethod = customSetMethod;
  }
}
