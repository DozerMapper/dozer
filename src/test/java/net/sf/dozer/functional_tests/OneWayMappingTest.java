package net.sf.dozer.functional_tests;

import net.sf.dozer.DataObjectInstantiator;
import net.sf.dozer.Mapper;
import net.sf.dozer.NoProxyDataObjectInstantiator;
import net.sf.dozer.vo.oneway.DestClass;
import net.sf.dozer.vo.oneway.Holder;
import net.sf.dozer.vo.oneway.SourceClass;

/**
 * @author Dmitry Buzdin
 */
public class OneWayMappingTest extends AbstractMapperTest {

  public void testOneWay() {
    Mapper mapper = getMapper("oneWayMapping.xml");

    SourceClass source = new SourceClass("A");

    Holder holder = mapper.map(source, Holder.class);
    DestClass dest = holder.getDest();

    assertNotNull(dest);
    assertEquals("A", dest.anonymousAccessor());
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }
}
