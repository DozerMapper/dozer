/**
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
package org.dozer.classmap;

import org.dozer.AbstractDozerTest;
import org.dozer.classmap.generator.BeanMappingGenerator;
import org.dozer.fieldmap.FieldMap;
import org.dozer.util.DozerConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author Dmitry Buzdin
 */
public class ClassMapBuilderTest extends AbstractDozerTest {

  private ClassMapBuilder.CollectionMappingGenerator collectionMappingGenerator;
  private ClassMapBuilder.MapMappingGenerator mapMappingGenerator;
  private BeanMappingGenerator beanMappingGenerator;
  private Configuration configuration;

  @Before
  public void setUp() throws Exception {
    collectionMappingGenerator = new ClassMapBuilder.CollectionMappingGenerator();
    mapMappingGenerator = new ClassMapBuilder.MapMappingGenerator();
    beanMappingGenerator = new BeanMappingGenerator();

    configuration = new Configuration();
  }

  @Test
  public void shouldPrepareMappingsForCollection() throws Exception {
    ClassMap classMap = new ClassMap(null);

    collectionMappingGenerator.apply(classMap, configuration);

    List<FieldMap> fieldMaps = classMap.getFieldMaps();
    assertEquals(1, fieldMaps.size());
    assertEquals(DozerConstants.SELF_KEYWORD, fieldMaps.get(0).getSrcFieldName());
    assertEquals(DozerConstants.SELF_KEYWORD, fieldMaps.get(0).getDestFieldName());
  }
}
