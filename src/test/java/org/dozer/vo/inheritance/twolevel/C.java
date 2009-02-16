package org.dozer.vo.inheritance.twolevel;

import java.io.Serializable;

/**
 * @author Dmitry Buzdin
 */
public class C implements Serializable {

  private String a;
  private String b;

  public String getA() {
    return a;
  }

  public void setA(String a) {
    this.a = a;
  }

  public String getB() {
    return b;
  }

  public void setB(String b) {
    this.b = b;
  }
}
