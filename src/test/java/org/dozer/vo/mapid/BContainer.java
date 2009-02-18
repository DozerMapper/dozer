package org.dozer.vo.mapid;

import org.dozer.vo.BaseTestObject;
import org.dozer.vo.inheritance.B;

public class BContainer extends BaseTestObject {

  private B bProperty;

  public B getBProperty() {
    return bProperty;
  }

  public void setBProperty(B bProperty) {
    this.bProperty = bProperty;
  }

}
