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
package org.dozer.functional_tests;

import junit.framework.Assert;
import org.dozer.vo.ArrayDest;
import org.dozer.vo.ArraySource;
import org.junit.Test;

/**
 * Collections and arrays test
 *
 * @author Vadim Shaigorodskiy
 */
public class CollectionTest
  extends AbstractFunctionalTest {

  @Override
  public void setUp() throws Exception {
    mapper = getMapper("arrayMapping.xml");
  }

  /**
   * Test shows how simple array grows, when dest array is not null
   */
  @Test
  public void testArrayGrowConversion() {
    ArraySource sourceBean = new ArraySource();
    String[] sourceArray = sourceBean.getPreInitializedArray();
    sourceArray[0] = "1";
    sourceArray[1] = "2";

    ArrayDest destinationBean = new ArrayDest();

    mapper.map(sourceBean, destinationBean, "array");

    String[] destinationArray = destinationBean.getPreInitializedArray();
    Assert.assertEquals(sourceArray.length + 2, destinationArray.length);

  }


  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

  
}
