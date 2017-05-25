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

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class SimpleObj extends BaseTestObject {

  private String field1;
  private Integer field2;
  private BigDecimal field3;
  private Double field4;
  private Calendar field5;
  private String field6;
  private Boolean field7;

  public String getField1() {
    return field1;
  }
  public void setField1(String field1) {
    this.field1 = field1;
  }
  public Integer getField2() {
    return field2;
  }
  public void setField2(Integer field2) {
    this.field2 = field2;
  }
  public BigDecimal getField3() {
    return field3;
  }
  public void setField3(BigDecimal field3) {
    this.field3 = field3;
  }
  public Double getField4() {
    return field4;
  }
  public void setField4(Double field4) {
    this.field4 = field4;
  }
  public Calendar getField5() {
    return field5;
  }
  public void setField5(Calendar field5) {
    this.field5 = field5;
  }
  public String getField6() {
    return field6;
  }
  public void setField6(String field6) {
    this.field6 = field6;
  }

  public Boolean getField7() {
    return field7;
  }

  public void setField7(Boolean field7) {
    this.field7 = field7;
  }
}
