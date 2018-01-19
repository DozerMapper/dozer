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


/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class SrcDeepObj extends BaseTestObject {
  private SrcNestedDeepObj srcNestedObj;
  private String sameNameField;
  private List someList;

  public SrcNestedDeepObj getSrcNestedObj() {
    return srcNestedObj;
  }
  public void setSrcNestedObj(SrcNestedDeepObj srcNestedObj) {
    this.srcNestedObj = srcNestedObj;
  }
  public String getSameNameField() {
    return sameNameField;
  }
  public void setSameNameField(String sameNameField) {
    this.sameNameField = sameNameField;
  }
  public List getSomeList() {
    return someList;
  }
  public void setSomeList(List someList) {
    this.someList = someList;
  }

}
