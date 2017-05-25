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
 * Bean for enum from/to {@link String} mapping test.
 * 
 * @author siarhei.krukau
 */
public class MyBeanPrimeString {

  private String destType;

  private String destTypeWithOverride;

  public String getDestType() {
    return destType;
  }

  public void setDestType(String destType) {
    this.destType = destType;
  }

  public String getDestTypeWithOverride() {
    return destTypeWithOverride;
  }

  public void setDestTypeWithOverride(String destTypeWithOverride) {
    this.destTypeWithOverride = destTypeWithOverride;
  }
}
