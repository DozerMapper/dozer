/*
 * Copyright 2005-2008 the original author or authors.
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
package net.sf.dozer.functional_tests;

import net.sf.dozer.*;
import net.sf.dozer.vo.cumulative.*;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Dmitry Buzdin
 */
public class CumulativeMappingTest extends AbstractMapperTest {

  protected void setUp() throws Exception {
    mapper = getMapper("cumulative.xml");
  }

  /* Domain model: Library is a list of books. A book has an author and id. An author has a name and id.
  * The same is on 'Prime' side with only one exception. AuthorPrime has all the fields of Author and
  * one more - salary.
  */
  public void testMapping() {
    Library libSrc = new Library();
    libSrc.setBooks(Collections.singletonList(new Book(new Long(141L), new Author("The Best One", new Long(505L)))));

    LibraryPrime libDest = new LibraryPrime();
    BookPrime bookDest = new BookPrime(new Long(141L), new AuthorPrime(new Long(505L), "The Ultimate One", new Long(5100L)));
    List bookDests = new ArrayList();
    bookDests.add(bookDest);
    libDest.setBooks(bookDests);

    mapper.map(libSrc, libDest);

    assertEquals(1, libDest.getBooks().size());
    BookPrime book = (BookPrime) libDest.getBooks().get(0);
    assertEquals(new Long(141L), book.getId());
    assertEquals("The Best One", book.getAuthor().getName());

//    assertEquals(new Long(5100L), book.getAuthor().getSalary()); TODO Enable this for non-cumulative recursion bug
  }

  protected net.sf.dozer.DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
