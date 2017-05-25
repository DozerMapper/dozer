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

import java.util.TreeSet;

/**  */
public class ClassAPrime {
  /**  */
  private String nom;
  /**  */
  private String prenom;
  /** */
  private TreeSet<ClassBPrime> subs;
  /**  */
  private final int prime = 31;
  /** {@inheritDoc} */
  public final void addSubs(final ClassBPrime value) {
    if (value == null) {
      return;
    }
    if (this.getSubs() == null) {
      this.setSubs(new TreeSet<ClassBPrime>());
    }
    this.getSubs().add(value);
    if (value.getParent() != this) {
      value.setParent(this);
    }
  }
  /** {@inheritDoc} */
  public String getNom() {
    return this.nom;
  }
  /** {@inheritDoc} */
  public void setNom(final String nom) {
    this.nom = nom;
  }
  /** {@inheritDoc} */
  public String getPrenom() {
    return this.prenom;
  }
  /** {@inheritDoc} */
  public void setPrenom(final String prenom) {
    this.prenom = prenom;
  }
  /** {@inheritDoc} */
  public TreeSet<ClassBPrime> getSubs() {
    return this.subs;
  }
  /** {@inheritDoc} */
  public void setSubs(final TreeSet<ClassBPrime> subs) {
    this.subs = subs;
  }
  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int result = 1;
    result = prime * result + ((this.nom == null) ? 0 : this.nom.hashCode());
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
    final ClassAPrime other = (ClassAPrime) obj;
    if (this.nom == null) {
      if (other.nom != null) {
        return false;
      }
    } else if (!this.nom.equals(other.nom)) {
      return false;
    }
    return true;
  }
}
