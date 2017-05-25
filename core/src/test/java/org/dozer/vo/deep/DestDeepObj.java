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

import java.util.List;

import org.dozer.vo.BaseTestObject;
import org.dozer.vo.FurtherTestObject;


/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class DestDeepObj extends BaseTestObject {

  private String dest1;
  private Integer dest2;
  private int dest3;
  private List dest4;
  private String dest5;
  private FurtherTestObject dest6;
  private String sameNameField;
  private List hintList;
  private List hintList2;

  public String getDest1() {
    return dest1;
  }
  public Integer getDest2() {
    return dest2;
  }
  public int getDest3() {
    return dest3;
  }
  public void setDest3(int dest3) {
    this.dest3 = dest3;
  }
  public void setDest2(Integer dest2) {
    this.dest2 = dest2;
  }
  public void setDest1(String dest1) {
    this.dest1 = dest1;
  }
  public String getSameNameField() {
    return sameNameField;
  }
  public void setSameNameField(String sameNameField) {
    this.sameNameField = sameNameField;
  }
  public List getDest4() {
    return dest4;
  }
  public void setDest4(List dest4) {
    this.dest4 = dest4;
  }
  public String getDest5() {
    return dest5;
  }
  public void setDest5(String dest5) {
    this.dest5 = dest5;
  }
  public FurtherTestObject getDest6() {
    return dest6;
  }
  public void setDest6(FurtherTestObject dest6) {
    this.dest6 = dest6;
  }
  public List getHintList() {
    return hintList;
  }
  public void setHintList(List hintList) {
    this.hintList = hintList;
  }
  public List getHintList2() {
    return hintList2;
  }
  public void setHintList2(List hintList2) {
    this.hintList2 = hintList2;
  }
}
