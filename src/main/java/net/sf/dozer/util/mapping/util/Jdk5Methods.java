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
package net.sf.dozer.util.mapping.util;

import java.lang.reflect.Method;

import net.sf.dozer.util.mapping.MappingException;

/**
 * Internal class containing single instances of thread safe jdk1.5 specific Method objects
 * that are discovered via reflection.  Use single instances of these objects for 
 * performance reasons.  Since Dozer must support older jdks, these
 * jdk1.5 objects must be instantiated via reflection so that the code base can be
 * built with older jdks such as 1.4.  Only intended for internal use. 
 * 
 */
public class Jdk5Methods {

  private static Jdk5Methods singleton;

  private Method classIsEnumMethod;
  private Method enumNameMethod;
  private Method enumValueOfMethod;
  private Method methodGetGenericParameterTypesMethod;
  private Class parameterizedTypeClass;
  private Method paramaterizedTypeGetActualTypeArgsMethod;

  public static Jdk5Methods getInstance() {
    if (singleton == null) {
      singleton = new Jdk5Methods();
    }
    return singleton;
  }

  private Jdk5Methods() {
    init();
  }

  private synchronized void init() {
    try {
      classIsEnumMethod = Class.class.getMethod("isEnum", null);
      Class enumClass = Class.forName("java.lang.Enum");
      enumNameMethod = enumClass.getMethod("name", null);
      enumValueOfMethod = enumClass.getMethod("valueOf", new Class[] { Class.class, String.class });
      methodGetGenericParameterTypesMethod = Method.class.getMethod("getGenericParameterTypes", null);
      parameterizedTypeClass = Class.forName("java.lang.reflect.ParameterizedType");
      paramaterizedTypeGetActualTypeArgsMethod = parameterizedTypeClass.getMethod("getActualTypeArguments", null);
    } catch (Exception e) {
      throw new MappingException("Unable to load jdk 1.5 classes via relection", e);
    }
  }
  
  public Method getClassIsEnumMethod() {
    return classIsEnumMethod;
  }
  
  public Method getEnumNameMethod() {
    return enumNameMethod;
  }
 
  public Method getEnumValueOfMethod() {
    return enumValueOfMethod;
  }
  
  public Method getMethodGetGenericParameterTypesMethod() {
    return methodGetGenericParameterTypesMethod;
  }
  
  public Method getParamaterizedTypeGetActualTypeArgsMethod() {
    return paramaterizedTypeGetActualTypeArgsMethod;
  }
  
  public Class getParameterizedTypeClass() {
    return parameterizedTypeClass;
  }
}
