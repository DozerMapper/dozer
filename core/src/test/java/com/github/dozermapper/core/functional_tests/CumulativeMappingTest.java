/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.functional_tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.dozermapper.core.vo.cumulative.Author;
import com.github.dozermapper.core.vo.cumulative.AuthorPrime;
import com.github.dozermapper.core.vo.cumulative.Book;
import com.github.dozermapper.core.vo.cumulative.BookPrime;
import com.github.dozermapper.core.vo.cumulative.Library;
import com.github.dozermapper.core.vo.cumulative.LibraryPrime;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
        Author author = newInstance(Author.class, new Object[] {505L, "The Best One"});
        Book book = newInstance(Book.class, new Object[] {141L, author});
        libSrc.setBooks(Collections.singletonList(book));

        LibraryPrime libDest = newInstance(LibraryPrime.class);
        AuthorPrime authorPrime = newInstance(AuthorPrime.class, new Object[] {505L, "The Ultimate One", 5100L});
        BookPrime bookDest = newInstance(BookPrime.class, new Object[] {141L, authorPrime});
        List<BookPrime> bookDests = newInstance(ArrayList.class);
        bookDests.add(bookDest);
        libDest.setBooks(bookDests);

        mapper.map(libSrc, libDest);

        // verify book collection
        assertEquals(bookDests, libDest.getBooks()); // reuse existing collection in destination object
        assertEquals(1, libDest.getBooks().size());

        // verify book
        BookPrime bookPrime = libDest.getBooks().get(0);
        assertEquals(bookDest, bookPrime); // reuse existing item in collection on destination object
        assertEquals(Long.valueOf(141L), bookPrime.getId());

        // verify author
        assertEquals(authorPrime, bookPrime.getAuthor());
        assertEquals("The Best One", bookPrime.getAuthor().getName());
        assertEquals(Long.valueOf(5100L), bookPrime.getAuthor().getSalary());
    }

}
