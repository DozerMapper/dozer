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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import net.sf.dozer.util.mapping.BeanFactoryIF;
import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.exception.DozerRuntimeException;
import net.sf.dozer.util.mapping.fieldmap.ClassMap;
import net.sf.dozer.util.mapping.fieldmap.DozerClass;
import net.sf.dozer.util.mapping.fieldmap.FieldMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class DestBeanCreator {
	private static final Log log = LogFactory.getLog(DestBeanCreator.class);

	private final Map factories;

	private final MappingUtils mappingUtils = new MappingUtils();

	public DestBeanCreator(Map factories) {
		this.factories = factories;
	}

	public Object create(Object srcObject, ClassMap classMap, Class destClass)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException {
		return create(srcObject, classMap, null, destClass);
	}

	public Object create(Object srcObject, ClassMap classMap,
			FieldMap fieldMap, Class destClass) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			NoSuchMethodException, InvocationTargetException {
		Object rvalue = null;
		DozerClass destClassObj = classMap.getDestClass();
		String factoryName = destClassObj.getBeanFactory();
		// see if there are any static createMethods
		if (fieldMap != null) {
			if (fieldMap.getDestField().getCreateMethod() != null) {
				return destClassObj.getClassToMap().getMethod(
						fieldMap.getDestField().getCreateMethod(), null)
						.invoke(null, null);
			}
		}
		if (classMap.getDestClass().getCreateMethod() != null) {
			return destClassObj.getClassToMap().getMethod(
					classMap.getDestClass().getCreateMethod(), null).invoke(
					null, null);
		}
		// If factory name wasn't specified, just create new instance. Otherwise
		// use
		// the specified custom bean factory
		if (mappingUtils.isBlankOrNull(factoryName)) {
			try {
				rvalue = createNewInstance(destClassObj.getClassToMap());
			} catch (InstantiationException e) {
				if (destClass != null) {
					return createNewInstance(destClass);
				}
				// we could be dealing with an Interface or Abstract Class which
				// was
				// mapped using a Class Level mapId
				// try to see if the parentFieldMap dest field has a hint...
				if (fieldMap != null
						&& fieldMap.getDestinationTypeHint() != null) {
					return createNewInstance(fieldMap.getDestinationTypeHint().getHint());
				} else {
					throw e;
				}
			}
		} else {
			rvalue = createFromFactory(srcObject, classMap.getSourceClass()
					.getClassToMap(), factoryName, destClassObj
					.getFactoryBeanId(), destClassObj.getClassToMap());
			// verify factory returned expected dest object type
			if (!classMap.getDestClass().getClassToMap().isAssignableFrom(
					rvalue.getClass())) {
				throw new MappingException(
						"Custom bean factory did not return correct type of destination data object.  Expected: "
								+ classMap.getDestClass().getClassToMap()
								+ ", Actual: " + rvalue.getClass());
			}
		}
		return rvalue;
	}

	public Object createFromFactory(Object srcObject, Class srcObjectClass,
			String factoryName, String factoryBeanId, Class destClass)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, NoSuchMethodException, InvocationTargetException {

		// By default, use dest object class name for factory bean id
		String beanId = !mappingUtils.isBlankOrNull(factoryBeanId) ? factoryBeanId
				: destClass.getName();

		BeanFactoryIF factory = (BeanFactoryIF) factories.get(factoryName);

		if (factory == null) {
			Class factoryClass = Class.forName(factoryName);
			if (!BeanFactoryIF.class.isAssignableFrom(factoryClass)) {
				throw new MappingException(
						"Custom bean factory must implement the BeanFactoryIF interface.");
			}
			factory = (BeanFactoryIF) createNewInstance(factoryClass);
			// put the created factory in our factory map
			factories.put(factoryName, factory);
		}
		Object rvalue = factory.createBean(srcObject, srcObjectClass, beanId);

		log.debug("Bean instance created with custom factory -->"
				+ "\n  Bean Type: " + rvalue.getClass().getName()
				+ "\n  Factory Name: " + factoryName);
		return rvalue;
	}

	private Object createNewInstance(Class clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
		try {
		  return clazz.newInstance();	
		} catch (IllegalAccessException e) {
			//Look for private constructor to use
			Constructor constructor = clazz.getDeclaredConstructor(null);
			if (constructor == null) {
				throw new DozerRuntimeException("Could not create a new instance of the dest object: " + clazz + ".  Could not find a no-arg constructor for this class.");
			}
			
			if (!constructor.isAccessible()) {
				constructor.setAccessible(true);
			}
			return constructor.newInstance(null);
		}
		
	}

}