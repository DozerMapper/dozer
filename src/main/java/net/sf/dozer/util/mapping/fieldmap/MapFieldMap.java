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
package net.sf.dozer.util.mapping.fieldmap;

import java.lang.reflect.Method;

import net.sf.dozer.util.mapping.classmap.ClassMap;
import net.sf.dozer.util.mapping.util.MappingUtils;
import net.sf.dozer.util.mapping.util.ReflectionUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Only intended for internal use.
 * 
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class MapFieldMap extends FieldMap {
  private static final Log log = LogFactory.getLog(MapFieldMap.class);

  public void writeDestinationValue(Object destObj, Object destFieldValue, ClassMap classMap) {
    Method destFieldWriteMethod = getDestFieldWriteMethod(destObj.getClass());
    if (destFieldWriteMethod.getParameterTypes().length == 2) { // this is a 'put'
      String key = null;
      if (StringUtils.isEmpty(getSourceField().getKey())) {
        key = getSourceField().getName();
      } else {
        key = getSourceField().getKey();
      }

      if (log.isDebugEnabled()) {
        log.debug("Getting ready to invoke write method on the destination object: "
            + MappingUtils.getClassNameWithoutPackage(destObj.getClass()) + ", Write Method: "
            + destFieldWriteMethod.getName() + ", Dest value: " + destFieldValue);
      }
      ReflectionUtils.invoke(destFieldWriteMethod, destObj, new Object[] { key, destFieldValue });
    } else { // this is a 'get'
      super.writeDestinationValue(destObj, destFieldValue, classMap);
    }
  }

}
