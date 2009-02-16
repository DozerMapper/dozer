package org.dozer.functional_tests.proxied;

import org.dozer.functional_tests.DataObjectInstantiator;
import org.dozer.functional_tests.TrimStringsTest;

public class ProxiedTrimStringsTest extends TrimStringsTest {

  @Override
  protected DataObjectInstantiator getDataObjectInstantiator() {
    return ProxyDataObjectInstantiator.INSTANCE;
  }

}
