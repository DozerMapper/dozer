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
import org.dozer.vo.oneway.DestClass;
import org.dozer.vo.oneway.Holder;
import org.dozer.vo.oneway.SourceClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Dmitry Buzdin
 */
public class OneWayMappingTest extends AbstractFunctionalTest {

  @Test
  public void testOneWay() {
    Mapper mapper = getMapper("mappings/oneWayMapping.xml");

    SourceClass source = newInstance(SourceClass.class, new Object[] {"A"});

    Holder holder = mapper.map(source, Holder.class);
    DestClass dest = holder.getDest();

    assertNotNull(dest);
    assertEquals("A", dest.anonymousAccessor());
  }

}
