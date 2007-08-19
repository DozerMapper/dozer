package net.sf.dozer.functional_tests.proxied;

import net.sf.dozer.functional_tests.DataObjectInstantiator;
import net.sf.dozer.functional_tests.TrimStringsTest;

public class ProxiedTrimStringsTest extends TrimStringsTest {
  
  protected DataObjectInstantiator getDataObjectInstantiator() {
    return DataObjectInstantiator.PROXY_INSTANTIATOR;
  }

}
