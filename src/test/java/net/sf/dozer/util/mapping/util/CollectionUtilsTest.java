/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util.mapping.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import junit.framework.Assert;
import net.sf.dozer.util.mapping.DozerTestBase;
import net.sf.dozer.util.mapping.vo.InsideTestObject;
import net.sf.dozer.util.mapping.vo.SimpleObj;

/**
 * @author tierney.matt
 */
public class CollectionUtilsTest extends DozerTestBase {
  private CollectionUtils utils = new CollectionUtils();
  
  public void testIsList() throws Exception {
    Object[] values = new Object[] { new ArrayList(), new Vector(), new LinkedList() };

    for (int i = 0; i < values.length; i++) {
      assertTrue(utils.isList(values[i].getClass()));
    }
  }

  public void testIsSet() throws Exception {
    Object[] values = new Object[] { new TreeSet(), new HashSet(), new HashSet() };

    for (int i = 0; i < values.length; i++) {
      assertTrue(utils.isSet(values[i].getClass()));
    }
  }

  public void testIsArray() throws Exception {
    Object[] values = new Object[] { new int[3], new InsideTestObject[2], new String[3] };

    for (int i = 0; i < values.length; i++) {
      assertTrue(utils.isArray(values[i].getClass()));
    }
  }
  
  public void testIsPrimitiveArray() throws Exception {
    Object[] values = new Object[] { new int[3], new long[2], new boolean[3] };

    for (int i = 0; i < values.length; i++) {
      assertTrue(utils.isPrimitiveArray(values[i].getClass()));
    }
  }
  
  public void testIsPrimitiveArray_False() throws Exception {
    Object[] values = new Object[] { new String[3], new Date[2], new SimpleObj[3] };

    for (int i = 0; i < values.length; i++) {
      assertFalse(utils.isPrimitiveArray(values[i].getClass()));
    }
  }

  public void testGetValueFromCollection() throws Exception {
    String sysTime = String.valueOf(System.currentTimeMillis());
    String[] input = new String[] { "zer", "one", "two", "three", "four", sysTime, "six", "seven" };
    Object result = utils.getValueFromCollection(input, 5);

    assertEquals("invalid result", sysTime, result);
  }

  public void testLengthOfCollection() throws Exception {
    String[] input = new String[] { "zer", "one", "two", "three", "four" };
    assertEquals("invalid array size", input.length, utils.getLengthOfCollection(input));
  }
  
  public void testCreateNewSet_Default() throws Exception {
    Set result = utils.createNewSet(Set.class);
    assertNotNull("new set should not be null", result);
  }
  
  public void testCreateNewSet_SortedSetDefault() throws Exception {
    Set result = utils.createNewSet(SortedSet.class);
    assertNotNull("new set should not be null", result);
    assertTrue("new set should be instance of SortedSet", result instanceof SortedSet);
  }
  
  public void testCreateNewSet_FromExistingSet() throws Exception {
    Set src = new HashSet();
    src.add("test1");
    src.add("test2");
    Set result = utils.createNewSet(Set.class, src);
    assertNotNull("new set should not be null", result);
    assertEquals("new set should equal src set", src, result);
  }
  
  public void testConvertPrimitiveArrayToList() throws Exception {
    int[] srcArray = new int[] {5, 10, 15};
    
    List result = utils.convertPrimitiveArrayToList(srcArray);
    assertEquals("invalid result size", srcArray.length, result.size());
    
    for(int i = 0; i < srcArray.length; i++) {
      Integer srcValue = new Integer((int)srcArray[i]);
      Integer resultValue = (Integer) result.get(i);
      assertEquals("invalid result entry value", srcValue, resultValue);
    }
  }
  
}
