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


import org.dozer.MappingException;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

public class InvalidMapping_WithExceptionsLoggedTest extends AbstractFunctionalTest {

    @Rule
    public ExpectedException testWrongClassNameEE = ExpectedException.none();

    @Rule
    public ExpectedException testNoFieldBEE = ExpectedException.none();

    @Rule
    public ExpectedException testEmptyFieldBEE = ExpectedException.none();

    @Rule
    public ExpectedException testFieldDoesNotExistEE = ExpectedException.none();

    @Rule
    public ExpectedException testNoClassAEE = ExpectedException.none();

    private final Logger LOG = LoggerFactory.getLogger(InvalidMapping_WithExceptionsLoggedTest.class);

    @Test
    public void testWrongClassName() {
        LOG.error("WithExceptionsLoggedTest; 'MappingException: java.lang.ClassNotFoundException: org.dozer.vo.TestObjectNNNNNNN'");

        testWrongClassNameEE.expectMessage("java.lang.ClassNotFoundException: org.dozer.vo.TestObjectNNNNNNN");
        testWrongClassNameEE.expect(MappingException.class);

        mapper = getMapper("mappings/invalidmapping1.xml");
        mapper.map("1", Integer.class);

        fail();
    }

    @Test
    public void testNoFieldB() {
        LOG.error("WithExceptionsLoggedTest; 'MappingException: cvc-complex-type.2.4.b: The content of element 'field' is not complete. One of '{\"http://dozermapper.github.io/schema/bean-mapping\":b}' is expected'");

        testNoFieldBEE.expectMessage(Matchers.containsString("cvc-complex-type.2.4.b: The content of element 'field' is not complete. One of '{\"http://dozermapper.github.io/schema/bean-mapping\":b}' is expected"));
        testNoFieldBEE.expect(MappingException.class);

        mapper = getMapper("non-strict/invalidmapping2.xml");
        mapper.map("1", Integer.class);

        fail();
    }

    @Test
    public void testEmptyFieldB() {
        LOG.error("WithExceptionsLoggedTest; 'MappingException: Field name can not be empty'");

        testEmptyFieldBEE.expectMessage("Field name can not be empty");
        testEmptyFieldBEE.expect(MappingException.class);

        mapper = getMapper("mappings/invalidmapping3.xml");
        mapper.map("1", Integer.class);

        fail();
    }

    @Test
    public void testFieldDoesNotExist() {
        LOG.error("WithExceptionsLoggedTest; 'MappingException: No read or write method found for field (fielddoesnotexist) in class (class org.dozer.vo.TestObjectPrime)'");

        testFieldDoesNotExistEE.expectMessage("No read or write method found for field (fielddoesnotexist) in class (class org.dozer.vo.TestObjectPrime)");
        testFieldDoesNotExistEE.expect(MappingException.class);

        mapper = getMapper("mappings/invalidmapping4.xml");
        mapper.map("1", Integer.class);

        fail();
    }

    @Test
    public void testNoClassA() {
        LOG.error("WithExceptionsLoggedTest; 'MappingException: cvc-complex-type.2.4.a: Invalid content was found starting with element 'class-b'. One of '{\"http://dozermapper.github.io/schema/bean-mapping\":class-a}' is expected.'");

        testNoClassAEE.expectMessage(Matchers.containsString("cvc-complex-type.2.4.a: Invalid content was found starting with element 'class-b'. One of '{\"http://dozermapper.github.io/schema/bean-mapping\":class-a}' is expected."));
        testNoClassAEE.expect(MappingException.class);

        mapper = getMapper("non-strict/invalidmapping5.xml");
        mapper.map("1", Integer.class);

        fail();
    }
}
