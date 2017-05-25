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
package org.dozer.vo.deep;

import org.dozer.vo.BaseTestObject;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class Person extends BaseTestObject {
  private String name;
  private String yourName;
  private int[] prim = { 1, 2, 3 };

  public int[] getPrim() {
    return prim;
  }

  public void setPrim(int[] prim) {
    this.prim = prim;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getYourName() {
    return yourName;
  }

  public void setYourName(String yourName) {
    this.yourName = yourName;
  }
}
