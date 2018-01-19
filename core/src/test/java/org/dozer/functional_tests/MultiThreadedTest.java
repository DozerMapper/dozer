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
package org.dozer.functional_tests;


import org.dozer.vo.TestObject;
import org.dozer.vo.TestObjectPrime;
import org.dozer.vo.inheritance.AnotherSubClass;
import org.dozer.vo.inheritance.AnotherSubClassPrime;
import org.junit.Before;
import org.junit.Test;

/**
 * @author tierney.matt
 * @author Ozzie Gurkan
 */
public class MultiThreadedTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    mapper = getMapper(new String[] {"testDozerBeanMapping.xml"});
  }

  /*
   * See Bug #1550275. ConcurrentModificationException was being thrown
   */
  @Test
  public void testMultiThreadedMapping() throws Exception {

    Runnable run = new Runnable() {
      public void run() {
        mapSomething();
      }
    };
    Runnable run2 = new Runnable() {
      public void run() {
        mapSomething();
      }
    };
    Runnable run3 = new Runnable() {
      public void run() {
        mapSomething();
      }
    };
    Runnable run4 = new Runnable() {
      public void run() {
        mapSomething();
      }
    };

    Thread t1 = new Thread(run, "Thread-1");
    Thread t2 = new Thread(run2, "Thread-2");
    Thread t3 = new Thread(run3, "Thread-3");
    Thread t4 = new Thread(run4, "Thread-4");

    t1.start();
    t2.start();
    t3.start();
    t4.start();
    t1.join();
    t2.join();
    t3.join();
    t4.join();
  }

  private void mapSomething() {
    TestObject src = testDataFactory.getInputGeneralMappingTestObject();
    AnotherSubClass src2 = testDataFactory.getAnotherSubClass();

    mapper.map(src, TestObjectPrime.class);
    mapper.map(src2, AnotherSubClassPrime.class);
  }

}
