package org.dozer.vo.map;

import java.util.HashMap;
import java.util.Map;

import org.dozer.vo.BaseTestObject;


public class GenericDOM extends BaseTestObject {

  private Map hmData = new HashMap();

  public Object get(Object key) {
    return hmData.get(key);
  }

  public void put(Object key, Object value) {
    hmData.put(key, value);
  }

}
