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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.dozer.AbstractDozerTest;
import org.dozer.MappingException;
import org.dozer.classmap.ClassMap;
import org.dozer.classmap.MappingFileData;
import org.dozer.config.BeanContainer;
import org.dozer.factory.DestBeanCreator;
import org.dozer.functional_tests.runner.ProxyDataObjectInstantiator;
import org.dozer.loader.MappingsParser;
import org.dozer.loader.xml.MappingFileReader;
import org.dozer.loader.xml.XMLParser;
import org.dozer.loader.xml.XMLParserFactory;
import org.dozer.propertydescriptor.PropertyDescriptorFactory;
import org.dozer.vo.enumtest.DestType;
import org.dozer.vo.enumtest.DestTypeWithOverride;
import org.dozer.vo.enumtest.SrcType;
import org.dozer.vo.enumtest.SrcTypeWithOverride;
import org.dozer.vo.interfacerecursion.AnotherLevelTwo;
import org.dozer.vo.interfacerecursion.AnotherLevelTwoImpl;
import org.dozer.vo.interfacerecursion.Base;
import org.dozer.vo.interfacerecursion.BaseImpl;
import org.dozer.vo.interfacerecursion.LevelOne;
import org.dozer.vo.interfacerecursion.LevelOneImpl;
import org.dozer.vo.interfacerecursion.LevelTwo;
import org.dozer.vo.interfacerecursion.LevelTwoImpl;
import org.dozer.vo.interfacerecursion.User;
import org.dozer.vo.interfacerecursion.UserSub;
import org.junit.Test;

/**
 * @author tierney.matt
 * @author dmitry.buzdin
 */
public class MappingUtilsTest extends AbstractDozerTest {

  private BeanContainer beanContainer = new BeanContainer();
  private DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);
  private PropertyDescriptorFactory propertyDescriptorFactory = new PropertyDescriptorFactory();

  @Test
  public void testIsBlankOrNull() throws Exception {
    assertTrue(MappingUtils.isBlankOrNull(null));
    assertTrue(MappingUtils.isBlankOrNull(""));
    assertTrue(MappingUtils.isBlankOrNull(" "));
  }

  @Test
  public void testOverridenFields() throws Exception {
    MappingFileReader fileReader = new MappingFileReader(new XMLParserFactory(beanContainer), new XMLParser(beanContainer, destBeanCreator, propertyDescriptorFactory), beanContainer);
    MappingFileData mappingFileData = fileReader.read("mappings/overridemapping.xml");
    MappingsParser mappingsParser = new MappingsParser(beanContainer, destBeanCreator, propertyDescriptorFactory);
    mappingsParser.processMappings(mappingFileData.getClassMaps(), mappingFileData.getConfiguration());
    // validate class mappings
    for (ClassMap classMap : mappingFileData.getClassMaps()) {
      if (classMap.getSrcClassToMap().getName().equals("org.dozer.util.mapping.vo.FurtherTestObject")) {
        assertTrue(classMap.isStopOnErrors());
      }
      if (classMap.getSrcClassToMap().getName().equals("org.dozer.util.mapping.vo.SuperSuperSuperClass")) {
        assertTrue(classMap.isWildcard());
      }
      if (classMap.getSrcClassToMap().getName().equals("org.dozer.util.mapping.vo.TestObject")) {
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

  @Test(expected=MappingException.class)
  public void testThrowMappingException_MappingException() {
    MappingException ex = new MappingException(String.valueOf(System.currentTimeMillis()));
      MappingUtils.throwMappingException(ex);
      fail("should have thrown exception");
  }

  @Test(expected=NullPointerException.class)
  public void testThrowMappingException_RuntimeException() {
    // Runtime ex should not get wrapped in MappingException
    NullPointerException ex = new NullPointerException(String.valueOf(System.currentTimeMillis()));
      MappingUtils.throwMappingException(ex);
      fail("should have thrown exception");
  }

  @Test(expected=MappingException.class)
  public void testThrowMappingException_CheckedException() {
    // Checked exception should get wrapped in MappingException
    NoSuchFieldException ex = new NoSuchFieldException(String.valueOf(System.currentTimeMillis()));
      MappingUtils.throwMappingException(ex);
      fail("should have thrown exception");
  }

  @Test
  public void testGetRealClass() {
    Object proxyObj = ProxyDataObjectInstantiator.INSTANCE.newInstance(ArrayList.class);
    assertEquals(ArrayList.class, MappingUtils.getRealClass(proxyObj.getClass(), beanContainer));
    assertEquals(ArrayList.class, MappingUtils.getRealClass(ArrayList.class, beanContainer));
  }

  @Test
  public void testGetRealClass_CGLIBTarget() {
    Object proxyObj = ProxyDataObjectInstantiator.INSTANCE.newInstance(new Class[] {List.class}, new ArrayList());
    assertSame(proxyObj.getClass(), MappingUtils.getRealClass(proxyObj.getClass(), beanContainer));
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

  @Test
  public void testLoadClass() {
    assertNotNull(MappingUtils.loadClass("java.lang.String", beanContainer));
    assertNotNull(MappingUtils.loadClass("java.lang.String[]", beanContainer));
    assertNotNull(MappingUtils.loadClass("[Ljava.lang.String;", beanContainer));
  }

  @Test
  public void testGetDeepInterfaces() {

    testGetDeepInterfaces(Base.class);
    testGetDeepInterfaces(LevelOne.class, Base.class, User.class);
    testGetDeepInterfaces(LevelTwo.class, LevelOne.class, User.class, Base.class);
    testGetDeepInterfaces(AnotherLevelTwo.class, LevelOne.class, UserSub.class, Base.class, User.class);

    testGetDeepInterfaces(BaseImpl.class, Base.class);
    testGetDeepInterfaces(LevelOneImpl.class, LevelOne.class, Serializable.class, Base.class, User.class);
    testGetDeepInterfaces(LevelTwoImpl.class, LevelTwo.class, LevelOne.class, User.class, Base.class);
    testGetDeepInterfaces(AnotherLevelTwoImpl.class, AnotherLevelTwo.class, LevelOne.class, UserSub.class, Base.class, User.class);
  }

  public void testGetDeepInterfaces(Class<?> classToTest, Class<?>... expectedInterfaces) {
    List<Class<?>> result = MappingUtils.getInterfaceHierarchy(classToTest, beanContainer);
    List<Class<?>> expected = Arrays.asList(expectedInterfaces);

    assertEquals(expected, result);
  }


  @Test
  public void testGetSuperClasses() {

    testGetSuperClasses(BaseImpl.class, Base.class);
    testGetSuperClasses(LevelOneImpl.class, BaseImpl.class, LevelOne.class, Serializable.class, Base.class, User.class);
    testGetSuperClasses(LevelTwoImpl.class, LevelTwo.class, LevelOne.class, User.class, Base.class);
    testGetSuperClasses(AnotherLevelTwoImpl.class, LevelOneImpl.class, BaseImpl.class, AnotherLevelTwo.class, LevelOne.class, UserSub.class, Base.class, User.class, Serializable.class);
  }

  public void testGetSuperClasses(Class<?> classToTest, Class<?>... expectedClasses) {
    List<Class<?>> result = MappingUtils.getSuperClassesAndInterfaces(classToTest, beanContainer);
    List<Class<?>> expected = Arrays.asList(expectedClasses);

    assertEquals(expected, result);
  }
}
