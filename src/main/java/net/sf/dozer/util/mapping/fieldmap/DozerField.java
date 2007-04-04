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
package net.sf.dozer.util.mapping.fieldmap;


/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class DozerField implements Cloneable {
  private final String type;
  private final String name;
  private String dateFormat;
  private String theGetMethod;
  private String theSetMethod;
  private String key;
  private String mapSetMethod;
  private String mapGetMethod;
  private boolean isAccessible;
  private String createMethod;
  private boolean isIndexed;
  private int index = -1;    

  public DozerField(String name, String type) {
    this.type = type;
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public String toString() {
    return getName() + " : " + getType();
  }

  public String getTheGetMethod() {
    return theGetMethod;
  }

  public void setTheGetMethod(String theGetMethod) {
    this.theGetMethod = theGetMethod;
  }

  public String getTheSetMethod() {
    return theSetMethod;
  }

  public void setTheSetMethod(String theSetMethod) {
    this.theSetMethod = theSetMethod;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getMapGetMethod() {
    return mapGetMethod;
  }

  public void setMapGetMethod(String mapGetMethod) {
    this.mapGetMethod = mapGetMethod;
  }

  public String getMapSetMethod() {
    return mapSetMethod;
  }

  public void setMapSetMethod(String mapSetMethod) {
    this.mapSetMethod = mapSetMethod;
  }

  public boolean isAccessible() {
    return isAccessible;
  }

  public void setAccessible(boolean isAccessible) {
    this.isAccessible = isAccessible;
  }

  public String getCreateMethod() {
    return createMethod;
  }

  public void setCreateMethod(String createMethod) {
    this.createMethod = createMethod;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public boolean isIndexed() {
    return isIndexed;
  }

  public void setIndexed(boolean isIndexed) {
    this.isIndexed = isIndexed;
  }

}