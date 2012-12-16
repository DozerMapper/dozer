package org.dozer.vo.proto;

/**
 * @author Dmitry Spikhalskiy
 */
public class ObjectWithEnumField {
  private SimpleEnum enumField;

  public ObjectWithEnumField() {
  }

  public ObjectWithEnumField(SimpleEnum enumField) {
    this.enumField = enumField;
  }

  public SimpleEnum getEnumField() {
    return enumField;
  }

  public void setEnumField(SimpleEnum enumField) {
    this.enumField = enumField;
  }
}
