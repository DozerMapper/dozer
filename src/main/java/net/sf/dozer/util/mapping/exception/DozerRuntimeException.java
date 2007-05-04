/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util.mapping.exception;

/**
 * 
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class DozerRuntimeException extends RuntimeException {

  private Throwable cause;

  public DozerRuntimeException(String arg0) {
    super(arg0);
  }

  public DozerRuntimeException(String arg0, Throwable cause) {
    // For JDK 1.3 RuntimeException - it does not support a Throwable in the constructor
    super(arg0);
    this.cause = cause;
  }
  
  public DozerRuntimeException(Throwable cause) {
    // For JDK 1.3 RuntimeException - it does not support a Throwable in the constructor
    super(cause.toString());
    this.cause = cause;
  }

  public Throwable getCause() {
    return cause;
  }
}