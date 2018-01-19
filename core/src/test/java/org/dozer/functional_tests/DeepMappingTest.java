/*
 * Copyright 2005-2017 Dozer Project
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

import org.dozer.Mapper;
import org.dozer.vo.Car;
import org.dozer.vo.InsideTestObject;
import org.dozer.vo.InsideTestObjectPrime;
import org.dozer.vo.MetalThingyIF;
import org.dozer.vo.deep.DestDeepObj;
import org.dozer.vo.deep.HomeDescription;
import org.dozer.vo.deep.House;
import org.dozer.vo.deep.Person;
import org.dozer.vo.deep.SrcDeepObj;
import org.dozer.vo.deep2.Dest;
import org.dozer.vo.deep2.Src;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class DeepMappingTest extends AbstractFunctionalTest {

  @Test
  public void testDeepMapping() throws Exception {
    mapper = getMapper(new String[] {"testDozerBeanMapping.xml"});
    SrcDeepObj src = testDataFactory.getSrcDeepObj();
    DestDeepObj dest = mapper.map(src, DestDeepObj.class);
    SrcDeepObj src2 = mapper.map(dest, SrcDeepObj.class);
    DestDeepObj dest2 = mapper.map(src2, DestDeepObj.class);

    assertEquals(src, src2);
    assertEquals(dest, dest2);
  }

  @Test
  public void testDeepPropertyOneWay() throws Exception {
    mapper = getMapper(new String[] {"testDozerBeanMapping.xml"});
    House house = newInstance(House.class);
    Person owner = newInstance(Person.class);
    owner.setYourName("myName");
    house.setOwner(owner);
    HomeDescription desc = mapper.map(house, HomeDescription.class);
    assertEquals(desc.getDescription().getMyName(), "myName");
    // make sure we don't map back
    House house2 = mapper.map(desc, House.class);
    assertNull(house2.getOwner().getYourName());
  }

  @Test
  public void testDeepInterfaceWithHint() throws Exception {
    Mapper mapper = getMapper(new String[] {"mappings/fieldAttributeMapping.xml"});
    InsideTestObject ito = newInstance(InsideTestObject.class);
    House house = newInstance(House.class);
    MetalThingyIF thingy = newInstance(Car.class);
    thingy.setName("name");
    house.setThingy(thingy);
    ito.setHouse(house);
    InsideTestObjectPrime itop = mapper.map(ito, InsideTestObjectPrime.class);
    assertEquals("name", itop.getDeepInterfaceString());

    mapper.map(itop, InsideTestObject.class);
    assertEquals("name", ito.getHouse().getThingy().getName());
  }

  /*
   * Related to feature request #1456486. Deep mapping with custom getter/setter does not work
   * Also related to #2459076
   */
  @Test
  public void testDeepMapping_UsingCustomGetSetMethods() {
    mapper = super.getMapper(new String[] {"mappings/deepMappingUsingCustomGetSet.xml"});

    Src src = newInstance(Src.class);
    src.setSrcField("srcFieldValue");

    Dest dest = mapper.map(src, Dest.class);

    assertNotNull(dest.getDestField().getNestedDestField().getNestedNestedDestField());
    assertEquals(src.getSrcField(), dest.getDestField().getNestedDestField().getNestedNestedDestField());
    assertTrue("should have been set by customer setter method", dest.getDestField().getNestedDestField().isSetWithCustomMethod());

    Src dest2 = mapper.map(dest, Src.class);

    assertNotNull(dest2.getSrcField());
    assertEquals(dest.getDestField().getNestedDestField().getNestedNestedDestField(), dest2.getSrcField());
  }

}
