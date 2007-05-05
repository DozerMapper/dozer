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

import java.util.Iterator;

import net.sf.dozer.util.mapping.DozerTestBase;
import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;
import net.sf.dozer.util.mapping.fieldmap.Mappings;
import net.sf.dozer.util.mapping.util.MappingFileReader;
import net.sf.dozer.util.mapping.util.MappingUtils;
import net.sf.dozer.util.mapping.util.MappingsParser;

/**
 * @author tierney.matt
 */
public class MappingUtilsTest extends DozerTestBase {

  public void testIsBlankOrNull() throws Exception {
    assertTrue(MappingUtils.isBlankOrNull(null));
    assertTrue(MappingUtils.isBlankOrNull(""));
    assertTrue(MappingUtils.isBlankOrNull(" "));
  }

  public void testOverridenFields() throws Exception {
    MappingFileReader fileReader = new MappingFileReader("overridemapping.xml");
    Mappings mappings = fileReader.read();
    MappingsParser mappingsParser = new MappingsParser();
    mappingsParser.parseMappings(mappings);
    // validate class mappings
    Iterator iter = mappings.getMapping().iterator();
    while (iter.hasNext()) {
      ClassMap classMap = (ClassMap) iter.next();
      if (classMap.getSourceClass().getName().equals("net.sf.dozer.util.mapping.vo.FurtherTestObject")) {
        assertTrue(classMap.getStopOnErrors());
      }
      if (classMap.getSourceClass().getName().equals("net.sf.dozer.util.mapping.vo.SuperSuperSuperClass")) {
        assertTrue(classMap.getWildcard());
      }
      if (classMap.getSourceClass().getName().equals("net.sf.dozer.util.mapping.vo.TestObject")) {
        assertTrue(!((FieldMap) classMap.getFieldMaps().get(0)).getCopyByReference());
      }
    }
  }

  public void testGetClassWithoutPackage() throws Exception {
    String result = MappingUtils.getClassNameWithoutPackage(String.class);
    assertNotNull("result should not be null", result);
    assertEquals("invalid result value", "String", result);
  }
  
  public void testThrowMappingException_MappingException() {
    MappingException ex = new MappingException(String.valueOf(System.currentTimeMillis()));
    try {
      MappingUtils.throwMappingException(ex);
      fail("should have thrown exception");
    } catch (MappingException e) {
      assertEquals("invalid ex", ex, e);
    }
  }
  
  public void testThrowMappingException_RuntimeException() {
    //Runtime ex should not get wrapped in MappingException
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
  
  public void testThrowMappingException_CheckedException() {
    //Checked exception should get wrapped in MappingException
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
  
}
