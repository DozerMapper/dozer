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
package org.dozer.vo;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class InsideTestObjectPrime extends BaseTestObject {

  private String labelPrime;
  private MyPrimitiveWrapper wrapper;
  private MyPrimitiveWrapper anotherWrapper;
  private String createdByFactoryName;
  private NoExtendBaseObject copyByReference;
  private String deepInterfaceString;
  private String myField;

  public InsideTestObjectPrime() {
    super();
  }

  /**
   * @return Returns the wrapper.
   */
  public MyPrimitiveWrapper getWrapper() {
    return wrapper;
  }

  /**
   * @param wrapper
   *          The wrapper to set.
   */
  public void setWrapper(MyPrimitiveWrapper wrapper) {
    this.wrapper = wrapper;
  }

  /**
   * @return Returns the labelPrime.
   */
  public String getLabelPrime() {
    return labelPrime;
  }

  /**
   * @param labelPrime
   *          The labelPrime to set.
   */
  public void setLabelPrime(String labelPrime) {
    this.labelPrime = labelPrime;
  }

  /**
   * @return Returns the anotherWrapper.
   */
  public MyPrimitiveWrapper getAnotherWrapper() {
    return anotherWrapper;
  }

  /**
   * @param anotherWrapper
   *          The anotherWrapper to set.
   */
  public void setAnotherWrapper(MyPrimitiveWrapper anotherWrapper) {
    this.anotherWrapper = anotherWrapper;
  }

  public String getCreatedByFactoryName() {
    return createdByFactoryName;
  }

  public void setCreatedByFactoryName(String createdByFactoryName) {
    this.createdByFactoryName = createdByFactoryName;
  }

  public NoExtendBaseObject getCopyByReference() {
    return copyByReference;
  }

  public void setCopyByReference(NoExtendBaseObject copyByReference) {
    this.copyByReference = copyByReference;
  }

  public static InsideTestObjectPrime createMethod() {
    InsideTestObjectPrime itop = new InsideTestObjectPrime();
    itop.setMyField("myField");
    return itop;
  }

  public String getMyField() {
    return myField;
  }

  public void setMyField(String myField) {
    this.myField = myField;
  }

  public String getDeepInterfaceString() {
    return deepInterfaceString;
  }

  public void setDeepInterfaceString(String deepInterfaceString) {
    this.deepInterfaceString = deepInterfaceString;
  }
}
