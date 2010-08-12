package org.dozer.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Dmitry Buzdin
 */
public final class IteratorUtils {

  public static <T> List<T> toList(Iterator<T> iterator) {
    List<T> list = new ArrayList<T>();    
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    return list;
  }

}
