package org.dozer.factory;

public class BeanCreationDirective {

  private Object srcObject;
  private Class<?> srcClass;
  private Class<?> targetClass;
  private Class<?> alternateClass;
  private String factoryName;
  private String factoryId;
  private String createMethod;

  public BeanCreationDirective() {
  }

  public BeanCreationDirective(Object srcObject, Class<?> srcClass, Class<?> targetClass, Class<?> alternateClass, String factoryName, String factoryId, String createMethod) {
    this.srcObject = srcObject;
    this.srcClass = srcClass;
    this.targetClass = targetClass;
    this.alternateClass = alternateClass;
    this.factoryName = factoryName;
    this.factoryId = factoryId;
    this.createMethod = createMethod;
  }

  public Object getSrcObject() {
    return srcObject;
  }

  public Class<?> getSrcClass() {
    return srcClass;
  }

  public Class<?> getTargetClass() {
    return targetClass;
  }

  public Class<?> getAlternateClass() {
    return alternateClass;
  }

  public String getFactoryName() {
    return factoryName;
  }

  public String getFactoryId() {
    return factoryId;
  }

  public String getCreateMethod() {
    return createMethod;
  }

  public void setSrcObject(Object srcObject) {
    this.srcObject = srcObject;
  }

  public void setSrcClass(Class<?> srcClass) {
    this.srcClass = srcClass;
  }

  public void setTargetClass(Class<?> targetClass) {
    this.targetClass = targetClass;
  }

  public void setAlternateClass(Class<?> alternateClass) {
    this.alternateClass = alternateClass;
  }

  public void setFactoryName(String factoryName) {
    this.factoryName = factoryName;
  }

  public void setFactoryId(String factoryId) {
    this.factoryId = factoryId;
  }

  public void setCreateMethod(String createMethod) {
    this.createMethod = createMethod;
  }

  public Class<?> getActualClass() {
    if (targetClass != null) {
      return targetClass;
    } else {
      return alternateClass;
    }
  }

}
