package net.sf.dozer.functional_tests.proxied;

import net.sf.dozer.DataObjectInstantiator;
import net.sf.dozer.ProxyDataObjectInstantiator;
import net.sf.dozer.functional_tests.TrimStringsTest;

public class ProxiedTrimStringsTest extends TrimStringsTest {

  @Override
  protected DataObjectInstantiator getDataObjectInstantiator() {
    return ProxyDataObjectInstantiator.INSTANCE;
  }

}
