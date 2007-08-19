package net.sf.dozer.functional_tests.proxied;

import net.sf.dozer.functional_tests.AbstractTrimStringsTest;
import net.sf.dozer.functional_tests.ObjectInstantiator;

public class ProxiedTrimStringsTest extends AbstractTrimStringsTest {
  
  protected ObjectInstantiator getDataObjectInstantiator() {
    return ObjectInstantiator.CGLIB_INSTANTIATOR;
  }

}
