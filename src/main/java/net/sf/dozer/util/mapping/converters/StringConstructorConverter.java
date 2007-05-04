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
import java.lang.reflect.Constructor;


/**
 * Internal class for converting String --> Complex Data Types with a String constructor.  Only intended for internal use. 

 * @author tierney.matt
 */
public class StringConstructorConverter implements Converter {

  public Object convert(Class destClass, Object sourceObj) {
    try {
      Constructor constructor = destClass.getConstructor(new Class[] {String.class});
      return constructor.newInstance(new Object[] {sourceObj.toString()});
    } catch (NoSuchMethodException e) {
      // just return the string
      return sourceObj.toString();
    } catch (Exception e) {
      throw new ConversionException(e);
    }
  }

}
