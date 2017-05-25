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
import org.dozer.vo.FurtherTestObjectPrime;


/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class SrcNestedDeepObj extends BaseTestObject {
  private String src1;
  private Integer src2;
  private int src3;
  private String[] src4;
  private SrcNestedDeepObj2 srcNestedObj2;
  private FurtherTestObjectPrime src6;
  private List hintList;
  private List hintList2;

  public String getSrc1() {
    return src1;
  }
  public Integer getSrc2() {
    return src2;
  }
  public int getSrc3() {
    return src3;
  }
  public String[] getSrc4() {
    return src4;
  }
  public void setSrc4(String[] src4) {
    this.src4 = src4;
  }
  public void setSrc3(int src3) {
    this.src3 = src3;
  }
  public void setSrc2(Integer src2) {
    this.src2 = src2;
  }
  public void setSrc1(String src1) {
    this.src1 = src1;
  }
  public SrcNestedDeepObj2 getSrcNestedObj2() {
    return srcNestedObj2;
  }
  public void setSrcNestedObj2(SrcNestedDeepObj2 srcNestedObj2) {
    this.srcNestedObj2 = srcNestedObj2;
  }
  public FurtherTestObjectPrime getSrc6() {
    return src6;
  }
  public void setSrc6(FurtherTestObjectPrime src6) {
    this.src6 = src6;
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
