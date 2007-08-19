package net.sf.dozer.functional_tests;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

public class ObjectInstantiator {
  
  public static final ObjectInstantiator NO_PROXY_INSTANTIATOR = new ObjectInstantiator(0);
  public static final ObjectInstantiator PROXY_INSTANTIATOR = new ObjectInstantiator(1);
  
  private int proxyMode;
  
  private ObjectInstantiator(int proxyMode) {
    this.proxyMode = proxyMode;
  }
  
  public Object create(Class classToInstantiate) {
    if (proxyMode == 0) {
      try {
        return classToInstantiate.newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } else if (proxyMode == 1) {
      return proxyNewInstance(classToInstantiate);
    } else {
      throw new  RuntimeException("invalid proxy mode specified: " + proxyMode);
    }
  }
  
  private Object proxyNewInstance(Class clazz) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(clazz);
    enhancer.setCallback(NoOp.INSTANCE);
    return enhancer.create();
    }


}
