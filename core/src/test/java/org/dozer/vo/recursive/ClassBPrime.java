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
package org.dozer.vo.recursive;

/**
 */
public class ClassBPrime implements Comparable<ClassBPrime> {
  /** */
  private String rue;

  /** */
  private String ville;

  /** */
  private ClassAPrime parent;

  /** */
  private int prime = 31;

  /** {@inheritDoc} */
  public ClassAPrime getParent() {
    return this.parent;
  }

  /** {@inheritDoc} */
  public void setParent(final ClassAPrime parent) {
    this.parent = parent;
  }

  /** {@inheritDoc} */
  public String getRue() {
    return this.rue;
  }

  /** {@inheritDoc} */
  public void setRue(final String rue) {
    this.rue = rue;
  }

  /** {@inheritDoc} */
  public String getVille() {
    return this.ville;
  }

  /** {@inheritDoc} */
  public void setVille(final String ville) {
    this.ville = ville;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int result = 1;
    result = prime * result + ((this.rue == null) ? 0 : this.rue.hashCode());
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final ClassBPrime other = (ClassBPrime) obj;
    if (this.rue == null) {
      if (other.rue != null) {
        return false;
      }
    } else if (!this.rue.equals(other.rue)) {
      return false;
    }
    return true;
  }

  public int compareTo(ClassBPrime o) {
    return 0;
  }
}