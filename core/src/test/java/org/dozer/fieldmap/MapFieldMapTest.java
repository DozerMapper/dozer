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
package org.dozer.fieldmap;

import static org.mockito.Mockito.mock;

import org.dozer.AbstractDozerTest;
import org.dozer.classmap.MappingDirection;
import org.dozer.classmap.RelationshipType;
import org.junit.Test;

/**
 * @author dmitry.buzdin
 */
public class MapFieldMapTest extends AbstractDozerTest {

  @Test
  public void testConstructor() {
    FieldMap fieldMap = mock(FieldMap.class);
    MapFieldMap source = new MapFieldMap(fieldMap);

    source.setCopyByReference(true);
    source.setCustomConverter("converter");
    source.setCustomConverterId("coverterId");
    source.setCustomConverterParam("param");
    source.setDestField(new DozerField("name", "type"));
    source.setDestHintContainer(new HintContainer());
    source.setDestDeepIndexHintContainer(new HintContainer());
    source.setMapId("mapId");
    source.setRelationshipType(RelationshipType.NON_CUMULATIVE);
    source.setRemoveOrphans(true);
    source.setSrcField(new DozerField("name", "type"));
    source.setSrcHintContainer(new HintContainer());
    source.setSrcDeepIndexHintContainer(new HintContainer());
    source.setType(MappingDirection.ONE_WAY);

    MapFieldMap result = new MapFieldMap(source);

    assertEquals(source.isCopyByReference(), result.isCopyByReference());
    assertEquals(source.getCustomConverter(), result.getCustomConverter());
    assertEquals(source.getCustomConverterId(), result.getCustomConverterId());
    assertEquals(source.getCustomConverterParam(), result.getCustomConverterParam());
    assertEquals(source.getDestField(), result.getDestField());
    assertEquals(source.getDestHintContainer(), result.getDestHintContainer());
    assertEquals(source.getDestDeepIndexHintContainer(), result.getDestDeepIndexHintContainer());
    assertEquals(source.getMapId(), result.getMapId());
    assertEquals(source.getRelationshipType(), result.getRelationshipType());
    assertEquals(source.isRemoveOrphans(), result.isRemoveOrphans());
    assertEquals(source.getSrcField(), result.getSrcField());
    assertEquals(source.getSrcHintContainer(), result.getSrcHintContainer());
    assertEquals(source.getSrcDeepIndexHintContainer(), result.getSrcDeepIndexHintContainer());
    assertEquals(source.getType(), result.getType());
  }

}
