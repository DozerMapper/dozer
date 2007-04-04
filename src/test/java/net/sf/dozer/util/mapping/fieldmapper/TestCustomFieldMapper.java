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
package net.sf.dozer.util.mapping.fieldmapper;

import net.sf.dozer.util.mapping.CustomFieldMapperIF;
import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;
import net.sf.dozer.util.mapping.vo.CustomDoubleObject;
import net.sf.dozer.util.mapping.vo.CustomDoubleObjectIF;
import net.sf.dozer.util.mapping.vo.SimpleObj;
import net.sf.dozer.util.mapping.vo.SimpleObjPrime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author tierney.matt
 */
public class TestCustomFieldMapper implements CustomFieldMapperIF {
  
  public static String FIELD_VALUE = "This field was set on the dest object by the TestCustomFieldMapper";
  
  public boolean mapField(Object sourceObj, Object destObj, Object sourceFieldValue, 
      ClassMap classMap, FieldMap fieldMapping) {
    boolean result = false;
    
    if (!(sourceObj instanceof SimpleObj)) {
      throw new MappingException("Unsupported source object type.  Should be of type: SimpleObj"); 
    }

    if (!(destObj instanceof SimpleObjPrime)) {
      throw new MappingException("Unsupported dest object type.  Should be of type: SimpleObjPrime"); 
    }
    
    if (fieldMapping.getSourceField().getName().equals("field1")) {
      ((SimpleObjPrime) destObj).setField1(FIELD_VALUE);
      result = true;
    }
    
    return result;
  }
  
}