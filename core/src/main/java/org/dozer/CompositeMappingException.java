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
package org.dozer;

import java.util.List;

/**
 * Runtime exception thrown by Dozer if the configuration parameter
 * "stop-on-errors" is set to false and "collect-errors" is set to true.
 * <p>
 * All throwables that occur during the mapping can be accessed using the method
 * {@link #getCollectedErrors()}. The partially filled result can be accessed
 * using the method {@link #getResult()}.
 * <p>
 * RuntimeExceptions thrown from custom code called by Dozer during mapping (eg.
 * custom converters) are not wrapped with MappingException.
 * 
 * @author subasi.artun
 */
public class CompositeMappingException extends MappingException {

  private Object result;
  private List<Throwable> collectedErrors;

  /**
   * Creates the exception with a message and the collected throwables.
   * 
   * @param message TODO ?
   * @param collectedErrors the throwables that occured during the mapping
   */
  public CompositeMappingException(String message, Object result,
      List<Throwable> collectedErrors) {
    super(message);
    this.result = result;
    this.collectedErrors = collectedErrors;
  }

  /**
   * Returns the partially filled result object. This is the result that Dozer
   * would return if the collect-errors parameter was set to false. The result
   * is not generically typed because Java does not support catching generic
   * exceptions due to type erasure.
   * 
   * @return the partially filled result object
   */
  public Object getResult() {
    return result;
  }

  /**
   * Returns the throwables that occured during the mapping. RuntimeExceptions
   * thrown from custom code called by Dozer during mapping (eg. custom
   * converters) are not wrapped with MappingException.
   * 
   * @return the throwables that occured during the mapping with at least one
   *         element
   */
  public List<Throwable> getCollectedErrors() {
    return collectedErrors;
  }

}