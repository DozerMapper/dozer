package org.dozer.vo.deepindex.customconverter;

public class First {

  private Second secondArray[];
  private Second second;

  public Second getSecond() {
    return second;
  }

  public void setSecond(Second second) {
    this.second = second;
  }

  public First() {
    secondArray = new Second[10];
    second = new Second();
    secondArray[0] = new Second();
  }

  public Second[] getSecondArray() {
    return secondArray;
  }

  public void setSecondArray(Second secondArray[]) {
    this.secondArray = secondArray;
  }
}
