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
package net.sf.dozer.util.mapping.util;

import net.sf.dozer.util.mapping.DozerTestBase;
import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.DozerClass;
import net.sf.dozer.util.mapping.vo.TestObject;
import net.sf.dozer.util.mapping.vo.TestObjectPrime;

/**
 * @author tierney.matt
 */
public class DestBeanCreatorTest extends DozerTestBase {
  private final DestBeanCreator destBeanCreator = new DestBeanCreator(MappingUtils.storedFactories);
  
  public void testCreatDestBeanNoFactory() throws Exception {
    ClassMap classMap = new ClassMap();
    DozerClass destClass = new DozerClass();
    destClass.setName(TestObject.class.getName());
    classMap.setDestClass(destClass);

    TestObject bean = (TestObject) destBeanCreator.create(null, classMap, null);

    assertNotNull(bean);
    assertNull(bean.getCreatedByFactoryName());
  }

  public void testCreatBeanFromFactory() throws Exception {
    DozerClass destClass = new DozerClass();
    String factoryName = "net.sf.dozer.util.mapping.factories.SampleCustomBeanFactory";
    destClass.setName(TestObject.class.getName());
    destClass.setBeanFactory(factoryName);

    DozerClass srcClass = new DozerClass();
    srcClass.setName(TestObjectPrime.class.getName());
    srcClass.setBeanFactory(factoryName);

    ClassMap classMap = new ClassMap();
    classMap.setDestClass(destClass);
    classMap.setSourceClass(srcClass);

    TestObject bean = (TestObject) destBeanCreator.create(new TestObjectPrime(), classMap, null);

    assertNotNull(bean);
    assertEquals(factoryName, bean.getCreatedByFactoryName());
  }
    
}
