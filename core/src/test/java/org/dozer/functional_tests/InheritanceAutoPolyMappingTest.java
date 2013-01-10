/*
 * Copyright 2005-2013 the original author or authors.
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
import org.dozer.vo.inheritance.autopoly.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** Test suite for automatic polymorphic resolving and conversion
 * @author Dmitry Spikhalskiy
 */
public class InheritanceAutoPolyMappingTest extends AbstractFunctionalTest {
  private Mapper mapper;

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper(new String[] {"autoPolyInheritanceMapping.xml"});
  }

  @Test
  public void testAutoPolymorphicMappingWithContainer() throws Exception {
    mapper = getMapper(new String[] {"autoPolyInheritanceMapping.xml"});

    AContainer aContainer = new AContainer();
    aContainer.setField(new C());
    XContainer result = mapper.map(aContainer, XContainer.class);

    assertTrue(result.getField() instanceof Z);
  }

  @Test
  public void testAutoPolymorphicDirectMapping() throws Exception {
    mapper = getMapper(new String[] {"autoPolyInheritanceMapping.xml"});

    X result = mapper.map(new C(), X.class);

    assertTrue(result instanceof Z);
  }

  @Test
  public void testAutoPolymorphicMappingForAlreadyExistedClassMapButUndefinedByUser() throws Exception {
    mapper = getMapper(new String[] {"autoPolyInheritanceMapping.xml"});

    mapper.map(new C(), Zz.class);

    X result = mapper.map(new C(), X.class);

    assertTrue(result instanceof Z);
  }

  @Test
  public void testAutoPolymorphicMappingForTwoPolymorphicCandidates() throws Exception {
    mapper = getMapper(new String[] {"autoPolyDeeperInheritanceMapping.xml"});

    X result = mapper.map(new C(), X.class);

    assertTrue(result instanceof Zz);
  }
}
