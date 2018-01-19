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
package org.dozer.vo.generics.deepindex;

import java.util.List;

import org.dozer.vo.BaseTestObject;


/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class DestDeepObj extends BaseTestObject {
  private String dest5;
  private List<TestObjectPrime> hintList;

  public List<TestObjectPrime> getHintList() {
    return hintList;
  }
  public void setHintList(List<TestObjectPrime> hintList) {
    this.hintList = hintList;
  }
  public String getDest5() {
    return dest5;
  }
  public void setDest5(String dest5) {
    this.dest5 = dest5;
  }
}
