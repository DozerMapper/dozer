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
package org.dozer.fieldmap;

import org.dozer.classmap.ClassMap;
import org.dozer.factory.DestBeanCreator;
import org.dozer.propertydescriptor.DozerPropertyDescriptor;
import org.dozer.propertydescriptor.FieldPropertyDescriptor;
import org.dozer.propertydescriptor.JavaBeanPropertyDescriptor;
import org.dozer.propertydescriptor.MapPropertyDescriptor;
import org.dozer.util.DozerConstants;
import org.dozer.util.MappingUtils;

/**
 * Only intended for internal use. Handles field mapping involving Tuple Backed properties. Tuple backed property support
 * includes top level class Tuple data type, field level Tuple data type, and custom Tuple backed objects that provide custom
 * map-get/set methods.
 * 
 * @author ikozar
 * 
 */
public class TupleFieldMap extends MapFieldMap {

  public TupleFieldMap(ClassMap classMap) {
    super(classMap);
  }
}
