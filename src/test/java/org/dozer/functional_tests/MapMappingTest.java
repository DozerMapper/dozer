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
package org.dozer.functional_tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


import org.dozer.vo.TestObject;
import org.dozer.vo.map.MapToMap;
import org.dozer.vo.map.MapToMapPrime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Dmitry Buzdin
 */
public class MapMappingTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper("mapMapping6.xml");
  }

  @Ignore("failing for some reason.  was commented out")
  @Test
  public void testMapWithNullEntries_WithoutHints() {
    MapToMap source = newInstance(MapToMap.class);
    HashMap<String, Object> map = newInstance(HashMap.class);
    map.put("A", Boolean.TRUE);
    map.put("B", null);
    source.setStandardMap(map);
    MapToMapPrime destination = newInstance(MapToMapPrime.class);
    HashMap<String, Object> hashMap = newInstance(HashMap.class);
    hashMap.put("C", Boolean.TRUE);
    destination.setStandardMap(hashMap);

    mapper.map(source, destination);

    Map<?, ?> resultingMap = destination.getStandardMap();

    assertNotNull(resultingMap);
    assertEquals(2, resultingMap.size());
    assertEquals(Boolean.TRUE, resultingMap.get("A"));
    assertNull(resultingMap.get("B"));
  }

  @Test
  public void testMapWithNullEntries_NullPointer() {
    MapToMap source = newInstance(MapToMap.class);
    HashMap<String, TestObject> map = newInstance(HashMap.class);
    map.put("A", null);
    source.setStandardMap(map);

    MapToMapPrime destination = newInstance(MapToMapPrime.class);
    HashMap<String, Serializable> map2 = newInstance(HashMap.class);
    map2.put("B", Boolean.TRUE);
    destination.setStandardMap(map2);

    mapper.map(source, destination);

    Map<?, ?> resultingMap = destination.getStandardMap();

    assertNotNull(resultingMap);
    assertEquals(2, resultingMap.size());
    assertNull(resultingMap.get("A"));
    assertEquals(Boolean.TRUE, resultingMap.get("B"));
  }

  @Test
  public void testMapNullEntry_MultipleEntries() {
    MapToMap source = newInstance(MapToMap.class);
    HashMap<String, Boolean> map = newInstance(HashMap.class);
    map.put("A", null);
    map.put("B", null);
    map.put("C", null);
    source.setStandardMap(map);

    MapToMapPrime destination = newInstance(MapToMapPrime.class);
    HashMap<String, Serializable> map2 = newInstance(HashMap.class);
    destination.setStandardMap(map2);

    mapper.map(source, destination);

    Map<?, ?> resultingMap = destination.getStandardMap();

    assertNotNull(resultingMap);
    assertEquals(3, resultingMap.size());
    assertNull(resultingMap.get("A"));
    assertNull(resultingMap.get("B"));
    assertNull(resultingMap.get("C"));
  }

  @Override
  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
