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
package net.sf.dozer.functional_tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sf.dozer.Mapper;
import net.sf.dozer.vo.Car;
import net.sf.dozer.vo.InsideTestObject;
import net.sf.dozer.vo.InsideTestObjectPrime;
import net.sf.dozer.vo.MetalThingyIF;
import net.sf.dozer.vo.deep.DestDeepObj;
import net.sf.dozer.vo.deep.HomeDescription;
import net.sf.dozer.vo.deep.House;
import net.sf.dozer.vo.deep.Person;
import net.sf.dozer.vo.deep.SrcDeepObj;
import net.sf.dozer.vo.deep2.Dest;
import net.sf.dozer.vo.deep2.Src;

import org.junit.Test;

/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class DeepMappingTest extends AbstractFunctionalTest {

  @Test
  public void testDeepMapping() throws Exception {
    mapper = getMapper(new String[] { "dozerBeanMapping.xml" });
    SrcDeepObj src = testDataFactory.getSrcDeepObj();
    DestDeepObj dest = mapper.map(src, DestDeepObj.class);
    SrcDeepObj src2 = mapper.map(dest, SrcDeepObj.class);
    DestDeepObj dest2 = mapper.map(src2, DestDeepObj.class);

    assertEquals(src, src2);
    assertEquals(dest, dest2);
  }

  @Test
  public void testDeepPropertyOneWay() throws Exception {
    mapper = getMapper(new String[] { "dozerBeanMapping.xml" });
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
    Mapper mapper = getMapper(new String[] { "fieldAttributeMapping.xml" });
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
    mapper = super.getMapper(new String[] { "deepMappingUsingCustomGetSet.xml" });

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

  @Override
  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}