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
package net.sf.dozer.functional_tests;

import static org.junit.Assert.assertEquals;
import net.sf.dozer.DataObjectInstantiator;
import net.sf.dozer.NoProxyDataObjectInstantiator;
import net.sf.dozer.vo.AnotherTestObject;
import net.sf.dozer.vo.AnotherTestObjectPrime;
import net.sf.dozer.vo.SimpleObj;
import net.sf.dozer.vo.SimpleObjPrime;
import net.sf.dozer.vo.TestObject;
import net.sf.dozer.vo.TestObjectPrime;

import org.junit.Before;
import org.junit.Test;

public class TrimStringsTest extends AbstractMapperTest {

  @Before
  public void setUp() throws Exception {
    mapper = getMapper(new String[] { "trimStringsMapping.xml" });
  }

  @Test
  public void testTrimStrings_Global() {
    AnotherTestObject src = (AnotherTestObject) newInstance(AnotherTestObject.class);
    src.setField3("      valueNeedingTrimmed       ");
    src.setField4("      anotherValueNeedingTrimmed       ");
    src.setField5("  127 ");

    AnotherTestObjectPrime dest = mapper.map(src, AnotherTestObjectPrime.class);

    assertEquals("valueNeedingTrimmed", dest.getField3());
    assertEquals("anotherValueNeedingTrimmed", dest.getTo().getOne());
    assertEquals("field 5 not trimmed", Integer.valueOf("127"), dest.getField5());
  }

  @Test
  public void testTrimStrings_ClassMapLevel() {
    TestObject src = (TestObject) newInstance(TestObject.class);
    String value = "    shouldNotBeNeedingTrimmed     ";
    src.setOne(value);

    TestObjectPrime dest = mapper.map(src, TestObjectPrime.class);

    assertEquals(value, dest.getOnePrime());
  }

  @Test
  public void testTrimStrings_ImplicitMapping() {
    SimpleObj src = (SimpleObj) newInstance(SimpleObj.class);
    src.setField1("      valueNeedingTrimmed       ");

    SimpleObjPrime dest = mapper.map(src, SimpleObjPrime.class);

    assertEquals("valueNeedingTrimmed", dest.getField1());
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}
