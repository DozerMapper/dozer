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
 * @author Hee Tatt Ooi
 */
public class ArrayDest {

  private final int preInitializedArraySize = 100;

  private String[] preInitializedArray = new String[preInitializedArraySize];

  private List<String> destList = new ArrayList<String>(100);

  private String[] array;
  
  private int[] primitiveIntArray ;

  public ArrayDest() {
    for (int i = 0; i < 10; i++) {
      destList.add(null);
    }
  }

  public String[] getPreInitializedArray() {
    return preInitializedArray;
  }

  public void setPreInitializedArray(String[] preInitializedArray) {
    if (preInitializedArray != null && (preInitializedArray.length > preInitializedArraySize)) {
//      throw new IllegalArgumentException("Length of array must be less then or equal to: " + preInitializedArraySize + " and is actually: " + preInitializedArray.length);
    }

    this.preInitializedArray = preInitializedArray;
  }

  public List<String> getDestList() {
    return destList;
  }

  public void setDestList(List<String> destList) {
    this.destList = destList;
  }

  public String[] getArray() {
    return array;
  }

  public void setArray(String[] array) {
    this.array = array;
  }

  public int[] getPrimitiveIntArray() {
    return primitiveIntArray;
  }

  public void setPrimitiveIntArray(int[] primitiveIntArray) {
    this.primitiveIntArray = primitiveIntArray;
  }
  
}
