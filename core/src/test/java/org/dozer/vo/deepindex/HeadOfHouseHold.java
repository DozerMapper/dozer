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
package org.dozer.vo.deepindex;

import org.dozer.vo.BaseTestObject;

public class HeadOfHouseHold extends BaseTestObject {
  private String firstName;
  private String lastName;
  private Integer salary;
  private String petName;
  private String petAge;
  private String offSpringName;

  public String getOffSpringName() {
    return offSpringName;
  }
  public void setOffSpringName(String offSpringName) {
    this.offSpringName = offSpringName;
  }
  public String getPetAge() {
    return petAge;
  }
  public void setPetAge(String petAge) {
    this.petAge = petAge;
  }
  public String getPetName() {
    return petName;
  }
  public void setPetName(String petName) {
    this.petName = petName;
  }
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  public Integer getSalary() {
    return salary;
  }
  public void setSalary(Integer salary) {
    this.salary = salary;
  }

}
