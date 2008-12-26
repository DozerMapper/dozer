package net.sf.dozer.functional_tests;

import net.sf.dozer.DataObjectInstantiator;
import net.sf.dozer.MapperIF;
import net.sf.dozer.NoProxyDataObjectInstantiator;
import net.sf.dozer.vo.oneway.DestClass;
import net.sf.dozer.vo.oneway.Holder;
import net.sf.dozer.vo.oneway.SourceClass;

/**
 * @author Dmitry Buzdin
 */
public class OneWayMappingTest extends AbstractMapperTest {

    public void testOneWay() {
        MapperIF mapper = getMapper("oneWayMapping.xml");

        SourceClass source = new SourceClass("A");

        Holder holder = (Holder) mapper.map(source, Holder.class);
        DestClass dest = holder.getDest();
        
        assertNotNull(dest);
        assertEquals("A", dest.anonymousAccessor());
    }

    protected DataObjectInstantiator getDataObjectInstantiator() {
         return NoProxyDataObjectInstantiator.INSTANCE;
    }
}
