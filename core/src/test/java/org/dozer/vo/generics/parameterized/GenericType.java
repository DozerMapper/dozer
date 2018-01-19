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
