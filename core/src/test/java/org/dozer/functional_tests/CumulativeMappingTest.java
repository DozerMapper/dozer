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
package org.dozer.functional_tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dozer.vo.cumulative.Author;
import org.dozer.vo.cumulative.AuthorPrime;
import org.dozer.vo.cumulative.Book;
import org.dozer.vo.cumulative.BookPrime;
import org.dozer.vo.cumulative.Library;
import org.dozer.vo.cumulative.LibraryPrime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Dmitry Buzdin
 */
public class CumulativeMappingTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("mappings/cumulative.xml");
  }

  /* Domain model: Library is a list of books. A book has an author and id. An author has a name and id.
  * The same is on 'Prime' side with only one exception. AuthorPrime has all the fields of Author and
  * one more - salary.
  */
  @Test
  public void testMapping() {
    Library libSrc = newInstance(Library.class);
    Author author = newInstance(Author.class, new Object[] {"The Best One", new Long(505L)});
    Book book = newInstance(Book.class, new Object[] {new Long(141L), author});
    libSrc.setBooks(Collections.singletonList(book));

    LibraryPrime libDest = newInstance(LibraryPrime.class);
    AuthorPrime authorPrime = newInstance(AuthorPrime.class, new Object[] {new Long(505L), "The Ultimate One", new Long(5100L)});
    BookPrime bookDest = newInstance(BookPrime.class, new Object[] {new Long(141L), authorPrime});
    List<BookPrime> bookDests = newInstance(ArrayList.class);
    bookDests.add(bookDest);
    libDest.setBooks(bookDests);

    mapper.map(libSrc, libDest);

    assertEquals(1, libDest.getBooks().size());
    BookPrime bookPrime = (BookPrime) libDest.getBooks().get(0);
    assertEquals(new Long(141L), bookPrime.getId());
    assertEquals("The Best One", bookPrime.getAuthor().getName());

    //    assertEquals(new Long(5100L), book.getAuthor().getSalary()); TODO Enable this for non-cumulative recursion bug
  }

}
