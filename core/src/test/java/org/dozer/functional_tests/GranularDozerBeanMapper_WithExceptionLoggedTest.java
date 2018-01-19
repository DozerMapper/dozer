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

import org.dozer.Mapper;
import org.dozer.MappingException;
import org.dozer.vo.NoDefaultConstructor;
import org.dozer.vo.TestObject;
import org.dozer.vo.TestObjectPrime;
import org.dozer.vo.allowedexceptions.TestException;
import org.dozer.vo.allowedexceptions.ThrowException;
import org.dozer.vo.allowedexceptions.ThrowExceptionPrime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

public class GranularDozerBeanMapper_WithExceptionLoggedTest extends AbstractFunctionalTest {

    @Rule
    public ExpectedException testAllowedExceptions_ImplicitEE = ExpectedException.none();

    @Rule
    public ExpectedException testAllowedExceptionsThrowExceptionEE = ExpectedException.none();

    private final Logger LOG = LoggerFactory.getLogger(GranularDozerBeanMapper_WithExceptionLoggedTest.class);

    @Test(expected = MappingException.class)
    public void testNoDefaultConstructor() throws Exception {
        mapper.map("test", NoDefaultConstructor.class);

        fail("should have thrown exception");
    }

    @Test
    public void testAllowedExceptions_Implicit() throws Exception {
        LOG.error("WithExceptionsLoggedTest; 'TestException: Checking Allowed Exceptions'");

        testAllowedExceptions_ImplicitEE.expectMessage("Checking Allowed Exceptions");
        testAllowedExceptions_ImplicitEE.expect(TestException.class);

        Mapper mapper = getMapper("mappings/implicitAllowedExceptionsMapping.xml");

        ThrowException to = newInstance(ThrowException.class);
        to.setThrowAllowedException("throw me");

        mapper.map(to, ThrowExceptionPrime.class);

        fail("We should have thrown TestException");
    }

    @Test
    public void testAllowedExceptionsThrowException() throws Exception {
        LOG.error("WithExceptionsLoggedTest; 'TestException: Checking Allowed Exceptions'");

        testAllowedExceptions_ImplicitEE.expectMessage("Checking Allowed Exceptions");
        testAllowedExceptions_ImplicitEE.expect(TestException.class);

        Mapper mapper = getMapper("mappings/allowedExceptionsMapping.xml");

        TestObject to = newInstance(TestObject.class);
        to.setThrowAllowedExceptionOnMap("throw me");

        mapper.map(to, TestObjectPrime.class);

        fail("We should have thrown TestException");
    }
}
