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
package org.dozer.vo.enumtest;

/**
 * Bean for short from/to enum mapping test.
 * 
 * @author siarhei.krukau
 */
public class MyBeanPrimeShort {
  private short first;
  private Short second;

  public short getFirst() {
    return first;
  }
  public void setFirst(short first) {
    this.first = first;
  }
  public Short getSecond() {
    return second;
  }
  public void setSecond(Short second) {
    this.second = second;
  }
}
