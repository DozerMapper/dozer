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
package org.dozer.util;

import org.apache.commons.logging.Log;

/**
 * Internal class that is just a thin wrapper for logging one time dozer initialization messages. These messages will be
 * written to system.out as well as log.info. To enable dual writes to System.out: -Ddozer.debug=true Only intended for
 * internal use.
 * 
 * @author tierney.matt
 */
public final class InitLogger {

  private static boolean debugEnabled = false;

  static {
    String sysProp = System.getProperty(DozerConstants.DEBUG_SYS_PROP);
    if (sysProp != null && !sysProp.trim().equals("false")) {
      debugEnabled = true;
    }
  }

  private InitLogger() {
  }

  public static void log(Log log, String msg) {
    if (debugEnabled) {
      System.out.println("dozer:  " + msg);
    }
    log.info(msg);
  }
}
