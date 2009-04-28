/**
 * 
 */
package org.dozer.vo.generics.parameterized;

public class GenericType<AType, BType, CType> {

  /** generiv attributes */
  private AType a;
  private BType b;
  private CType c;

  /**
   * @return the a
   */
  public AType getA() {
    return a;
  }

  /**
   * @param a the a to set
   */
  public void setA(final AType a) {
    this.a = a;
  }

  /**
   * @return the b
   */
  public BType getB() {
    return b;
  }

  /**
   * @param b the b to set
   */
  public void setB(final BType b) {
    this.b = b;
  }

  /**
   * @return the c
   */
  public CType getC() {
    return c;
  }

  /**
   * @param c the c to set
   */
  public void setC(final CType c) {
    this.c = c;
  }

}
