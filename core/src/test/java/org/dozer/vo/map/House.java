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
