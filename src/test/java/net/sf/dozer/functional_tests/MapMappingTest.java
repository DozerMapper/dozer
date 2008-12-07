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
import net.sf.dozer.util.mapping.vo.map.MapToMap;
import net.sf.dozer.util.mapping.vo.map.MapToMapPrime;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Buzdin
 */
public class MapMappingTest extends AbstractMapperTest{

  protected void setUp() throws Exception {
    mapper = getMapper("mapMapping6.xml");
  }

//  public void testMapWithNullEntries_WithoutHints() {
//    MapToMap source = new MapToMap();
//    HashMap map = new HashMap();
//    map.put("A", Boolean.TRUE);
//    map.put("B", null);
//    source.setStandardMap(map);
//
//    MapToMapPrime destination = new MapToMapPrime();
//    HashMap hashMap = new HashMap();
//    hashMap.put("C", Boolean.TRUE);
//    destination.setStandardMap(hashMap);
//
//    mapper.map(source, destination);
//
//    Map resultingMap = destination.getStandardMap();
//
//    assertNotNull(resultingMap);
//    assertEquals(2, resultingMap.size());
//    assertEquals(Boolean.TRUE, resultingMap.get("A"));
//    assertNull(resultingMap.get("B"));
//  }


  public void testMapWithNullEntries_NullPointer() {
    MapToMap source = new MapToMap();
    HashMap map = new HashMap();
    map.put("A", null);
    source.setStandardMap(map);

    MapToMapPrime destination = new MapToMapPrime();
    HashMap map2 = new HashMap();
    map2.put("B", Boolean.TRUE);
    destination.setStandardMap(map2);

    mapper.map(source, destination);

    Map resultingMap = destination.getStandardMap();

    assertNotNull(resultingMap);
    assertEquals(2, resultingMap.size());
    assertNull(resultingMap.get("A"));
    assertEquals(Boolean.TRUE, resultingMap.get("B"));
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
