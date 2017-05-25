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

import java.util.List;
import java.util.Set;

/**
 * @author wojtek.kiersztyn
 * @author dominic.peciuch
 * 
 */
public class Individuals {

  private List usernames;
  private Individual individual;
  private String simpleField;
  private String[] secondNames;
  private Aliases aliases;
  private Set addressSet;
  private String[] thirdNames;

  public List getUsernames() {
    return usernames;
  }

  public void setUsernames(List usernames) {
    this.usernames = usernames;
  }

  public Individual getIndividual() {
    return individual;
  }

  public void setIndividual(Individual individual) {
    this.individual = individual;
  }

  public String getSimpleField() {
    return simpleField;
  }

  public void setSimpleField(String simpleField) {
    this.simpleField = simpleField;
  }

  public String[] getSecondNames() {
    return secondNames;
  }

  public void setSecondNames(String[] secondNames) {
    this.secondNames = secondNames;
  }

  public Aliases getAliases() {
    return aliases;
  }

  public void setAliases(Aliases aliases) {
    this.aliases = aliases;
  }

  public Set getAddressSet() {
    return addressSet;
  }

  public void setAddressSet(Set addressSet) {
    this.addressSet = addressSet;
  }

  public String getThirdNameElement1() {
    return this.thirdNames[0];
  }

}
