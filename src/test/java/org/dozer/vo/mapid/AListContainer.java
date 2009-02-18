package org.dozer.vo.mapid;

import java.util.ArrayList;
import java.util.List;

import org.dozer.vo.BaseTestObject;

public class AListContainer extends BaseTestObject {

  private List aList = new ArrayList();

  public List getAList() {
    return aList;
  }

  public void setAList(List aList) {
    this.aList = aList;
  }

}
