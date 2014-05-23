package org.dozer.functional_tests.tuple;

import org.dozer.Mapper;
import org.dozer.functional_tests.AbstractFunctionalTest;
import org.dozer.vo.SimpleObj;
import org.dozer.vo.SimpleObjPrime;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TupleMappingTest extends AbstractFunctionalTest {
  private Mapper mapper;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    mapper = getMapper("tupleMapping.xml");
  }

  @Test
  public void testTupleObject() {
    List<TupleElementProxy> tupleElements = new ArrayList<TupleElementProxy>();
    tupleElements.add(new TupleElementProxy("field1", String.class));
    tupleElements.add(new TupleElementProxy("field2", Integer.class));
    tupleElements.add(new TupleElementProxy("field3", BigDecimal.class));
    tupleElements.add(new TupleElementProxy("field4", Double.class));
    TupleProxy.initTuples(tupleElements);
    TupleProxy tuple = new TupleProxy(new Object[]{"Field1", new Integer(11),
        BigDecimal.TEN, Double.MAX_VALUE});
    SimpleObj dest = mapper.map(tuple, SimpleObj.class);

    assertEquals(dest.getField1(), tuple.get("field1"));
    assertEquals(dest.getField2(), tuple.get("field2"));
  }
}
