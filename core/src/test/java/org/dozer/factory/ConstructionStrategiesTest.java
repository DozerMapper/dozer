package org.dozer.factory;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.UniqueDocumentImpl;
import org.dozer.AbstractDozerTest;
import org.dozer.BeanFactory;
import org.dozer.MappingException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Dmitry Buzdin
 */
public class ConstructionStrategiesTest extends AbstractDozerTest {

  private BeanCreationDirective directive;

  private ConstructionStrategies.ByCreateMethod byCreateMethod;
  private ConstructionStrategies.ByGetInstance byGetInstance;
  private ConstructionStrategies.ByFactory byFactory;
  private ConstructionStrategies.ByInterface byInterface;
  private ConstructionStrategies.ByConstructor byConstructor;
  private ConstructionStrategies.XMLBeansBased xmlBeansBased;
  private XMLBeanFactory xmlBeanFactory;

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    xmlBeanFactory = mock(XMLBeanFactory.class);

    byCreateMethod = new ConstructionStrategies.ByCreateMethod();
    byGetInstance = new ConstructionStrategies.ByGetInstance();
    byFactory = new ConstructionStrategies.ByFactory();
    byInterface = new ConstructionStrategies.ByInterface();
    byConstructor = new ConstructionStrategies.ByConstructor();
    xmlBeansBased = new ConstructionStrategies.XMLBeansBased(xmlBeanFactory);

    directive = new BeanCreationDirective();
  }

  @Test
  public void shouldAcceptOnlyWhenCreateMethodDefined() {
    directive.setCreateMethod("a");
    assertTrue(byCreateMethod.isApplicable(directive));

    directive.setCreateMethod("");
    assertFalse(byCreateMethod.isApplicable(directive));

    directive.setCreateMethod(null);
    assertFalse(byCreateMethod.isApplicable(directive));
  }

  @Test
  public void shouldUseStaticCreateMethod() {
    directive.setTargetClass(SelfFactory.class);
    directive.setCreateMethod("create");
    SelfFactory result = (SelfFactory) byCreateMethod.create(directive);

    assertNotNull(result);
    assertEquals("a", result.getName());
  }

  @Test
  public void shouldUseFullyQualifiedStaticCreateMethod() {
    directive.setTargetClass(String.class);
    directive.setCreateMethod("org.dozer.factory.ConstructionStrategiesTest$ExternalFactory.create");
    String result = (String) byCreateMethod.create(directive);
    assertEquals("hello", result);
  }

  @Test(expected = MappingException.class)
  public void shouldFailWithNoSuchMethod() {
    directive.setTargetClass(SelfFactory.class);
    directive.setCreateMethod("wrong");
    byCreateMethod.create(directive);
    fail();
  }

  @Test
  public void shouldAcceptCalendar() {
    directive.setTargetClass(Calendar.class);
    assertTrue(byGetInstance.isApplicable(directive));

    directive.setTargetClass(String.class);
    assertFalse(byGetInstance.isApplicable(directive));
  }

  @Test
  public void shouldCreateByGetInstance() {
    directive.setTargetClass(Calendar.class);
    Calendar result = (Calendar) byGetInstance.create(directive);
    assertNotNull(result);
  }

  @Test
  public void shouldAcceptWhenFactoryDefined() {
    directive.setFactoryName(MyBeanFactory.class.getName());
    assertTrue(byFactory.isApplicable(directive));

    directive.setFactoryName("");
    assertFalse(byFactory.isApplicable(directive));
  }


  @Test
  public void shouldTakeFactoryFromCache() {
    HashMap<String, BeanFactory> factories = new HashMap<String, BeanFactory>();
    factories.put(MyBeanFactory.class.getName(), new MyBeanFactory());
    byFactory.setStoredFactories(factories);
    directive.setFactoryName(MyBeanFactory.class.getName());
    directive.setTargetClass(String.class);
    assertEquals("", byFactory.create(directive));
  }

  @Test
  public void shouldInstantiateFactory() {
    directive.setFactoryName(MyBeanFactory.class.getName());
    directive.setTargetClass(String.class);
    assertEquals("", byFactory.create(directive));
  }

  @Test(expected = MappingException.class)
  public void shouldCheckType() {
    directive.setFactoryName(String.class.getName());
    directive.setTargetClass(String.class);
    byFactory.create(directive);
    fail();
  }

  @Test
  public void shouldAcceptInterfaces() {
    directive.setTargetClass(String.class);
    assertFalse(byInterface.isApplicable(directive));
    directive.setTargetClass(List.class);
    assertTrue(byInterface.isApplicable(directive));
    directive.setTargetClass(Map.class);
    assertTrue(byInterface.isApplicable(directive));
    directive.setTargetClass(Set.class);
    assertTrue(byInterface.isApplicable(directive));
  }

  @Test
  public void shouldCreateDefaultImpl() {
    directive.setTargetClass(List.class);
    assertTrue(byInterface.create(directive) instanceof ArrayList);

    directive.setTargetClass(Map.class);
    assertTrue(byInterface.create(directive) instanceof HashMap);

    directive.setTargetClass(Set.class);
    assertTrue(byInterface.create(directive) instanceof HashSet);    
  }

  @Test
  public void shouldCreateByConstructor() {
    directive.setTargetClass(String.class);
    assertEquals("", byConstructor.create(directive));
  }

  @Test(expected = MappingException.class)
  public void shouldFailToFindConstructor() {
    directive.setTargetClass(SelfFactory.class);
    byConstructor.create(directive);
  }
  
  @Test(expected = MappingException.class)
  public void shouldFailToReturnCorrectType() {
    directive.setFactoryName(MyBeanFactory.class.getName());
    directive.setTargetClass(SelfFactory.class);
    byFactory.create(directive);
  }

  @Test
  public void shouldInstantiateByXmlBeansFactory() {
    directive.setSrcObject("");
    directive.setSrcClass(String.class);
    directive.setFactoryId("id");
    directive.setTargetClass(UniqueDocumentImpl.class);

    xmlBeansBased.create(directive);

    verify(xmlBeanFactory, times(1)).createBean("", String.class, "id");
  }

  public static class SelfFactory {
    private String name;

    private SelfFactory(String name) {
      this.name = name;
    }
    public static SelfFactory create() {
      return new SelfFactory("a");
    }

    public String getName() {
      return name;
    }
  }

  public static class MyBeanFactory implements BeanFactory {

    public Object createBean(Object source, Class<?> sourceClass, String targetBeanId) {
      return "";
    }

  }

  public static class ExternalFactory {
    public static String create() {
      return "hello";
    }
  }

}

