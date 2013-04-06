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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;

import org.dozer.CompositeMappingException;
import org.dozer.Mapper;
import org.dozer.vo.InsideTestObject;
import org.dozer.vo.TestObject;
import org.dozer.vo.TestObjectPrime;
import org.junit.Test;

/**
 * @author subasi.artun
 */
public class CollectErrorsTest extends AbstractFunctionalTest {

  /**
   * Tests the feature request #88.
   * 
   * The mapper is configured so that an exception occurs while mapping the
   * field "one" in the setter method. Another exception occurs while mapping
   * the field "two" in the custom converter. The third field is mapped without
   * an exception.
   */
  @Test
  public void testCollectErrors() throws Exception {
    Mapper mapper = getMapper("collectErrors.xml");
    
    // Create the test object
    TestObject to = newInstance(TestObject.class);
    to.setOne("throw me");
    to.setTwo(2);
    to.setThree(InsideTestObject.createMethod());

    try {
      mapper.map(to, TestObjectPrime.class);
      fail("CompositeMappingException was expected.");
      
    } catch (CompositeMappingException e) {
      // Check if the exceptions are gathered correctly
      assertNotNull(e.getCollectedErrors());
      assertEquals(2, e.getCollectedErrors().size());
      
      // Assert that the custom converter exception was not collected as InvocationTargetException
      for (Throwable throwable : e.getCollectedErrors()) {
        assertFalse(InvocationTargetException.class.equals(throwable.getClass()));
      }
      
      // Check if the result is partially filled despite the two exceptions
      assertTrue(e.getResult() instanceof TestObjectPrime);
      TestObjectPrime result = (TestObjectPrime) e.getResult();
      assertNull(result.getThrowAllowedExceptionOnMapPrime());
      assertNull(result.getTwoPrime());
      assertNotNull(result.getThreePrime());
    }
  }

}
