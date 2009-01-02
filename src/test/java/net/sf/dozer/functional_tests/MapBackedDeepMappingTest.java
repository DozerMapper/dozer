/*
 * Copyright 2005-2008 the original author or authors.
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
package net.sf.dozer.functional_tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import net.sf.dozer.vo.DeepObject;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Dmitry Buzdin
 */
public class MapBackedDeepMappingTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("mapBackedDeepMapping.xml");
  }

  @Test
  public void testMapBackedDeepMapping_OneLevel() {
    DeepObject deepObject = new DeepObject();
    DeepObject hangingReference = new DeepObject();
    deepObject.setDeepObject(hangingReference);

    HashMap<String, String> map = new HashMap<String, String>();
    map.put("data1", "value");

    assertNotNull(deepObject.getDeepObject());
    assertNull(deepObject.getDeepObject().getName());
    mapper.map(map, deepObject, "TC1");
    assertEquals("value", hangingReference.getName());
    assertNotNull(deepObject.getDeepObject());
    assertEquals("value", deepObject.getDeepObject().getName());
  }

  @Test
  public void testMapBackedDeepMapping_TwoLevels() {
    DeepObject deepObject = new DeepObject();
    DeepObject firstLevel = new DeepObject();
    deepObject.setDeepObject(firstLevel);
    DeepObject hangingReference = new DeepObject();
    firstLevel.setDeepObject(hangingReference);

    HashMap<String, String> map = new HashMap<String, String>();
    map.put("data1", "value");

    assertNotNull(deepObject.getDeepObject().getDeepObject());
    assertNull(deepObject.getDeepObject().getDeepObject().getName());
    mapper.map(map, deepObject, "TC2");
    assertEquals("value", hangingReference.getName());
    assertNotNull(deepObject.getDeepObject());
    assertNotNull(deepObject.getDeepObject().getDeepObject());
    assertEquals("value", deepObject.getDeepObject().getDeepObject().getName());
  }

  @Test
  public void testMapBackedDeepMapping_Simple() {
    DeepObject deepObject = new DeepObject();

    HashMap<String, String> map = new HashMap<String, String>();
    map.put("data1", "value");

    mapper.map(map, deepObject, "TC3");
    assertEquals("value", deepObject.getName());
  }

  @Override
  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
