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
package org.dozer.vo.generics.deepindex;

import java.util.ArrayList;
import java.util.List;

public class Family {
  private List<PersonalDetails> familyMembers;
  private Pet[] pets;

  public Pet[] getPets() {
    return pets;
  }

  public void setPets(Pet[] pets) {
    this.pets = pets;
  }

  public Family() {
  }

  public Family(String firstMember, String secondMember, String lastName, Integer firstSal, Integer secondSal) {

    PersonalDetails first = new PersonalDetails();
    first.setFirstName(firstMember);
    first.setLastName(lastName);
    first.setSalary(firstSal);

    PersonalDetails second = new PersonalDetails();
    second.setFirstName(secondMember);
    second.setLastName(lastName);
    second.setSalary(secondSal);

    familyMembers = new ArrayList<PersonalDetails>();
    familyMembers.add(first);
    familyMembers.add(second);
  }

  public List<PersonalDetails> getFamilyMembers() {
    return familyMembers;
  }

  public void setFamilyMembers(List<PersonalDetails> familyMembers) {
    this.familyMembers = familyMembers;
  }
}
