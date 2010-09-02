/*
 * Copyright 2005-2010 the original author or authors.
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

import org.dozer.DozerBeanMapper;
import org.dozer.classmap.RelationshipType;
import org.dozer.functional_tests.AbstractFunctionalTest;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.FieldsMappingOptions;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.dozer.loader.api.FieldsMappingOptions.collectionStrategy;
import static org.dozer.loader.api.TypeMappingOptions.mapId;
import static org.junit.Assert.assertEquals;

/**
 * @author Dmitry Buzdin
 */
public class MapMappingTest extends AbstractFunctionalTest {

  private DozerBeanMapper beanMapper;
  private MapContainer source;
  private MapContainer target;

  @Before
  public void setUp() {
    beanMapper = new DozerBeanMapper();
    source = new MapContainer();
    target = new MapContainer();
  }

  // TODO Test with Map-Id

  @Test
  public void shouldAccumulateEntries() {
    beanMapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(MapContainer.class, MapContainer.class)
                .fields("map", "map",
                        collectionStrategy(false, RelationshipType.CUMULATIVE)
                );
      }
    });

    source.getMap().put("A", "1");
    target.getMap().put("B", "2");

    beanMapper.map(source, target);
    
    assertEquals(2, target.getMap().size());
  }

  @Test
  public void shouldRemoveOrphans() {
    beanMapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(MapContainer.class, MapContainer.class)
                .fields("map", "map",
                        collectionStrategy(true, RelationshipType.CUMULATIVE)
                );
      }
    });

    source.getMap().put("A", "1");
    target.getMap().put("B", "2");

    beanMapper.map(source, target);

    assertEquals(1, target.getMap().size());
  }

  @Test
  public void shouldMapTopLevel() {
    Map<String,String> src = new HashMap<String, String>();
    Map<String,String> dest = new HashMap<String, String>();

    src.put("A", "B");
    dest.put("B", "A");

    beanMapper.map(src, dest);

    assertEquals(2, dest.size());
  }

  public static class MapContainer {

    private Map<String,String> map = new HashMap<String,String>();

    public Map<String, String> getMap() {
      return map;
    }

    public void setMap(Map<String, String> map) {
      this.map = map;
    }
  }

}
