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
package org.dozer.vo.cumulative;

/**
 * @author Dmitry Buzdin
 */
public class BookPrime {

  private Long id;
  private AuthorPrime author;

  public BookPrime() {
  }

  public BookPrime(Long id, AuthorPrime author) {
    super();
    this.id = id;
    this.author = author;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public AuthorPrime getAuthor() {
    return author;
  }

  public void setAuthor(AuthorPrime author) {
    this.author = author;
  }

  @Override
  public int hashCode() {
    return id == null ? 0 : id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof BookPrime)) {
      return false;
    }

    BookPrime other = (BookPrime) obj;
    return id == null ? (this == other) : id.equals(other.id);
  }

}
