/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.event;

import net.sf.dozer.classmap.ClassMap;
import net.sf.dozer.fieldmap.FieldMap;

/**
 * Public class containing event information.
 * 
 * @author garsombke.franz
 * 
 */
public class DozerEvent {

  private DozerEventType type;
  private ClassMap classMap;
  private FieldMap fieldMap;
  private Object sourceObject;
  private Object destinationObject;
  private Object destinationValue;

  public DozerEvent(DozerEventType type, ClassMap classMap, FieldMap fieldMap, Object sourceObject, Object destinationObject,
      Object destinationValue) {
    this.type = type;
    this.classMap = classMap;
    this.fieldMap = fieldMap;
    this.sourceObject = sourceObject;
    this.destinationObject = destinationObject;
    this.destinationValue = destinationValue;
  }

  public DozerEventType getType() {
    return type;
  }

  public ClassMap getClassMap() {
    return classMap;
  }

  public Object getDestinationObject() {
    return destinationObject;
  }

  public Object getDestinationValue() {
    return destinationValue;
  }

  public FieldMap getFieldMap() {
    return fieldMap;
  }

  public Object getSourceObject() {
    return sourceObject;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    if (getClassMap() != null) {
      sb.append("Type:" + type + "\n");
    }
    if (getClassMap() != null) {
      sb.append("ClassMap:" + getClassMap().toString() + "\n");
    }
    if (getFieldMap() != null) {
      sb.append("FieldMap:" + getFieldMap().toString() + "\n");
    }
    if (getSourceObject() != null) {
      sb.append("SourceObject:" + getSourceObject().toString() + "\n");
    }
    if (getDestinationObject() != null) {
      sb.append("DestinationObject:" + getDestinationObject().toString() + "\n");
    }
    if (getDestinationValue() != null) {
      sb.append("DestinationValue:" + getDestinationValue().toString() + "\n");
    }
    return sb.toString();
  }
}