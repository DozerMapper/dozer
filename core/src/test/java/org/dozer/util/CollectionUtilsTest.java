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
package org.dozer.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.dozer.AbstractDozerTest;
import org.dozer.vo.InsideTestObject;
import org.dozer.vo.SimpleObj;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class CollectionUtilsTest extends AbstractDozerTest {

  @Test
  public void testIsList() throws Exception {
    Object[] values = new Object[] { new ArrayList(), new Vector(), new LinkedList() };
    for (int i = 0; i < values.length; i++) {
      assertTrue(CollectionUtils.isList(values[i].getClass()));
    }
  }

  @Test
  public void testIsSet() throws Exception {
    Object[] values = new Object[] { new TreeSet(), new HashSet(), new HashSet() };
    for (int i = 0; i < values.length; i++) {
      assertTrue(CollectionUtils.isSet(values[i].getClass()));
    }
  }

  @Test
  public void testIsArray() throws Exception {
    Object[] values = new Object[] { new int[3], new InsideTestObject[2], new String[3] };
    for (int i = 0; i < values.length; i++) {
      assertTrue(CollectionUtils.isArray(values[i].getClass()));
    }
  }

  @Test
  public void testIsPrimitiveArray() throws Exception {
    Object[] values = new Object[] { new int[3], new long[2], new boolean[3] };
    for (int i = 0; i < values.length; i++) {
      assertTrue(CollectionUtils.isPrimitiveArray(values[i].getClass()));
    }
  }

  @Test
  public void testIsPrimitiveArray_False() throws Exception {
    Object[] values = new Object[] { new String[3], new Date[2], new SimpleObj[3] };
    for (int i = 0; i < values.length; i++) {
      assertFalse(CollectionUtils.isPrimitiveArray(values[i].getClass()));
    }
  }

  @Test
  public void testGetValueFromCollection() throws Exception {
    String sysTime = String.valueOf(System.currentTimeMillis());
    String[] input = new String[] { "zer", "one", "two", "three", "four", sysTime, "six", "seven" };
    Object result = CollectionUtils.getValueFromCollection(input, 5);

    assertEquals("invalid result", sysTime, result);
  }

  @Test
  public void testLengthOfCollection() throws Exception {
    String[] input = new String[] { "zer", "one", "two", "three", "four" };
    assertEquals("invalid array size", input.length, CollectionUtils.getLengthOfCollection(input));
  }

  @Test
  public void testCreateNewSet_Default() throws Exception {
    Set<?> result = CollectionUtils.createNewSet(Set.class);
    assertNotNull("new set should not be null", result);
  }

  @Test
  public void testCreateNewSet_SortedSetDefault() throws Exception {
    Set<?> result = CollectionUtils.createNewSet(SortedSet.class);
    assertNotNull("new set should not be null", result);
    assertTrue("new set should be instance of SortedSet", result instanceof SortedSet);
  }

  @Test
  public void testCreateNewSet_FromExistingSet() throws Exception {
    Set<String> src = new HashSet<String>();
    src.add("test1");
    src.add("test2");
    Set<?> result = CollectionUtils.createNewSet(Set.class, src);
    assertNotNull("new set should not be null", result);
    assertEquals("new set should equal src set", src, result);
  }

  @Test
  public void testConvertPrimitiveArrayToList() throws Exception {
    int[] srcArray = new int[] { 5, 10, 15 };
    List<?> result = CollectionUtils.convertPrimitiveArrayToList(srcArray);
    assertEquals("invalid result size", srcArray.length, result.size());

    for (int i = 0; i < srcArray.length; i++) {
      Integer srcValue = new Integer(srcArray[i]);
      Integer resultValue = (Integer) result.get(i);
      assertEquals("invalid result entry value", srcValue, resultValue);
    }
  }

  @Test
  public void testConvertListToArray() {
    List<String> src = Arrays.asList("a", "b");
    String[] result = (String[]) CollectionUtils.convertListToArray(src, String.class);
    assertTrue("wrong result value", Arrays.equals(new String[] { "a", "b" }, result));
  }

  @Test
  public void testCreateNewSet_ExistingValue() {
    Collection<String> src = new HashSet<String>();
    src.add("a");
    src.add("b");
    Set<?> result = CollectionUtils.createNewSet(TreeSet.class, src);
    assertEquals("wrong result value", src, result);
  }

  @Test
  public void testCreateNewSet() {
    Set<?> result = CollectionUtils.createNewSet(HashSet.class);
    assertNotNull("should be not null", result);
    assertEquals("shoulb be size zero", 0, result.size());
  }

}
