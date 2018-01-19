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
package org.dozer.vo.orphan;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParentPrime {

  private Long id;
  private String name;
  private List childrenList;
  private Set childrenSet;
  private Map childrenMap;

  private ParentPrime() {
  }

  public ParentPrime(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List getChildrenList() {
    return childrenList;
  }

  public void setChildrenList(List childrenList) {
    this.childrenList = childrenList;
  }

  public Set getChildrenSet() {
    return childrenSet;
  }

  public void setChildrenSet(Set childrenSet) {
    this.childrenSet = childrenSet;
  }

  public Map getChildrenMap() {
    return childrenMap;
  }

  public void setChildrenMap(Map childrenMap) {
    this.childrenMap = childrenMap;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ParentPrime) {
      ParentPrime castObj = (ParentPrime) o;
      return id.equals(castObj.getId());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

}
