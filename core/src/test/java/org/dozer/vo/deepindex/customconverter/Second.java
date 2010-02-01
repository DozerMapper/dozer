package org.dozer.vo.deepindex.customconverter;

public class Second {

  private Third thirdArray[];
  private Third third;

  public Third getThird() {
    return third;
  }

  public void setThird(Third third) {
    this.third = third;
  }

  public Second() {
    thirdArray = new Third[10];
    third = new Third();
    thirdArray[7] = new Third();
    thirdArray[7].setName("asdasdfasdf");
  }

  public Third[] getThirdArray() {
    return thirdArray;
  }

  public void setThirdArray(Third thirdArray[]) {
    this.thirdArray = thirdArray;
  }
}
