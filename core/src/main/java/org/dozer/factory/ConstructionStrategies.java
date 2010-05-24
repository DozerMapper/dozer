package org.dozer.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.dozer.BeanFactory;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Dmitry Buzdin
 */
public final class ConstructionStrategies {

  public static class ByCreateMethod implements BeanCreationStrategy {

    public boolean isApplicable(BeanCreationDirective directive) {
      String createMethod = directive.getCreateMethod();
      return !MappingUtils.isBlankOrNull(createMethod);
    }

    public Object create(BeanCreationDirective directive) {
      Class<?> actualClass = directive.getActualClass();
      String createMethod = directive.getCreateMethod();

      Method method = null;
      try {
        method = ReflectionUtils.getMethod(actualClass, createMethod, null); // empty args method
      } catch (NoSuchMethodException e) {
        MappingUtils.throwMappingException(e);
      }
      return ReflectionUtils.invoke(method, null, null);
    }

  }

  public static class ByGetInstance extends ByCreateMethod {

    @Override
    public boolean isApplicable(BeanCreationDirective directive) {
      Class<?> actualClass = directive.getActualClass();
      return Calendar.class.isAssignableFrom(actualClass); // TODO Investigate what else could be here
    }

    public Object create(BeanCreationDirective directive) {
      directive.setCreateMethod("getInstance");
      return super.create(directive);
    }
  }

  public static class ByFactory implements BeanCreationStrategy {

    private static final Log log = LogFactory.getLog(ByFactory.class);

    private final ConcurrentMap<String, BeanFactory> factoryCache = new ConcurrentHashMap<String, BeanFactory>();

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
        Class<?> factoryClass = MappingUtils.loadClass(factoryName);
        if (!BeanFactory.class.isAssignableFrom(factoryClass)) {
          MappingUtils.throwMappingException("Custom bean factory must implement "
              + BeanFactory.class.getName() + " interface : " + factoryClass);
        }
        factory = (BeanFactory) ReflectionUtils.newInstance(factoryClass);
        // put the created factory in our factory map
        factoryCache.put(factoryName, factory);
      }

      Object result = factory.createBean(directive.getSrcObject(), directive.getSrcClass(), beanId);

      if (log.isDebugEnabled()) {
        log.debug("Bean instance created with custom factory -->" + "\n  Bean Type: " + result.getClass().getName()
            + "\n  Factory Name: " + factoryName);
      }

      if (!classToCreate.isAssignableFrom(result.getClass())) {
        MappingUtils.throwMappingException("Custom bean factory (" + factory.getClass() +
            ") did not return correct type of destination data object. Expected : "
            + classToCreate + ", Actual : " + result.getClass());
      }
      return result;
    }

    public void setStoredFactories(Map<String, BeanFactory> factories) {
      this.factoryCache.putAll(factories);
    }

  }


  public static class ByInterface implements BeanCreationStrategy {

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

  public static class XMLBeansBased implements BeanCreationStrategy {

    BeanFactory xmlBeanFactory = new XMLBeanFactory();

    public boolean isApplicable(BeanCreationDirective directive) {
      Class<?> actualClass = directive.getActualClass();
      return XmlObject.class.isAssignableFrom(actualClass);
    }

    public Object create(BeanCreationDirective directive) {
      Class<?> classToCreate = directive.getActualClass();
      String factoryBeanId = directive.getFactoryId();
      String beanId = !MappingUtils.isBlankOrNull(factoryBeanId) ? factoryBeanId : classToCreate.getName();
      return xmlBeanFactory.createBean(directive.getSrcObject(), directive.getSrcClass(), beanId);
    }


  }

  public static class ByConstructor implements BeanCreationStrategy {

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
        MappingUtils.throwMappingException("Could not create a new instance of the dest object: " + clazz
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


}
