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
package org.dozer.vo.deepindex.customconverter;

public class Second {

  private Third thirdArray[];
  private Third third;

  public Third getThird() {
    return third;
  }

  public void setThird(Third third) {
    this.third = third;
  }

  public Second() {
    thirdArray = new Third[10];
    third = new Third();
    thirdArray[7] = new Third();
    thirdArray[7].setName("asdasdfasdf");
  }

  public Third[] getThirdArray() {
    return thirdArray;
  }

  public void setThirdArray(Third thirdArray[]) {
    this.thirdArray = thirdArray;
  }
}
