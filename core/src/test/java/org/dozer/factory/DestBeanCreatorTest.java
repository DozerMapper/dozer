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
package org.dozer.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.dozer.AbstractDozerTest;
import org.dozer.config.BeanContainer;
import org.dozer.vo.TestObject;
import org.dozer.vo.TestObjectPrime;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class DestBeanCreatorTest extends AbstractDozerTest {

  private DestBeanCreator destBeanCreator = new DestBeanCreator(new BeanContainer());

  @Test
  public void testCreatDestBeanNoFactory() throws Exception {
    TestObject bean = (TestObject) destBeanCreator.create(new BeanCreationDirective(null, null, TestObject.class, null, null, null, null));

    assertNotNull(bean);
    assertNull(bean.getCreatedByFactoryName());
  }

  @Test
  public void testCreatBeanFromFactory() throws Exception {
    String factoryName = "org.dozer.functional_tests.support.SampleCustomBeanFactory";
    TestObject bean = (TestObject) destBeanCreator.create(
        new BeanCreationDirective(new TestObjectPrime(), TestObjectPrime.class, TestObject.class, null, factoryName, null, null));

    assertNotNull(bean);
    assertEquals(factoryName, bean.getCreatedByFactoryName());
  }

  @Test
  public void testMap() {
    Map map = destBeanCreator.create(Map.class);
    assertTrue(map instanceof HashMap);

    TreeMap treeMap = destBeanCreator.create(TreeMap.class);
    assertNotNull(treeMap);
  }


}
