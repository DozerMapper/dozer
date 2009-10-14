/*
 * Copyright 2005-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dozer.stats;

import org.dozer.util.MappingUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Internal dynamic proxy used for collecting mapping statisics. Only intended for internal use.
 * 
 * @author tierney.matt
 */
public class StatisticsInterceptor implements InvocationHandler {
  private final Object delegate;
  private final StatisticsManager statsMgr;

  public StatisticsInterceptor(Object delegate, StatisticsManager statsMgr) {
    this.delegate = delegate;
    this.statsMgr = statsMgr;
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    long start = System.currentTimeMillis();

    try {
      Object result = method.invoke(delegate, args);

      long stop = System.currentTimeMillis();
      statsMgr.increment(StatisticType.MAPPING_SUCCESS_COUNT);
      statsMgr.increment(StatisticType.MAPPING_TIME, (stop - start));

      return result;
    } catch (InvocationTargetException e) {
      Throwable ex = e.getTargetException();

      statsMgr.increment(StatisticType.MAPPING_FAILURE_COUNT);
      Throwable rootCause = MappingUtils.getRootCause(ex);
      statsMgr.increment(StatisticType.MAPPING_FAILURE_EX_TYPE_COUNT, rootCause.getClass());
      incrementClassMappingFailureTypeStat(args);
      throw ex;
    }
  }

  private void incrementClassMappingFailureTypeStat(Object[] args) {
    // Determine src and dest class name. The combination of src and dest class name will be used for the statistic entry key.
    String srcClassName = null;
    if (args[0] != null) {
      srcClassName = args[0].getClass().getName();
    }
    String destClassName = null;
    if (args[1] != null) {
      if (args[1] instanceof Class) {
        destClassName = ((Class<?>) args[1]).getName();
      } else {
        destClassName = args[1].getClass().getName();
      }
    }
    statsMgr.increment(StatisticType.MAPPING_FAILURE_TYPE_COUNT, srcClassName + "-->" + destClassName);
  }
}
