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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dozer.functional_tests.runner.Proxied;
import org.dozer.vo.A;
import org.dozer.vo.Aliases;
import org.dozer.vo.B;
import org.dozer.vo.C;
import org.dozer.vo.D;
import org.dozer.vo.FieldValue;
import org.dozer.vo.FlatIndividual;
import org.dozer.vo.Individual;
import org.dozer.vo.Individuals;
import org.dozer.vo.index.Mccoy;
import org.dozer.vo.index.MccoyPrime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author wojtek.kiersztyn
 * @author dominic.peciuch
 * 
 */
@RunWith(Proxied.class)
public class IndexMappingTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() {
    mapper = getMapper(new String[] {"mappings/IndividualMapping.xml"});
  }

  @Test
  public void testMap1() throws Exception {
    List<String> userNames = newInstance(ArrayList.class);
    userNames.add("username1");
    userNames.add("username2");

    String[] secondNames = new String[3];
    secondNames[0] = "secondName1";
    secondNames[1] = "secondName2";
    secondNames[2] = "secondName3";

    Individuals source = newInstance(Individuals.class);
    source.setUsernames(userNames);
    source.setSimpleField("a very simple field");
    source.setSecondNames(secondNames);

    Set<String> mySet = newInstance(HashSet.class);
    mySet.add("myString");

    source.setAddressSet(mySet);

    FlatIndividual dest = mapper.map(source, FlatIndividual.class);

    assertEquals(source.getUsernames().get(0), dest.getUsername1());
    assertEquals(source.getSimpleField(), dest.getSimpleField());
    assertEquals(source.getSecondNames()[1], dest.getSecondName1());
    assertEquals(source.getSecondNames()[2], dest.getSecondName2());
    assertEquals("myString", dest.getAddress());
  }

  @Test
  public void testMap1Inv() {
    FlatIndividual source = newInstance(FlatIndividual.class);
    source.setUsername1("username1");
    source.setUsername2("username2");
    source.setSimpleField("a simple field");
    source.setSecondName1("secondName1");
    source.setSecondName2("secondName2");
    source.setPrimaryAlias("aqqq");
    source.setThirdName("thirdName");

    Individuals dest = mapper.map(source, Individuals.class);

    assertEquals(source.getUsername1(), dest.getUsernames().get(0));
    assertEquals(dest.getIndividual().getUsername(), source.getUsername2());
    assertEquals(dest.getAliases().getOtherAliases()[0], "aqqq");
    assertEquals(source.getUsername2(), dest.getUsernames().get(1));
    assertEquals(source.getSimpleField(), dest.getSimpleField());
    assertEquals(source.getSecondName1(), dest.getSecondNames()[1]);
    assertEquals(source.getSecondName2(), dest.getSecondNames()[2]);
    assertEquals(source.getThirdName(), dest.getThirdNameElement1());
  }

  @Test
  public void testMap3() {
    List<String> userNames = newInstance(ArrayList.class);
    userNames.add("username1");
    userNames.add("username2");

    Individual nestedIndividual = newInstance(Individual.class);
    nestedIndividual.setUsername("nestedusername");

    String[] secondNames = new String[3];
    secondNames[0] = "secondName1";
    secondNames[1] = "secondName2";
    secondNames[2] = "secondName3";

    Individuals source = newInstance(Individuals.class);
    source.setUsernames(userNames);
    source.setIndividual(nestedIndividual);
    source.setSecondNames(secondNames);

    FlatIndividual dest = mapper.map(source, FlatIndividual.class);

    assertEquals(source.getUsernames().get(0), dest.getUsername1());
    assertEquals(source.getIndividual().getUsername(), dest.getUsername2());
    assertEquals(source.getSecondNames()[1], dest.getSecondName1());
    assertEquals(source.getSecondNames()[2], dest.getSecondName2());
  }

  @Test
  public void testNulls() {
    FlatIndividual source = newInstance(FlatIndividual.class);
    source.setSimpleField("a simplefield");

    Individuals dest = mapper.map(source, Individuals.class);
    assertEquals(source.getSimpleField(), dest.getSimpleField());
  }

  @Test
  public void testNullsInv() {
    Individuals source = newInstance(Individuals.class);
    source.setSimpleField("a simplefield");

    FlatIndividual dest = mapper.map(source, FlatIndividual.class);
    assertEquals(source.getSimpleField(), dest.getSimpleField());
  }

  @Test
  public void testNestedArray() {
    Individuals source = newInstance(Individuals.class);
    Aliases aliases = newInstance(Aliases.class);
    aliases.setOtherAliases(new String[] { "other alias 1", "other alias 2" });
    source.setAliases(aliases);

    FlatIndividual dest = mapper.map(source, FlatIndividual.class);
    assertEquals("other alias 1", dest.getPrimaryAlias());
  }

  @Test
  public void testNotNullNestedIndexAtoD() {
    C c = newInstance(C.class);
    c.setValue("value1");
    B b = newInstance(B.class);
    b.setCs(new C[] { c });
    A a = newInstance(A.class);
    a.setB(b);

    D d = mapper.map(a, D.class);
    assertEquals("value not translated", "value1", d.getValue());
  }

  @Test
  public void testNullNestedIndexAtoD() {
    A a = newInstance(A.class);

    D d = mapper.map(a, D.class);
    assertNull("value should not be translated", d.getValue());
  }

  @Test
  public void testNotNullNestedIndexDtoA() {
    D d = newInstance(D.class);
    d.setValue("value1");

    A a = mapper.map(d, A.class);
    assertEquals("value not translated", d.getValue(), a.getB().getCs()[0].getValue());
  }

  @Test
  public void testNullNestedIndexDtoA() {
    D d = newInstance(D.class);
    A a = mapper.map(d, A.class);
    assertNotNull(a);
  }

  @Test
  public void testStringToIndexedSet_UsingMapSetMethod() {
    mapper = getMapper(new String[] {"mappings/indexMapping.xml"});
    Mccoy src = newInstance(Mccoy.class);
    src.setStringProperty(String.valueOf(System.currentTimeMillis()));

    MccoyPrime dest = mapper.map(src, MccoyPrime.class);
    Set<?> destSet = dest.getFieldValueObjects();
    assertNotNull("dest set should not be null", destSet);
    assertEquals("dest set should contain 1 entry", 1, destSet.size());
    Object entry = destSet.iterator().next();
    assertTrue("dest set entry should be instance of FieldValue", entry instanceof FieldValue);
    assertEquals("invalid value for dest object", src.getStringProperty(), ((FieldValue) entry).getValue("stringProperty"));
  }

}
