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
package org.dozer.vo.km;

/**
 * @author garsombke.franz
 */
public class Super {
  private String age;
  private String loginName;
  private Property property;

  public String getAge() {
    return age;
  }
  public void setAge(String age) {
    this.age = age;
  }
  public String getLoginName() {
    return loginName;
  }
  public void setLoginName(String name) {
    this.loginName = name;
  }
  public Property getProperty() {
    return property;
  }
  public void setProperty(Property property) {
    this.property = property;
  }
}
