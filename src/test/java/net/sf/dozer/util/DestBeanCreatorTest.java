/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util;

import net.sf.dozer.AbstractDozerTest;
import net.sf.dozer.functional_tests.vo.TestObject;
import net.sf.dozer.functional_tests.vo.TestObjectPrime;
import net.sf.dozer.util.DestBeanCreator;

/**
 * @author tierney.matt
 */
public class DestBeanCreatorTest extends AbstractDozerTest {

  public void testCreatDestBeanNoFactory() throws Exception {
    TestObject bean = (TestObject) DestBeanCreator.create(null, null, TestObject.class, null, null, null, null);

    assertNotNull(bean);
    assertNull(bean.getCreatedByFactoryName());
  }

  public void testCreatBeanFromFactory() throws Exception {
    String factoryName = "net.sf.dozer.factories.SampleCustomBeanFactory";
    TestObject bean = (TestObject) DestBeanCreator.create(new TestObjectPrime(), TestObjectPrime.class, TestObject.class, null,
        factoryName, null, null);

    assertNotNull(bean);
    assertEquals(factoryName, bean.getCreatedByFactoryName());
  }

}
