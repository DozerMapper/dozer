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

import java.util.ArrayList;
import java.util.List;

/**
 * Put comments here
 *
 * @author Vadim Shaigorodskiy
 */
public class ArraySource {

  private final int preInitializedArraySize = 100;

  private String[] preInitializedArray = new String[preInitializedArraySize];

  private List<String> list = new ArrayList<String>();

  private String value;
  
  private List<Integer> listOfIntegers = new ArrayList<Integer>();

  public String[] getPreInitializedArray() {
    return preInitializedArray;
  }

  public void setPreInitializedArray(String[] preInitializedArray) {
    this.preInitializedArray = preInitializedArray;
  }

  public List<String> getList() {
    return list;
  }

  public void setList(List<String> list) {
    this.list = list;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public List<Integer> getListOfIntegers() {
    return listOfIntegers;
  }

  public void setListOfIntegers(List<Integer> listOfIntegers) {
    this.listOfIntegers = listOfIntegers;
  }
  
}
