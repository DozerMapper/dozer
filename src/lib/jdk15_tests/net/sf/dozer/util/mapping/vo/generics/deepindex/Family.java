package net.sf.dozer.util.mapping.vo.generics.deepindex;

import java.util.List;
import java.util.ArrayList;

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
