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

import org.dozer.vo.DeepObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Dmitry Buzdin
 */
public class MapBackedDeepMappingTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("mappings/mapBackedDeepMapping.xml");
  }

  @Test
  public void testMapBackedDeepMapping_OneLevel() {
    DeepObject deepObject = newInstance(DeepObject.class);
    DeepObject hangingReference = newInstance(DeepObject.class);
    deepObject.setDeepObject(hangingReference);

    HashMap<String, String> map = newInstance(HashMap.class);
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
    DeepObject deepObject = newInstance(DeepObject.class);
    DeepObject firstLevel = newInstance(DeepObject.class);
    deepObject.setDeepObject(firstLevel);
    DeepObject hangingReference = newInstance(DeepObject.class);
    firstLevel.setDeepObject(hangingReference);

    HashMap<String, String> map = newInstance(HashMap.class);
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
    DeepObject deepObject = newInstance(DeepObject.class);

    HashMap<String, String> map = newInstance(HashMap.class);
    map.put("data1", "value");

    mapper.map(map, deepObject, "TC3");
    assertEquals("value", deepObject.getName());
  }

}
