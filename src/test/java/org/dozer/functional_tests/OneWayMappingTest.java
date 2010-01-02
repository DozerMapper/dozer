package org.dozer.functional_tests;

import org.dozer.Mapper;
import org.dozer.vo.oneway.DestClass;
import org.dozer.vo.oneway.Holder;
import org.dozer.vo.oneway.SourceClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * @author Dmitry Buzdin
 */
public class OneWayMappingTest extends AbstractFunctionalTest {

  @Test
  public void testOneWay() {
    Mapper mapper = getMapper("oneWayMapping.xml");

    SourceClass source = newInstance(SourceClass.class, new Object[] {"A"});

    Holder holder = mapper.map(source, Holder.class);
    DestClass dest = holder.getDest();

    assertNotNull(dest);
    assertEquals("A", dest.anonymousAccessor());
  }

}
