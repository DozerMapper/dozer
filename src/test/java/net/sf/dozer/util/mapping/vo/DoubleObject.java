/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util.mapping.vo;

import java.io.Serializable;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class DoubleObject implements Serializable {
  private double value;

  public DoubleObject() {
  }

  public DoubleObject(double value) {
    this.value = value;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof DoubleObject))
      return false;

    final DoubleObject doubleObject = (DoubleObject) o;

    if (value != doubleObject.value)
      return false;

    return true;
  }

  public int hashCode() {
    final long temp = value != +0.0d ? Double.doubleToLongBits(value) : 0l;
    return (int) (temp ^ (temp >>> 32));
  }

  public String toString() {
    return String.valueOf(value);
  }

}