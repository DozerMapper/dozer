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
package org.dozer.functional_tests.builder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.dozer.classmap.RelationshipType;
import org.dozer.functional_tests.AbstractFunctionalTest;
import org.dozer.loader.api.BeanMappingBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.dozer.loader.api.FieldsMappingOptions.collectionStrategy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * @author Dmitry Buzdin
 */
public class MapMappingTest extends AbstractFunctionalTest {

  private MapContainer source;
  private MapContainer target;

  @Before
  public void setUp() {
    source = new MapContainer();
    target = new MapContainer();
  }

  // TODO Test with Map-Id

  @Test
  public void shouldAccumulateEntries() {
    Mapper beanMapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(MapContainer.class, MapContainer.class)
                        .fields("map", "map",
                                collectionStrategy(false, RelationshipType.CUMULATIVE)
                        );
              }
            })
            .build();

    source.getMap().put("A", "1");
    target.getMap().put("B", "2");

    beanMapper.map(source, target);

    assertEquals(2, target.getMap().size());
  }

  @Test
  public void shouldRemoveOrphans() {
    Mapper beanMapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(MapContainer.class, MapContainer.class)
                        .fields("map", "map",
                                collectionStrategy(true, RelationshipType.CUMULATIVE)
                        );
              }
            })
            .build();

    source.getMap().put("A", "1");
    target.getMap().put("B", "2");

    beanMapper.map(source, target);

    assertEquals(1, target.getMap().size());
  }

  @Test
  @Ignore("Backwards mapping does not work")
  public void shouldMapEmbeddedList() {
    Mapper beanMapper = DozerBeanMapperBuilder.create()
            .withMappingBuilder(new BeanMappingBuilder() {
              @Override
              protected void configure() {
                mapping(Map.class, ListContainer.class)
                        .fields(this_().mapKey("embedded"), "list",
                                collectionStrategy(true, RelationshipType.NON_CUMULATIVE)
                        );
              }
            })
            .build();

    HashMap<String, Object> map = new HashMap<>();
    List<String> list = new ArrayList<>();
    list.add("A");
    map.put("embedded", list);

    ListContainer container = new ListContainer();

    beanMapper.map(map, container);

    assertEquals(1, container.getList().size());
    assertEquals("A", container.getList().get(0));

    HashMap<String, Object> copy = new HashMap<>();
    
    beanMapper.map(container, copy);

    assertEquals(map, copy);
  }

  @Test
  public void shouldMapTopLevel() {
    Map<String, String> src = new HashMap<>();
    Map<String, String> dest = new HashMap<>();

    src.put("A", "B");
    dest.put("B", "A");

    DozerBeanMapperBuilder.buildDefault().map(src, dest);

    assertEquals(2, dest.size());
  }

  @Test
  public void testDozerMultiTypeMapContainingCollections() throws Exception {
    Mapper dozerBeanMapper = DozerBeanMapperBuilder.buildDefault();

    // Setting up test data, multiple types in a single Map
    DozerExampleEntry entry = new DozerExampleEntry();
    {
      entry.getMap().put("A", "foobar");
      entry.getMap().put("B", new Date(0));
      entry.getMap().put("C", Boolean.TRUE);
      // This array list will produce the problem
      // Remove it and the test case will succeed
      ArrayList<String> genericList = new ArrayList<>();
      genericList.add("something");
      entry.getMap().put("D", genericList);
      entry.getMap().put("E", new BigDecimal("0.00"));
    }

    DozerExampleEntry mapped = dozerBeanMapper.map(entry, DozerExampleEntry.class);

    // All the fields which are visited/mapped before the
    // ArrayList are mapped successfully and to correct type
    assertEquals("foobar", mapped.getMap().get("A"));
    assertEquals(new Date(0), mapped.getMap().get("B"));
    assertEquals(Boolean.TRUE, mapped.getMap().get("C"));
    ArrayList<String> expectedList = new ArrayList<>();
    expectedList.add("something");
    assertEquals(expectedList, mapped.getMap().get("D"));
    assertNotSame(expectedList, mapped.getMap().get("D"));

    // The BigDecimal was visited _after_ the ArrayList
    // and thus converted to String due to the bug.
    assertEquals(new BigDecimal("0.00"), mapped.getMap().get("E"));
  }

  public static class DozerExampleEntry {
    /*
    * Explicitly using a sorted TreeMap here to force the visiting order of the entries in the
    * Map. A, B and C are converted successfully. D too, but this will trigger the
    * setDestinationTypeHint(). And that will lead to the invalid mapping of entry E.
    */
    private Map<String, Object> map = new TreeMap<>();

    public Map<String, Object> getMap() {
      return this.map;
    }

    public void setMap(Map<String, Object> aMap) {
      this.map = aMap;
    }
  }

  public static class MapContainer {

    private Map<String, String> map = new HashMap<>();

    public Map<String, String> getMap() {
      return map;
    }

    public void setMap(Map<String, String> map) {
      this.map = map;
    }
  }

  public static class ListContainer {
    private List<String> list = new ArrayList<>();

    public List<String> getList() {
      return list;
    }

    public void setList(List<String> list) {
      this.list = list;
    }
  }

}
