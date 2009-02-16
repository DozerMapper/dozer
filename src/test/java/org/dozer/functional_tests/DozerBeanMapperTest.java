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
package org.dozer.functional_tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;


import org.dozer.AbstractDozerTest;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.MappingException;
import org.dozer.functional_tests.support.ApplicationBeanFactory;
import org.dozer.functional_tests.support.EventTestListener;
import org.dozer.functional_tests.support.SampleCustomBeanFactory;
import org.dozer.functional_tests.support.SampleCustomBeanFactory2;
import org.dozer.functional_tests.support.SampleDefaultBeanFactory;
import org.dozer.functional_tests.support.TestDataFactory;
import org.dozer.vo.Bus;
import org.dozer.vo.Car;
import org.dozer.vo.MetalThingyIF;
import org.dozer.vo.Moped;
import org.dozer.vo.TestObject;
import org.dozer.vo.TestObjectPrime;
import org.dozer.vo.Van;
import org.dozer.vo.deep.HomeDescription;
import org.dozer.vo.deep.House;
import org.junit.Before;
import org.junit.Test;

/**
 * Very high level tests of the DozerBeanMapper. This test class is not intended to provide in-depth testing of all the
 * possible mapping use cases. The more in-depth unit tests of the DozerBeanMapper and MappingProcessor can be found in
 * other test classes within this same package. i.e) GranularDozerBeanMapperTest, MapperTest, IndexMappingTest, etc
 * 
 * @author tierney.matt
 * @author garsombke.franz
 */
public class DozerBeanMapperTest extends AbstractDozerTest {
  private static Mapper mapper;
  private TestDataFactory testDataFactory = new TestDataFactory(NoProxyDataObjectInstantiator.INSTANCE);

  @Override
  @Before
  public void setUp() throws Exception {
    if (mapper == null) {
      mapper = getNewMapper(new String[] { "dozerBeanMapping.xml" });
    }
  }

  @Test
  public void testNoSourceObject() throws Exception {
    try {
      mapper.map(null, TestObjectPrime.class);
      fail("should have thrown exception");
    } catch (MappingException e) {
      assertEquals("source object must not be null", e.getMessage());
    }
  }

  @Test
  public void testNoDestinationClass() throws Exception {
    try {
      mapper.map(new TestObjectPrime(), null);
      fail("should have thrown exception");
    } catch (MappingException e) {
      assertEquals("destination class must not be null", e.getMessage());
    }
  }

  @Test
  public void testNullDestObj() throws Exception {
    try {
      Object destObj = null;
      mapper.map(new TestObject(), destObj);
      fail("should have thrown mapping exception");
    } catch (MappingException e) {
    }
  }

  @Test
  public void testMapIdDoesNotExist() {
    try {
      mapper.map(new TestObject(), TestObjectPrime.class, "thisMapIdDoesNotExist");
      fail("should have thrown exception");
    } catch (Exception e) {
      // expected
    }
  }

  @Test
  public void testGeneralMapping() throws Exception {
    assertCommon(mapper);
  }

  @Test
  public void testNoMappingFilesSpecified() throws Exception {
    // Mapper can be used without specifying any mapping files. Fields that have the same name will be mapped
    // automatically.
    Mapper mapper = new DozerBeanMapper();

    assertCommon(mapper);
  }

  @Test
  public void testInjectMapperUsingSpring() throws Exception {
    // Try to get mapper from spring. Mapping files are injected via Spring config.
    Mapper mapper = (Mapper) ApplicationBeanFactory.getBean(Mapper.class);
    DozerBeanMapper mapperImpl = (DozerBeanMapper) mapper;

    Mapper cleanMapper = (Mapper) ApplicationBeanFactory.getBean("cleanMapper");

    assertNotNull("mapper should not be null", mapper);
    assertNotNull("mapping file names should not be null", mapperImpl.getMappingFiles());
    assertTrue("mapping file names should not be empty", mapperImpl.getMappingFiles().size() > 0);

    // Do some mapping so that the mapping files are actually loaded
    assertCommon(mapper);

    // make sure that the customconverter was injected
    Car car = new Car();
    Van van = cleanMapper.map(car, Van.class);
    assertEquals("injectedName", van.getName());
    // map back
    Car carDest = cleanMapper.map(van, Car.class);
    assertEquals("injectedName", carDest.getName());

    // test that we get customconverter even though it wasn't defined in the mapping file
    Moped moped = new Moped();
    Bus bus = cleanMapper.map(moped, Bus.class);
    assertEquals("injectedName", bus.getName());

    // map back
    Moped mopedDest = cleanMapper.map(bus, Moped.class);
    assertEquals("injectedName", mopedDest.getName());
  }

  @Test
  public void testSpringNoMappingFilesSpecified() throws Exception {
    // Mapper can be used without specifying any mapping files. Fields that have the same name will be mapped
    // automatically.
    Mapper mapper = (Mapper) ApplicationBeanFactory.getBean("NoExplicitMappingsMapperIF");

    assertCommon(mapper);
  }

  @Test
  public void testDetectDuplicateMapping() throws Exception {
    Mapper myMapper = null;
    try {

      List<String> mappingFiles = new ArrayList<String>();
      mappingFiles.add("duplicateMapping.xml");
      myMapper = new DozerBeanMapper(mappingFiles);

      myMapper.map(new org.dozer.vo.SuperSuperSuperClass(), org.dozer.vo.SuperSuperSuperClassPrime.class);
      fail("should have thrown exception");
    } catch (Exception e) {
      assertTrue("invalid exception", e.getMessage().indexOf("Duplicate Class Mapping Found") != -1);
    }
  }

  @Test
  public void testCustomBeanFactory() throws Exception {
    // -----------------------------------------------------------
    // Test that java beans get created with explicitly specified
    // custom bean factory
    // -----------------------------------------------------------

    Mapper mapper = getNewMapper(new String[] { "customfactorymapping.xml" });

    TestObjectPrime prime = mapper.map(testDataFactory.getInputGeneralMappingTestObject(), TestObjectPrime.class);
    TestObject source = mapper.map(prime, TestObject.class);

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
    MetalThingyIF car = mapper.map(van, MetalThingyIF.class);
    assertEquals("testName", car.getName());
  }

  @Test
  public void testEventListeners() throws Exception {
    DozerBeanMapper eventMapper = (DozerBeanMapper) ApplicationBeanFactory.getBean("EventMapper");
    assertNotNull("event listenter list should not be null", eventMapper.getEventListeners());
    assertEquals("event listenter list should contain 1 element", 1, eventMapper.getEventListeners().size());
    assertEquals("event listenter list should contain 1 element", EventTestListener.class, eventMapper.getEventListeners().get(0)
        .getClass());
    House src = testDataFactory.getHouse();
    eventMapper.map(src, HomeDescription.class);
  }

  private void assertCommon(Mapper mapper) throws Exception {
    TestObjectPrime prime = mapper.map(testDataFactory.getInputGeneralMappingTestObject(), TestObjectPrime.class);
    TestObject source = mapper.map(prime, TestObject.class);
    TestObjectPrime prime2 = mapper.map(source, TestObjectPrime.class);

    assertEquals(prime2, prime);
  }

  private Mapper getNewMapper(String[] mappingFiles) {
    List<String> list = new ArrayList<String>();
    if (mappingFiles != null) {
      for (int i = 0; i < mappingFiles.length; i++) {
        list.add(mappingFiles[i]);
      }
    }
    Mapper mapper = new DozerBeanMapper();
    ((DozerBeanMapper) mapper).setMappingFiles(list);
    return mapper;
  }

}