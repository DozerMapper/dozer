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
package net.sf.dozer.util.mapping.converters;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.IntegerConverter;

/**
 * @author tierney.matt
 */
public class DozerIntegerConverter implements Converter {
  
  private static IntegerConverter commonsIntConverter = new IntegerConverter(); 

  public Object convert(Class destClass, Object sourceObj) {
    //Boolean to Int not supported in apache common's int converter and this is why this
    //class is req'd
    if(Boolean.class.isAssignableFrom(sourceObj.getClass())) {
      boolean value = ((Boolean) sourceObj).booleanValue();
      return (value ? new Integer(1) : new Integer(0));
    } else {
      return commonsIntConverter.convert(destClass, sourceObj);
    }
  }

}
