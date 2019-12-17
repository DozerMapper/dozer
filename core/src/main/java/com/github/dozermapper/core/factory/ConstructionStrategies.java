/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.github.dozermapper.core.BeanFactory;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.converters.JAXBElementConverter;
import com.github.dozermapper.core.util.DozerClassLoader;
import com.github.dozermapper.core.util.MappingUtils;
import com.github.dozermapper.core.util.ReflectionUtils;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConstructionStrategies {

    private final BeanCreationStrategy byCreateMethod;
    private final BeanCreationStrategy byGetInstance;
    private final BeanCreationStrategy byInterface;
    private final BeanCreationStrategy xmlBeansBased;
    private final BeanCreationStrategy jaxbBeansBased;
    private final BeanCreationStrategy constructorBased;
    private final BeanCreationStrategy noArgObjectConstructor;
    private final ConstructionStrategies.ByFactory byFactory;
    private final BeanCreationStrategy xmlGregorianCalendar;

    public ConstructionStrategies(BeanContainer beanContainer) {
        byCreateMethod = new ByCreateMethod(beanContainer);
        byGetInstance = new ByGetInstance(beanContainer);
        byInterface = new ByInterface();
        xmlBeansBased = new XMLBeansBased(beanContainer);
        jaxbBeansBased = new JAXBBeansBased(beanContainer);
        constructorBased = new ByConstructor();
        noArgObjectConstructor = new ByNoArgObjectConstructor();
        byFactory = new ByFactory(beanContainer);
        xmlGregorianCalendar = new XmlGregorian();
    }

    public BeanCreationStrategy byCreateMethod() {
        return byCreateMethod;
    }

    public BeanCreationStrategy byGetInstance() {
        return byGetInstance;
    }

    public BeanCreationStrategy byInterface() {
        return byInterface;
    }

    public BeanCreationStrategy xmlBeansBased() {
        return xmlBeansBased;
    }

    public BeanCreationStrategy jaxbBeansBased() {
        return jaxbBeansBased;
    }

    public BeanCreationStrategy byConstructor() {
        return constructorBased;
    }

    public BeanCreationStrategy byNoArgObjectConstructor() {
        return noArgObjectConstructor;
    }

    public ByFactory byFactory() {
        return byFactory;
    }

    public BeanCreationStrategy xmlGregorianCalendar() {
        return xmlGregorianCalendar;
    }

    static class ByCreateMethod implements BeanCreationStrategy {

        private final BeanContainer beanContainer;

        ByCreateMethod(BeanContainer beanContainer) {
            this.beanContainer = beanContainer;
        }

        public boolean isApplicable(BeanCreationDirective directive) {
            String createMethod = directive.getCreateMethod();
            return !MappingUtils.isBlankOrNull(createMethod);
        }

        public Object create(BeanCreationDirective directive) {
            Class<?> actualClass = directive.getActualClass();
            String createMethod = directive.getCreateMethod();

            Method method;
            if (createMethod.contains(".")) {
                String methodName = createMethod.substring(createMethod.lastIndexOf(".") + 1, createMethod.length());
                String typeName = createMethod.substring(0, createMethod.lastIndexOf("."));
                DozerClassLoader loader = beanContainer.getClassLoader();
                Class type = loader.loadClass(typeName);
                method = findMethod(type, methodName);
            } else {
                method = findMethod(actualClass, createMethod);
            }
            return ReflectionUtils.invoke(method, null, null);
        }

        private Method findMethod(Class<?> actualClass, String createMethod) {
            Method method = null;
            try {
                method = ReflectionUtils.getMethod(actualClass, createMethod, null);
            } catch (NoSuchMethodException e) {
                MappingUtils.throwMappingException(e);
            }
            return method;
        }

    }

    static class ByGetInstance extends ByCreateMethod {

        ByGetInstance(BeanContainer beanContainer) {
            super(beanContainer);
        }

        // TODO Investigate what else could be here

        @Override
        public boolean isApplicable(BeanCreationDirective directive) {
            Class<?> actualClass = directive.getActualClass();
            return Calendar.class.isAssignableFrom(actualClass)
                   || DateFormat.class.isAssignableFrom(actualClass);
        }

        public Object create(BeanCreationDirective directive) {
            directive.setCreateMethod("getInstance");
            return super.create(directive);
        }
    }

    static class ByFactory implements BeanCreationStrategy {

        private final Logger log = LoggerFactory.getLogger(ByFactory.class);

        private final ConcurrentMap<String, BeanFactory> factoryCache = new ConcurrentHashMap<>();
        private final BeanContainer beanContainer;

        ByFactory(BeanContainer beanContainer) {
            this.beanContainer = beanContainer;
        }

        public boolean isApplicable(BeanCreationDirective directive) {
            String factoryName = directive.getFactoryName();
            return !MappingUtils.isBlankOrNull(factoryName);
        }

        public Object create(BeanCreationDirective directive) {
            Class<?> classToCreate = directive.getActualClass();
            String factoryName = directive.getFactoryName();
            String factoryBeanId = directive.getFactoryId();

            // By default, use dest object class name for factory bean id
            String beanId = !MappingUtils.isBlankOrNull(factoryBeanId) ? factoryBeanId : classToCreate.getName();

            BeanFactory factory = factoryCache.get(factoryName);
            if (factory == null) {
                Class<?> factoryClass = MappingUtils.loadClass(factoryName, beanContainer);
                if (!BeanFactory.class.isAssignableFrom(factoryClass)) {
                    MappingUtils.throwMappingException("Custom bean factory must implement "
                                                       + BeanFactory.class.getName()
                                                       + " interface : " + factoryClass);
                }

                factory = (BeanFactory)ReflectionUtils.newInstance(factoryClass);
                // put the created factory in our factory map
                factoryCache.put(factoryName, factory);
            }

            Object result = factory.createBean(directive.getSrcObject(), directive.getSrcClass(), beanId, beanContainer);

            log.debug("Bean instance created with custom factory -->\n  Bean Type: {}\n  Factory Name: {}",
                      result.getClass().getName(), factoryName);

            if (!classToCreate.isAssignableFrom(result.getClass())) {
                MappingUtils.throwMappingException("Custom bean factory (" + factory.getClass()
                                                   + ") did not return correct type of destination data object. Expected : "
                                                   + classToCreate + ", Actual : " + result.getClass());
            }
            return result;
        }

        public void setStoredFactories(Map<String, BeanFactory> factories) {
            this.factoryCache.putAll(factories);
        }

    }

    static class ByInterface implements BeanCreationStrategy {

        public boolean isApplicable(BeanCreationDirective directive) {
            Class<?> actualClass = directive.getActualClass();
            return Map.class.equals(actualClass) || List.class.equals(actualClass) || Set.class.equals(actualClass);
        }

        public Object create(BeanCreationDirective directive) {
            Class<?> actualClass = directive.getActualClass();
            if (Map.class.equals(actualClass)) {
                return new HashMap();
            } else if (List.class.equals(actualClass)) {
                return new ArrayList();
            } else if (Set.class.equals(actualClass)) {
                return new HashSet();
            }
            throw new IllegalStateException("Type not expected : " + actualClass);
        }

    }

    static class ByNoArgObjectConstructor implements BeanCreationStrategy {
        private final Objenesis objectFactory = new ObjenesisStd();

        @Override
        public boolean isApplicable(BeanCreationDirective directive) {
            return directive.isSkipConstructor();
        }

        @Override
        public Object create(BeanCreationDirective directive) {
            Class<?> classToCreate = directive.getActualClass();
            try {
                return objectFactory.newInstance(classToCreate);
            } catch (Exception e) {
                MappingUtils.throwMappingException(e);
            }
            return null;
        }
    }

    static class XMLBeansBased implements BeanCreationStrategy {

        final BeanFactory xmlBeanFactory;
        boolean xmlBeansAvailable;
        private Class<?> xmlObjectType;
        private final BeanContainer beanContainer;

        XMLBeansBased(BeanContainer beanContainer) {
            this(new XMLBeanFactory(), beanContainer);
        }

        XMLBeansBased(XMLBeanFactory xmlBeanFactory, BeanContainer beanContainer) {
            this.xmlBeanFactory = xmlBeanFactory;
            this.beanContainer = beanContainer;
            try {
                xmlObjectType = Class.forName("org.apache.xmlbeans.XmlObject");
                xmlBeansAvailable = true;
            } catch (ClassNotFoundException e) {
                xmlBeansAvailable = false;
            }
        }

        public boolean isApplicable(BeanCreationDirective directive) {
            if (!xmlBeansAvailable) {
                return false;
            }
            Class<?> actualClass = directive.getActualClass();
            return xmlObjectType.isAssignableFrom(actualClass);
        }

        public Object create(BeanCreationDirective directive) {
            Class<?> classToCreate = directive.getActualClass();
            String factoryBeanId = directive.getFactoryId();
            String beanId = !MappingUtils.isBlankOrNull(factoryBeanId) ? factoryBeanId : classToCreate.getName();
            return xmlBeanFactory.createBean(directive.getSrcObject(), directive.getSrcClass(), beanId, beanContainer);
        }

    }

    static class JAXBBeansBased implements BeanCreationStrategy {

        final BeanFactory jaxbBeanFactory;
        boolean jaxbBeansAvailable;
        private Class<?> jaxbObjectType;
        private final BeanContainer beanContainer;

        JAXBBeansBased(BeanContainer beanContainer) {
            this(new JAXBBeanFactory(), beanContainer);
        }

        JAXBBeansBased(JAXBBeanFactory jaxbBeanFactory, BeanContainer beanContainer) {
            this.jaxbBeanFactory = jaxbBeanFactory;
            this.beanContainer = beanContainer;
            try {
                jaxbObjectType = Class.forName("javax.xml.bind.JAXBElement");
                jaxbBeansAvailable = true;
            } catch (ClassNotFoundException e) {
                jaxbBeansAvailable = false;
            }
        }

        public boolean isApplicable(BeanCreationDirective directive) {
            if (!jaxbBeansAvailable) {
                return false;
            }
            Class<?> actualClass = directive.getActualClass();
            return jaxbObjectType.isAssignableFrom(actualClass);
        }

        public Object create(BeanCreationDirective directive) {
            JAXBElementConverter jaxbElementConverter = new JAXBElementConverter(
                    (directive.getDestObj() != null) ? directive.getDestObj().getClass().getCanonicalName() : directive.getActualClass().getCanonicalName(),
                    directive.getFieldName(),
                    null, beanContainer);
            String beanId = jaxbElementConverter.getBeanId();
            Object destValue = jaxbBeanFactory.createBean(directive.getSrcObject(), directive.getSrcClass(), beanId, beanContainer);
            return jaxbElementConverter.convert(jaxbObjectType, (destValue != null) ? destValue : directive.getSrcObject());
        }
    }

    static class ByConstructor implements BeanCreationStrategy {

        public boolean isApplicable(BeanCreationDirective directive) {
            return true;
        }

        public Object create(BeanCreationDirective directive) {
            Class<?> classToCreate = directive.getActualClass();

            try {
                return newInstance(classToCreate);
            } catch (Exception e) {
                if (directive.getAlternateClass() != null) {
                    return newInstance(directive.getAlternateClass());
                } else {
                    MappingUtils.throwMappingException(e);
                }
            }
            return null;
        }

        private static <T> T newInstance(Class<T> clazz) {
            //Create using public or private no-arg constructor
            Constructor<T> constructor = null;
            try {
                constructor = clazz.getDeclaredConstructor(null);
            } catch (SecurityException e) {
                MappingUtils.throwMappingException(e);
            } catch (NoSuchMethodException e) {
                MappingUtils.throwMappingException(e);
            }

            if (constructor == null) {
                MappingUtils.throwMappingException("Could not create a new instance of the dest object: "
                                                   + clazz
                                                   + ".  Could not find a no-arg constructor for this class.");
            }

            // If private, make it accessible
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }

            T result = null;
            try {
                result = constructor.newInstance(null);
            } catch (IllegalArgumentException e) {
                MappingUtils.throwMappingException(e);
            } catch (InstantiationException e) {
                MappingUtils.throwMappingException(e);
            } catch (IllegalAccessException e) {
                MappingUtils.throwMappingException(e);
            } catch (InvocationTargetException e) {
                MappingUtils.throwMappingException(e);
            }
            return result;
        }

    }

    private static class XmlGregorian implements BeanCreationStrategy {

        // Instantiating a DatatypeFactory with DatatypeFactory.newInstance() is expensive, so this should be cached.
        // Regrettably the spec does not require implementations to be thread safe, so we are defensively using
        // thread local caching here.
        private static final ThreadLocal<DatatypeFactory> datatypeFactoryHolder = ThreadLocal.withInitial(() -> {
            try {
                return DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                throw new MappingException(e);
            }
        });

        public boolean isApplicable(BeanCreationDirective directive) {
            Class<?> actualClass = directive.getActualClass();
            return XMLGregorianCalendar.class.isAssignableFrom(actualClass);
        }

        public Object create(BeanCreationDirective directive) {
            return datatypeFactoryHolder.get().newXMLGregorianCalendar();
        }

    }
}
