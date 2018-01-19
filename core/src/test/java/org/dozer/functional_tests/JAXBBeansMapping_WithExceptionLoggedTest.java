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


import org.dozer.vo.jaxb.employee.EmployeeType;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JAXBBeansMapping_WithExceptionLoggedTest extends AbstractFunctionalTest {

    private final Logger LOG = LoggerFactory.getLogger(JAXBBeansMapping_WithExceptionLoggedTest.class);

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mapper = getMapper("mappings/jaxbBeansMapping.xml");
    }

    @Test
    public void testJAXBListWithNoSetter() {
        LOG.error("WithExceptionsLoggedTest; 'NoSuchMethodException: Unable to determine write method for Field: 'ids' in Class: class org.dozer.vo.jaxb.employee.EmployeeType'");

        JAXBBeansMappingTest.ListContainer source = new JAXBBeansMappingTest.ListContainer();
        source.getList().add(1);
        source.getList().add(2);

        source.getSubordinates().add(new JAXBBeansMappingTest.StringContainer("John"));

        EmployeeType result = mapper.map(source, EmployeeType.class);

        assertNotNull(result);
        assertEquals(2, result.getIds().size());
        assertTrue(result.getIds().contains(1));
        assertTrue(result.getIds().contains(2));

        assertEquals(1, result.getSubordinates().size());
        assertEquals("John", result.getSubordinates().get(0).getFirstName());
    }
}
