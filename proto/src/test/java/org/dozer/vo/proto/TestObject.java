/**
 * Copyright 2005-2013 Dozer Project
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
package org.dozer.vo.proto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class TestObject {
  private String one;
  private Integer two;

  public String getOne() {
    return one;
  }

  public void setOne(String one) {
    this.one = one;
  }

  public Integer getTwo() {
    return two;
  }

  public void setTwo(Integer two) {
    this.two = two;
  }
}
