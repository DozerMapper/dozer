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
package org.dozer.vo;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class SuperClass extends SuperSuperClass {
  private String superAttribute;
  private java.util.List superList;
  private String superFieldToExclude;

  public java.util.List getSuperList() {
    return superList;
  }

  public void setSuperList(java.util.List superList) {
    this.superList = superList;
  }

  public String getSuperAttribute() {
    return superAttribute;
  }

  public void setSuperAttribute(String superAttribute) {
    this.superAttribute = superAttribute;
  }

  public String getSuperFieldToExclude() {
    return superFieldToExclude;
  }

  public void setSuperFieldToExclude(String superFieldToExclude) {
    this.superFieldToExclude = superFieldToExclude;
  }
}
