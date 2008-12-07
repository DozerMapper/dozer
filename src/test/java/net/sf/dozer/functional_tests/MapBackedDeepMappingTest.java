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

import net.sf.dozer.util.mapping.DataObjectInstantiator;
import net.sf.dozer.util.mapping.NoProxyDataObjectInstantiator;
import net.sf.dozer.util.mapping.vo.DeepObject;

import java.util.HashMap;

/**
 * @author Dmitry Buzdin
 */
public class MapBackedDeepMappingTest extends AbstractMapperTest {

  protected void setUp() throws Exception {
    mapper = getMapper("mapBackedDeepMapping.xml");
  }

  public void testMapBackedDeepMapping_OneLevel() {
    DeepObject deepObject = new DeepObject();
    DeepObject hangingReference = new DeepObject();
    deepObject.setDeepObject(hangingReference);

    HashMap map = new HashMap();
    map.put("data1", "value");

    assertNotNull(deepObject.getDeepObject());
    assertNull(deepObject.getDeepObject().getName());
    mapper.map(map, deepObject, "TC1");
    assertEquals("value", hangingReference.getName());
    assertNotNull(deepObject.getDeepObject());
    assertEquals("value", deepObject.getDeepObject().getName());
  }

  public void testMapBackedDeepMapping_TwoLevels() {
    DeepObject deepObject = new DeepObject();
    DeepObject firstLevel = new DeepObject();
    deepObject.setDeepObject(firstLevel);
    DeepObject hangingReference = new DeepObject();
    firstLevel.setDeepObject(hangingReference);

    HashMap map = new HashMap();
    map.put("data1", "value");

    assertNotNull(deepObject.getDeepObject().getDeepObject());
    assertNull(deepObject.getDeepObject().getDeepObject().getName());
    mapper.map(map, deepObject, "TC2");
    assertEquals("value", hangingReference.getName());
    assertNotNull(deepObject.getDeepObject());
    assertNotNull(deepObject.getDeepObject().getDeepObject());
    assertEquals("value", deepObject.getDeepObject().getDeepObject().getName());
  }
  
  public void testMapBackedDeepMapping_Simple() {
    DeepObject deepObject = new DeepObject();

    HashMap map = new HashMap();
    map.put("data1", "value");

    mapper.map(map, deepObject, "TC3");
    assertEquals("value", deepObject.getName());
  }


  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
