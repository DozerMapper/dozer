package net.sf.dozer.util.mapping;

import java.util.ArrayList;

import net.sf.dozer.util.mapping.util.TestDataFactory;
import net.sf.dozer.util.mapping.vo.TestObject;
import net.sf.dozer.util.mapping.vo.TestObjectPrime;
import net.sf.dozer.util.mapping.vo.inheritance.AnotherSubClass;
import net.sf.dozer.util.mapping.vo.inheritance.AnotherSubClassPrime;

/**
 * @author tierney.matt
 * @author Ozzie Gurkan
 */
public class MultiThreadedTest extends AbstractDozerTest {
  private MapperIF mapper;

  public MultiThreadedTest() {
    ArrayList list = new ArrayList();
    list.add("dozerBeanMapping.xml");
    mapper = new DozerBeanMapper(list);
  }

  /*
   * See Bug #1550275.  ConcurrentModificationException was being thrown  
   */
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
    TestObject src = TestDataFactory.getInputGeneralMappingTestObject();
    AnotherSubClass src2 = TestDataFactory.getAnotherSubClass();
    
    mapper.map(src, TestObjectPrime.class);
    mapper.map(src2, AnotherSubClassPrime.class);
  }

}