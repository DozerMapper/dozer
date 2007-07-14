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

import net.sf.dozer.util.mapping.vo.Family;
import net.sf.dozer.util.mapping.vo.HeadOfHouseHold;
import net.sf.dozer.util.mapping.vo.PersonalDetails;
import net.sf.dozer.util.mapping.vo.Pet;
import net.sf.dozer.util.mapping.vo.SimpleObj;
import net.sf.dozer.util.mapping.vo.deep.DestDeepObj;
import net.sf.dozer.util.mapping.vo.deep.SrcDeepObj;
import net.sf.dozer.util.mapping.vo.deep.SrcNestedDeepObj;
import net.sf.dozer.util.mapping.vo.deep.SrcNestedDeepObj2;

/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class DeepMappingWithIndexTest extends AbstractDozerTest {

  protected void setUp() throws Exception {
    super.setUp();
    mapper = getNewMapper(new String[] { "deepMappingWithIndexedFields.xml" });
  }

  public void testDeepMappingWithIndexOnSrcField() {
    SrcDeepObj src = new SrcDeepObj();
    SrcNestedDeepObj srcNestedObj = new SrcNestedDeepObj();
    SrcNestedDeepObj2 srcNestedObj2 = new SrcNestedDeepObj2();
    SimpleObj simpleObj = new SimpleObj();
    simpleObj.setField1("985756");
    srcNestedObj2.setSimpleObjects(new SimpleObj[] { simpleObj, new SimpleObj() });
    srcNestedObj.setSrcNestedObj2(srcNestedObj2);
    src.setSrcNestedObj(srcNestedObj);

    DestDeepObj dest = (DestDeepObj) mapper.map(src, DestDeepObj.class);
    assertEquals(Integer.valueOf("985756"), dest.getDest2());
  }

  public void testDeepMappingWithIndexOnDestField() {
    DestDeepObj src = new DestDeepObj();
    src.setDest2(new Integer(857557));

    SrcDeepObj dest = (SrcDeepObj) mapper.map(src, SrcDeepObj.class);
    assertEquals("857557", dest.getSrcNestedObj().getSrcNestedObj2().getSimpleObjects()[0].getField1());
  }

  public void testDeepMapIndexed() throws Exception {

    Pet[] myPets = new Pet[2];
    Family source = new Family("john", "jane", "doe", new Integer(22000), new Integer(20000));
    Pet firstPet = new Pet("molly", 2, null);
    myPets[0] = firstPet;

    Pet[] offSprings = new Pet[4];
    offSprings[0] = new Pet("Rocky1", 1, null);
    offSprings[1] = new Pet("Rocky2", 1, null);
    offSprings[2] = new Pet("Rocky3", 1, null);
    offSprings[3] = new Pet("Rocky4", 1, null);

    Pet secondPet = new Pet("Rocky", 3, offSprings);
    myPets[1] = secondPet;

    // Save the pet details into the source object
    source.setPets(myPets);

    HeadOfHouseHold dest = (HeadOfHouseHold) mapper.map(source, HeadOfHouseHold.class);
    assertEquals(((PersonalDetails) source.getFamilyMembers().get(0)).getFirstName(), dest.getFirstName());
    assertEquals(((PersonalDetails) source.getFamilyMembers().get(0)).getLastName(), dest.getLastName());
    assertEquals(((PersonalDetails) source.getFamilyMembers().get(0)).getSalary(), dest.getSalary());
    assertEquals(source.getPets()[1].getPetName(), dest.getPetName());
    assertEquals(String.valueOf(source.getPets()[1].getPetAge()), dest.getPetAge());
    assertEquals(source.getPets()[1].getOffSpring()[2].getPetName(), dest.getOffSpringName());
  }

  public void testDeepMapInvIndexed() throws Exception {

    HeadOfHouseHold source = new HeadOfHouseHold();
    source.setFirstName("Tom");
    source.setLastName("Roy");
    source.setPetName("Ronny");
    source.setSalary(new Integer(15000));
    source.setPetAge("2");
    source.setOffSpringName("Ronny2");

    Family dest = new Family();
    mapper.map(source, dest);

    assertEquals(((PersonalDetails) dest.getFamilyMembers().get(0)).getFirstName(), source.getFirstName());
    assertEquals(((PersonalDetails) dest.getFamilyMembers().get(0)).getLastName(), source.getLastName());
    assertEquals(((PersonalDetails) dest.getFamilyMembers().get(0)).getSalary(), source.getSalary());
    assertEquals(dest.getPets()[1].getPetName(), source.getPetName());
    assertEquals(String.valueOf(dest.getPets()[1].getPetAge()), source.getPetAge());
    assertEquals(dest.getPets()[1].getOffSpring()[2].getPetName(), source.getOffSpringName());
  }

}