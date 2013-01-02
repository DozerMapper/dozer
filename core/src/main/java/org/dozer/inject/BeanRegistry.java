package org.dozer.inject;

import java.util.Collection;

/**
 * @author Dmitry Buzdin
 */
public interface BeanRegistry {

  void register(Class<?> type);

  <T> T getBean(Class<T> type);

  <T> Collection<T> getBeans(Class<T> type);

}
