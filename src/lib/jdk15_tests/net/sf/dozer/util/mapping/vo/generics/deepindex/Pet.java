package net.sf.dozer.util.mapping.vo.generics.deepindex;

public class Pet {
  private String petName;
  private int petAge;
  private Pet[] offSpring;

  public Pet() {
  }

  public Pet(String petName, int petAge, Pet[] offSpring) {
    this.petName = petName;
    this.petAge = petAge;
    this.offSpring = offSpring;
  }

  public Pet[] getOffSpring() {
    return offSpring;
  }
  public void setOffSpring(Pet[] offSpring) {
    this.offSpring = offSpring;
  }
  public int getPetAge() {
    return petAge;
  }
  public void setPetAge(int petAge) {
    this.petAge = petAge;
  }
  public String getPetName() {
    return petName;
  }
  public void setPetName(String petName) {
    this.petName = petName;
  }
}
