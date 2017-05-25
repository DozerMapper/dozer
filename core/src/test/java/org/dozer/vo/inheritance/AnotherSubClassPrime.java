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
package org.dozer.vo.inheritance;

public class AnotherSubClassPrime extends IntermediateClass {

  private String subAttribute2;
  private java.util.List theListOfSubClassPrime;
  private SClassPrime sClass;
  private SClassPrime sClass2;

  public java.util.List getTheListOfSubClassPrime() {
    return theListOfSubClassPrime;
  }

  public void setTheListOfSubClassPrime(java.util.List subList2) {
    this.theListOfSubClassPrime = subList2;
  }

  public String getSubAttribute2() {
    return subAttribute2;
  }

  public void setSubAttribute2(String subAttribute) {
    this.subAttribute2 = subAttribute;
  }

  public SClassPrime getSClass() {
    return sClass;
  }

  public void setSClass(SClassPrime class1) {
    sClass = class1;
  }

  public SClassPrime getSClass2() {
    return sClass2;
  }

  public void setSClass2(SClassPrime class2) {
    sClass2 = class2;
  }

}
