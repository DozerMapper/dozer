package net.sf.dozer.functional_tests.proxied;

import net.sf.dozer.functional_tests.TrimStringsTest;
import net.sf.dozer.util.mapping.DataObjectInstantiator;
import net.sf.dozer.util.mapping.ProxyDataObjectInstantiator;

public class ProxiedTrimStringsTest extends TrimStringsTest {

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return ProxyDataObjectInstantiator.INSTANCE;
  }

}
