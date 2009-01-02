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
package net.sf.dozer.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sf.dozer.AbstractDozerTest;
import net.sf.dozer.MappingException;
import net.sf.dozer.classmap.ClassMap;
import net.sf.dozer.classmap.MappingFileData;
import net.sf.dozer.functional_tests.proxied.ProxyDataObjectInstantiator;
import net.sf.dozer.vo.enumtest.DestType;
import net.sf.dozer.vo.enumtest.DestTypeWithOverride;
import net.sf.dozer.vo.enumtest.SrcType;
import net.sf.dozer.vo.enumtest.SrcTypeWithOverride;

import org.junit.Test;

/**
 * @author tierney.matt
 * @author dmitry.buzdin
 */
public class MappingUtilsTest extends AbstractDozerTest {

  @Test
  public void testIsBlankOrNull() throws Exception {
    assertTrue(MappingUtils.isBlankOrNull(null));
    assertTrue(MappingUtils.isBlankOrNull(""));
    assertTrue(MappingUtils.isBlankOrNull(" "));
  }

  @Test
  public void testOverridenFields() throws Exception {
    MappingFileReader fileReader = new MappingFileReader("overridemapping.xml");
    MappingFileData mappingFileData = fileReader.read();
    MappingsParser mappingsParser = MappingsParser.getInstance();
    mappingsParser.processMappings(mappingFileData.getClassMaps(), mappingFileData.getConfiguration());
    // validate class mappings
    Iterator<?> iter = mappingFileData.getClassMaps().iterator();
    while (iter.hasNext()) {
      ClassMap classMap = (ClassMap) iter.next();
      if (classMap.getSrcClassToMap().getName().equals("net.sf.dozer.util.mapping.vo.FurtherTestObject")) {
        assertTrue(classMap.isStopOnErrors());
      }
      if (classMap.getSrcClassToMap().getName().equals("net.sf.dozer.util.mapping.vo.SuperSuperSuperClass")) {
        assertTrue(classMap.isWildcard());
      }
      if (classMap.getSrcClassToMap().getName().equals("net.sf.dozer.util.mapping.vo.TestObject")) {
        assertTrue(!(classMap.getFieldMaps().get(0)).isCopyByReference());
      }
    }
  }

  @Test
  public void testGetClassWithoutPackage() throws Exception {
    String result = MappingUtils.getClassNameWithoutPackage(String.class);
    assertNotNull("result should not be null", result);
    assertEquals("invalid result value", "String", result);
  }

  @Test
  public void testThrowMappingException_MappingException() {
    MappingException ex = new MappingException(String.valueOf(System.currentTimeMillis()));
    try {
      MappingUtils.throwMappingException(ex);
      fail("should have thrown exception");
    } catch (MappingException e) {
      assertEquals("invalid ex", ex, e);
    }
  }

  @Test
  public void testThrowMappingException_RuntimeException() {
    // Runtime ex should not get wrapped in MappingException
    NullPointerException ex = new NullPointerException(String.valueOf(System.currentTimeMillis()));
    try {
      MappingUtils.throwMappingException(ex);
      fail("should have thrown exception");
    } catch (NullPointerException e) {
      assertEquals("invalid ex", ex, e);
    } catch (Throwable e) {
      fail("NullPointerException should have been thrown");
    }
  }

  @Test
  public void testThrowMappingException_CheckedException() {
    // Checked exception should get wrapped in MappingException
    NoSuchFieldException ex = new NoSuchFieldException(String.valueOf(System.currentTimeMillis()));
    try {
      MappingUtils.throwMappingException(ex);
      fail("should have thrown exception");
    } catch (MappingException e) {
      assertEquals("invalid nested ex", ex, e.getCause());
    } catch (Throwable e) {
      fail("MappingException should have been thrown");
    }
  }

  @Test
  public void testGetRealClass() {
    Object proxyObj = ProxyDataObjectInstantiator.INSTANCE.newInstance(ArrayList.class);
    assertEquals(ArrayList.class, MappingUtils.getRealClass(proxyObj.getClass()));
    assertEquals(ArrayList.class, MappingUtils.getRealClass(ArrayList.class));
  }

  @Test
  public void testIsProxy() {
    Object proxyObj = ProxyDataObjectInstantiator.INSTANCE.newInstance(ArrayList.class);
    assertTrue("should have evaluated to true for cglib proxy", MappingUtils.isProxy(proxyObj.getClass()));
    assertFalse("should not have evaluated to true", MappingUtils.isProxy(ArrayList.class));
  }

  @Test
  public void testGetRealSuperclass() {
    Object proxyObj = ProxyDataObjectInstantiator.INSTANCE.newInstance(ArrayList.class);
    assertEquals("wrong value returned for cglib proxy", AbstractList.class, MappingUtils.getRealSuperclass(proxyObj.getClass()));
    assertEquals("wrong value returned for unproxied object", AbstractList.class, MappingUtils.getRealSuperclass(ArrayList.class));
  }

  @Test
  public void testIsSupportedMap() {
    assertTrue(MappingUtils.isSupportedMap(Map.class));
    assertTrue(MappingUtils.isSupportedMap(HashMap.class));
    assertFalse(MappingUtils.isSupportedMap(String.class));
  }

  @Test
  public void testIsDeepMapping() {
    assertTrue(MappingUtils.isDeepMapping("a.b"));
    assertTrue(MappingUtils.isDeepMapping("."));
    assertTrue(MappingUtils.isDeepMapping("aa.bb.cc"));

    assertFalse(MappingUtils.isDeepMapping(null));
    assertFalse(MappingUtils.isDeepMapping(""));
    assertFalse(MappingUtils.isDeepMapping("aaa"));
  }

  @Test
  public void testPrepareIndexedCollection_Array() {
    String[] result = (String[]) MappingUtils.prepareIndexedCollection(String[].class, null, "some entry", 0);
    assertTrue(Arrays.equals(new String[] { "some entry" }, result));

    result = (String[]) MappingUtils.prepareIndexedCollection(String[].class, null, "some entry", 3);
    assertTrue(Arrays.equals(new String[] { null, null, null, "some entry" }, result));

    result = (String[]) MappingUtils.prepareIndexedCollection(String[].class, new String[] { "a", "b", "c" }, "some entry", 5);
    assertTrue(Arrays.equals(new String[] { "a", "b", "c", null, null, "some entry" }, result));
  }

  @Test
  public void testPrepareIndexedCollection_List() {
    List<?> result = (List<?>) MappingUtils.prepareIndexedCollection(List.class, null, "some entry", 0);
    assertEquals(Arrays.asList(new String[] { "some entry" }), result);

    result = (List<?>) MappingUtils.prepareIndexedCollection(List.class, null, "some entry", 3);
    assertEquals(Arrays.asList(new String[] { null, null, null, "some entry" }), result);

    result = (List<?>) MappingUtils.prepareIndexedCollection(List.class, Arrays.asList(new String[] { "a", "b", "c" }),
        "some entry", 5);
    assertEquals(Arrays.asList(new String[] { "a", "b", "c", null, null, "some entry" }), result);
  }

  @Test
  public void testPrepareIndexedCollection_Vector() {
    Vector<?> result = (Vector<?>) MappingUtils.prepareIndexedCollection(Vector.class, null, "some entry", 0);
    assertEquals(new Vector<String>(Arrays.asList(new String[] { "some entry" })), result);

    result = (Vector<?>) MappingUtils.prepareIndexedCollection(Vector.class, null, "some entry", 3);
    assertEquals(new Vector<String>(Arrays.asList(new String[] { null, null, null, "some entry" })), result);

    result = (Vector<?>) MappingUtils.prepareIndexedCollection(Vector.class, new Vector<String>(Arrays.asList(new String[] { "a",
        "b", "c" })), "some entry", 5);
    assertEquals(new Vector<String>(Arrays.asList(new String[] { "a", "b", "c", null, null, "some entry" })), result);
  }

  @Test
  public void testPrepareIndexedCollection_ArrayResize() {
    String[] result = (String[]) MappingUtils.prepareIndexedCollection(String[].class, new String[] { "a", "b" }, "some entry", 3);
    assertTrue(Arrays.equals(new String[] { "a", "b", null, "some entry" }, result));
  }

  @Test(expected = MappingException.class)
  public void testPrepareIndexedCollection_UnsupportedType() {
    MappingUtils.prepareIndexedCollection(String.class, null, "some entry", 0);
  }

  /**
   * Test for isEnumType(Class srcFieldClass, Class destFieldType) defined in MappingUtils
   */
  @Test
  public void testIsEnum() {
    assertTrue(MappingUtils.isEnumType(SrcType.class, DestType.class));
    assertTrue(MappingUtils.isEnumType(SrcType.FOO.getClass(), DestType.FOO.getClass()));
    assertTrue(MappingUtils.isEnumType(SrcTypeWithOverride.FOO.getClass(), DestType.FOO.getClass()));
    assertTrue(MappingUtils.isEnumType(SrcTypeWithOverride.FOO.getClass(), DestTypeWithOverride.FOO.getClass()));
    assertFalse(MappingUtils.isEnumType(SrcType.class, String.class));
    assertFalse(MappingUtils.isEnumType(String.class, SrcType.class));
  }

}
