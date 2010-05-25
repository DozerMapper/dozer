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
package org.dozer.classmap;

import java.util.ArrayList;
import java.util.List;


import org.dozer.AbstractDozerTest;
import org.dozer.classmap.ClassMap;
import org.dozer.classmap.Configuration;
import org.dozer.fieldmap.FieldMap;
import org.dozer.fieldmap.GenericFieldMap;
import org.junit.Before;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class ClassMapTest extends AbstractDozerTest {

  private ClassMap classMap;
  private Configuration globalConfiguration;

  @Override
  @Before
  public void setUp() throws Exception {
    globalConfiguration = new Configuration();
    classMap = new ClassMap(globalConfiguration);
  }

  @Test
  public void testAddFieldMappings() throws Exception {
    ClassMap cm = new ClassMap(null);
    GenericFieldMap fm = new GenericFieldMap(cm);

    cm.addFieldMapping(fm);

    assertNotNull(cm.getFieldMaps());
    assertTrue(cm.getFieldMaps().size() == 1);
    assertEquals(cm.getFieldMaps().get(0), fm);
  }

  @Test
  public void testSetFieldMappings() throws Exception {
    ClassMap cm = new ClassMap(null);
    GenericFieldMap fm = new GenericFieldMap(cm);
    List<FieldMap> fmList = new ArrayList<FieldMap>();
    fmList.add(fm);

    cm.setFieldMaps(fmList);

    assertNotNull(cm.getFieldMaps());
    assertTrue(cm.getFieldMaps().size() == fmList.size());
    assertEquals(cm.getFieldMaps().get(0), fmList.get(0));
  }

  @Test
  public void testGetFieldMapUsingDest() {
    assertNull(classMap.getFieldMapUsingDest("", true));
  }

  @Test
  public void testProvideAlternateName() {
    assertEquals("field1", classMap.provideAlternateName("Field1"));
  }

}
