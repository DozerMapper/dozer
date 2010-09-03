package org.dozer.factory;

import org.dozer.BeanFactory;
import org.dozer.MappingException;
import org.dozer.config.BeanContainer;
import org.dozer.util.DozerClassLoader;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
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

/**
 * @author Dmitry Buzdin
 */
public final class ConstructionStrategies {

  private static final BeanCreationStrategy byCreateMethod = new ConstructionStrategies.ByCreateMethod();
  private static final BeanCreationStrategy byGetInstance = new ConstructionStrategies.ByGetInstance();
  private static final BeanCreationStrategy byInterface = new ConstructionStrategies.ByInterface();
  private static final BeanCreationStrategy xmlBeansBased = new ConstructionStrategies.XMLBeansBased();
  private static final BeanCreationStrategy constructorBased = new ConstructionStrategies.ByConstructor();
  private static final ConstructionStrategies.ByFactory byFactory = new ConstructionStrategies.ByFactory();
  private static final BeanCreationStrategy xmlGregorianCalendar = new ConstructionStrategies.XmlGregorian();

  public static BeanCreationStrategy byCreateMethod() {
    return byCreateMethod;
  }

  public static BeanCreationStrategy byGetInstance() {
    return byGetInstance;
  }

  public static BeanCreationStrategy byInterface() {
    return byInterface;
  }

  public static BeanCreationStrategy xmlBeansBased() {
    return xmlBeansBased;
  }

  public static BeanCreationStrategy byConstructor() {
    return constructorBased;
  }

  public static ByFactory byFactory() {
    return byFactory;
  }

  public static BeanCreationStrategy xmlGregorianCalendar() {
    return xmlGregorianCalendar;
  }

  static class ByCreateMethod implements BeanCreationStrategy {

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
        DozerClassLoader loader = BeanContainer.getInstance().getClassLoader();
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

    private static final Logger log = LoggerFactory.getLogger(ByFactory.class);

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

      log.debug("Bean instance created with custom factory -->\n  Bean Type: {}\n  Factory Name: {}",
              result.getClass().getName(), factoryName);

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

  static class XMLBeansBased implements BeanCreationStrategy {

    final BeanFactory xmlBeanFactory;
    boolean xmlBeansAvailable;
    private Class<?> xmlObjectType;

    XMLBeansBased() {
      this(new XMLBeanFactory());
    }

    XMLBeansBased(XMLBeanFactory xmlBeanFactory) {
      this.xmlBeanFactory = xmlBeanFactory;
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
      return xmlBeanFactory.createBean(directive.getSrcObject(), directive.getSrcClass(), beanId);
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

  private static class XmlGregorian implements BeanCreationStrategy {

    public boolean isApplicable(BeanCreationDirective directive) {
      Class<?> actualClass = directive.getActualClass();
      return XMLGregorianCalendar.class.isAssignableFrom(actualClass);
    }

    public Object create(BeanCreationDirective directive) {
      DatatypeFactory dataTypeFactory;
      try {
        dataTypeFactory = DatatypeFactory.newInstance();
      } catch (DatatypeConfigurationException e) {
        throw new MappingException(e);
      }
      return dataTypeFactory.newXMLGregorianCalendar();
    }

  }

}
