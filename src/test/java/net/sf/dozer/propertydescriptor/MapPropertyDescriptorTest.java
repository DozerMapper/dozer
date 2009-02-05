/*
 * Copyright 2005-2009 the original author or authors.
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
package net.sf.dozer.propertydescriptor;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.Before;

import java.lang.reflect.Method;
import java.util.Arrays;

import net.sf.dozer.MappingException;

/**
 * @author dmitry.buzdin
 */
public class MapPropertyDescriptorTest extends TestCase {

  private MapPropertyDescriptor descriptor;

  @Before
  protected void setUp() throws Exception {
    descriptor = new MapPropertyDescriptor(MapStructure.class, "", false, 0, "set", "get", "key", null, null);
  }


  @Test
  public void testGetWriteMethod() throws NoSuchMethodException {
    Method method = descriptor.getWriteMethod();
    assertEquals(2, method.getParameterTypes().length);
    assertTrue(Arrays.equals(new Class[]{String.class, Object.class}, method.getParameterTypes()));
  }

  @Test
  public void testGetWriteMethod_NotFound() throws NoSuchMethodException {
    descriptor = new MapPropertyDescriptor(MapStructure.class, "", false, 0, "missing_set", "get", "key", null, null);
    try {
      descriptor.getWriteMethod();
      fail();
    } catch (MappingException e) {

    }
  }

  @Test
  public void testGetReadMethod() throws NoSuchMethodException {
    Method method = descriptor.getReadMethod();
    assertEquals(1, method.getParameterTypes().length);
    assertTrue(Arrays.equals(new Class[]{String.class}, method.getParameterTypes()));
  }

  private static class MapStructure {

    public Object get() {
      return null;
    }

    public Object get(String id) {
      return null;
    }

    public Object get(String id1, String id2) {
      return null;
    }

    public void set(String id) {
    }

    public void set(String id, Object object) {
    }

    public void set(String id, Object object, String id2) {
    }

  }


}
