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
package net.sf.dozer.util.mapping;

import java.util.ArrayList;
import java.util.List;

import net.sf.dozer.util.mapping.event.EventTestListener;
import net.sf.dozer.util.mapping.factories.SampleCustomBeanFactory;
import net.sf.dozer.util.mapping.factories.SampleCustomBeanFactory2;
import net.sf.dozer.util.mapping.factories.SampleDefaultBeanFactory;
import net.sf.dozer.util.mapping.util.ApplicationBeanFactory;
import net.sf.dozer.util.mapping.util.TestDataFactory;
import net.sf.dozer.util.mapping.vo.Bus;
import net.sf.dozer.util.mapping.vo.Car;
import net.sf.dozer.util.mapping.vo.MetalThingyIF;
import net.sf.dozer.util.mapping.vo.Moped;
import net.sf.dozer.util.mapping.vo.TestObject;
import net.sf.dozer.util.mapping.vo.TestObjectPrime;
import net.sf.dozer.util.mapping.vo.Van;
import net.sf.dozer.util.mapping.vo.deep.HomeDescription;
import net.sf.dozer.util.mapping.vo.deep.House;
import net.sf.dozer.util.mapping.vo.km.SomeVo;
import net.sf.dozer.util.mapping.vo.km.Sub;
import net.sf.dozer.util.mapping.vo.km.Super;

/**
 * Very high level tests of the DozerBeanMapper.  This test class is not intended to provide in-depth testing of all the 
 * possible mapping use cases.  The more in-depth unit tests of the DozerBeanMapper and MappingProcessor can be found
 * in other test classes within this same package.  i.e) GranularDozerBeanMapperTest, MapperTest, IndexMappingTest, etc
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public class DozerBeanMapperTest extends AbstractDozerTest {
  private static MapperIF mapper;

  protected void setUp() throws Exception {
    super.setUp();
    if (mapper == null) {
      mapper = getNewMapper(new String[] { "dozerBeanMapping.xml" });
    }
  }

  public void testNoSourceObject() throws Exception {
    try {
      mapper.map(null, TestObjectPrime.class);
      fail("should have thrown exception");
    } catch (MappingException e) {
      assertEquals("source object must not be null", e.getMessage());
    }
  }

  public void testNoDestinationClass() throws Exception {
    try {
      mapper.map(new TestObjectPrime(), null);
      fail("should have thrown exception");
    } catch (MappingException e) {
      assertEquals("destination class must not be null", e.getMessage());
    }
  }
  
  public void testMapIdDoesNotExist() {
    try {
      mapper.map(new TestObject(), TestObjectPrime.class, "thisMapIdDoesNotExist");
      fail("should have thrown exception");
    } catch (Exception e) {
      //expected
    }
  }
  
  public void testGeneralMapping() throws Exception {
    assertCommon(mapper);
  }

  public void testNoMappingFilesSpecified() throws Exception {
    // Mapper can be used without specifying any mapping files. Fields that have the same name will be mapped
    // automatically.
    MapperIF mapper = new DozerBeanMapper();

    assertCommon(mapper);
  }

  public void testInjectMapperUsingSpring() throws Exception {
    // Try to get mapper from spring. Mapping files are injected via Spring config.
    MapperIF mapper = (MapperIF) ApplicationBeanFactory.getBean(MapperIF.class);
    DozerBeanMapper mapperImpl = (DozerBeanMapper) mapper;

    MapperIF cleanMapper = (MapperIF) ApplicationBeanFactory.getBean("cleanMapper");

    assertNotNull("mapper should not be null", mapper);
    assertNotNull("mapping file names should not be null", mapperImpl.getMappingFiles());
    assertTrue("mapping file names should not be empty", mapperImpl.getMappingFiles().size() > 0);

    // Do some mapping so that the mapping files are actually loaded
    assertCommon(mapper);

    // make sure that the customconverter was injected
    Car car = new Car();
    Van van = (Van) cleanMapper.map(car, Van.class);
    assertEquals("injectedName", van.getName());
    // map back
    Car carDest = (Car) cleanMapper.map(van, Car.class);
    assertEquals("injectedName", carDest.getName());

    // test that we get customconverter even though it wasn't defined in the mapping file
    Moped moped = new Moped();
    Bus bus = (Bus) cleanMapper.map(moped, Bus.class);
    assertEquals("injectedName", bus.getName());

    // map back
    Moped mopedDest = (Moped) cleanMapper.map(bus, Moped.class);
    assertEquals("injectedName", mopedDest.getName());
  }

  public void testSpringNoMappingFilesSpecified() throws Exception {
    // Mapper can be used without specifying any mapping files. Fields that have the same name will be mapped
    // automatically.
    MapperIF mapper = (MapperIF) ApplicationBeanFactory.getBean("NoExplicitMappingsMapperIF");

    assertCommon(mapper);
  }
  
  public void testDetectDuplicateMapping() throws Exception {
    MapperIF myMapper = null;
    try {

      List mappingFiles = new ArrayList();
      mappingFiles.add("duplicateMapping.xml");
      myMapper = new DozerBeanMapper(mappingFiles);

      myMapper.map(new net.sf.dozer.util.mapping.vo.SuperSuperSuperClass(),
          net.sf.dozer.util.mapping.vo.SuperSuperSuperClassPrime.class);
      fail("should have thrown exception");
    } catch (Exception e) {
      assertTrue("invalid exception", e.getMessage().indexOf("Duplicate Class Mapping Found") != -1);
    }
  }
  
  public void testCustomBeanFactory() throws Exception {
    // -----------------------------------------------------------
    // Test that java beans get created with explicitly specified
    // custom bean factory
    // -----------------------------------------------------------

    MapperIF mapper = getNewMapper(new String[] {"customfactorymapping.xml"});

    TestObjectPrime prime = (TestObjectPrime) mapper.map(TestDataFactory.getInputGeneralMappingTestObject(),
        TestObjectPrime.class);
    TestObject source = (TestObject) mapper.map(prime, TestObject.class);

    // The following asserts will verify that the ClassMap beanFactory attr gets applied to both classes
    assertNotNull("created by factory name should not be null", prime.getCreatedByFactoryName());
    assertNotNull("created by factory name should not be null", source.getCreatedByFactoryName());
    assertEquals(SampleCustomBeanFactory.class.getName(), prime.getCreatedByFactoryName());
    assertEquals(SampleCustomBeanFactory.class.getName(), source.getCreatedByFactoryName());

    // The following asserts will verify that default configuration is being applied
    assertNotNull("created by factory name should not be null", source.getThree().getCreatedByFactoryName());
    assertEquals(SampleDefaultBeanFactory.class.getName(), source.getThree().getCreatedByFactoryName());

    // The following asserts will verify that dest or src class level attr's override classMap and default config attr's
    assertNotNull("created by factory name should not be null", prime.getThreePrime().getCreatedByFactoryName());
    assertEquals(SampleCustomBeanFactory2.class.getName(), prime.getThreePrime().getCreatedByFactoryName());

    // test returning an Interface
    Van van = new Van();
    van.setName("testName");
    MetalThingyIF car = (MetalThingyIF) mapper.map(van, MetalThingyIF.class);
    assertEquals("testName", car.getName());
  }

  public void testEventListeners() throws Exception {
    DozerBeanMapper eventMapper = (DozerBeanMapper) ApplicationBeanFactory.getBean("EventMapper");
    assertNotNull("event listenter list should not be null", eventMapper.getEventListeners());
    assertEquals("event listenter list should contain 1 element", 1, eventMapper.getEventListeners().size());
    assertEquals("event listenter list should contain 1 element", EventTestListener.class, eventMapper.getEventListeners().get(0).getClass());
    House src = TestDataFactory.getHouse();
    HomeDescription dest = (HomeDescription) eventMapper.map(src, HomeDescription.class);
  }
  
  private void assertCommon(MapperIF mapper) throws Exception {
    TestObjectPrime prime = (TestObjectPrime) mapper.map(TestDataFactory.getInputGeneralMappingTestObject(),
        TestObjectPrime.class);
    TestObject source = (TestObject) mapper.map(prime, TestObject.class);
    TestObjectPrime prime2 = (TestObjectPrime) mapper.map(source, TestObjectPrime.class);

    assertEquals(prime2, prime);
  }
}