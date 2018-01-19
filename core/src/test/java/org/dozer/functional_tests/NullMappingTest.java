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

import java.util.HashMap;
import java.util.Map;

import org.dozer.vo.A;
import org.dozer.vo.B;
import org.dozer.vo.set.NamesArray;
import org.dozer.vo.set.NamesList;
import org.dozer.vo.set.NamesSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author dmitry.buzdin
 */
public class NullMappingTest extends AbstractFunctionalTest {

  private static final String NULL = "null";
  private static final String NOT_NULL = "not-null";

  @Before
  public void setUp() throws Exception {
    super.setUp();
    mapper = getMapper("mappings/nullMapping.xml");
  }

  @Test
  public void testSimple() {
    HashMap source = new HashMap();
    source.put("key", new B());
    A dest = mapper.map(source, A.class, NULL);
    assertNotNull(dest);
    assertNotNull(dest.getB());
  }

  @Test
  public void testSimpleReverse() {
    A source = new A();
    source.setB(new B());
    Map dest = mapper.map(source, HashMap.class, NULL);
    assertNotNull(dest);
    assertTrue(dest.containsKey("key"));
    assertNotNull(dest.get("key"));
  }

  @Test
  public void testNull() {
    HashMap source = new HashMap();
    source.put("key", null);
    A dest = mapper.map(source, A.class, NULL);
    assertNotNull(dest);
    assertNull(dest.getB());
  }

  @Test
  public void testNullReverse() {
    A source = new A();
    source.setB(null);
    Map dest = mapper.map(source, HashMap.class, NULL);
    assertNotNull(dest);
    assertTrue(dest.containsKey("key"));
    assertNull(dest.get("key"));
  }

  @Test
  public void testNullReverse_NoNullMApping() {
    A source = new A();
    source.setB(null);
    Map dest = mapper.map(source, HashMap.class, NOT_NULL);
    assertNotNull(dest);
    assertFalse(dest.containsKey("key"));
  }

  @Test
  public void testNullSet() {
    NamesArray namesArray = new NamesArray();
    String[] arr = new String[] {null, "two"};
    namesArray.setNames(arr);
    
    NamesSet namesSet = mapper.map(namesArray, NamesSet.class, "null-set");

    assertEquals(2, namesSet.getNames().size());
    assertTrue(namesSet.getNames().contains(arr[0]));
    assertTrue(namesSet.getNames().contains(arr[1]));
  }

  @Test
  public void testNullList() {
    NamesArray namesArray = new NamesArray();
    String[] arr = new String[] {null, "two"};
    namesArray.setNames(arr);
    NamesList namesSet = mapper.map(namesArray, NamesList.class, "null-list");
    Assert.assertArrayEquals(arr, namesSet.getNames().toArray());
  }

}
