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
package org.dozer.event;

import org.dozer.classmap.ClassMap;
import org.dozer.fieldmap.FieldMap;

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

  // TODO Create Object instances only if Event Listeners are registered;
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

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    if (getClassMap() != null) {
      sb.append("Type:").append(type).append("\n");
    }
    if (getClassMap() != null) {
      sb.append("ClassMap:").append(getClassMap().toString()).append("\n");
    }
    if (getFieldMap() != null) {
      sb.append("FieldMap:").append(getFieldMap().toString()).append("\n");
    }
    if (getSourceObject() != null) {
      sb.append("SourceObject:").append(getSourceObject().toString()).append("\n");
    }
    if (getDestinationObject() != null) {
      sb.append("DestinationObject:").append(getDestinationObject().toString()).append("\n");
    }
    if (getDestinationValue() != null) {
      sb.append("DestinationValue:").append(getDestinationValue().toString()).append("\n");
    }
    return sb.toString();
  }
}