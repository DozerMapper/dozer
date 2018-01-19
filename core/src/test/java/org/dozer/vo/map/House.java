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

import java.util.List;

import org.dozer.vo.BaseTestObject;

public class House extends BaseTestObject {

  private String houseName;
  private Room room;
  private List<String> bathrooms;

  public String getHouseName() {
    return houseName;
  }
  public void setHouseName(String houseName) {
    this.houseName = houseName;
  }
  public Room getRoom() {
    return room;
  }
  public void setRoom(Room room) {
    this.room = room;
  }
  public List<String> getBathrooms() {
    return bathrooms;
  }
  public void setBathrooms(List<String> bathrooms) {
    this.bathrooms = bathrooms;
  }

}
