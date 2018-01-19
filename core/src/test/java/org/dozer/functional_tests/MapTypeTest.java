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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.dozer.Mapper;
import org.dozer.vo.TestObject;
import org.dozer.vo.TestObjectPrime;
import org.dozer.vo.map.ChildDOM;
import org.dozer.vo.map.CustomMap;
import org.dozer.vo.map.CustomMapIF;
import org.dozer.vo.map.GenericDOM;
import org.dozer.vo.map.MapTestObject;
import org.dozer.vo.map.MapTestObjectPrime;
import org.dozer.vo.map.MapToMap;
import org.dozer.vo.map.MapToMapPrime;
import org.dozer.vo.map.MapToProperty;
import org.dozer.vo.map.NestedObj;
import org.dozer.vo.map.NestedObjPrime;
import org.dozer.vo.map.ParentDOM;
import org.dozer.vo.map.PropertyToMap;
import org.dozer.vo.map.SimpleObj;
import org.dozer.vo.map.SimpleObjPrime;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class MapTypeTest extends AbstractFunctionalTest {

  @Test
  public void testMapToVo() throws Exception {
    // Test simple Map --> Vo with custom mappings defined.
    mapper = getMapper(new String[] {"mappings/mapMapping2.xml"});

    NestedObj nestedObj = newInstance(NestedObj.class);
    nestedObj.setField1("nestedfield1value");
    Map<String, Serializable> src = newInstance(HashMap.class);
    src.put("field1", "mapnestedfield1value");
    src.put("nested", nestedObj);

    SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class, "caseA");
    assertEquals(src.get("field1"), result.getField1());
    assertEquals(nestedObj.getField1(), result.getNested().getField1());
  }

  @Test
  public void testMapToVoSimple() throws Exception {
    mapper = getMapper(new String[] { });

    NestedObj nestedObj = newInstance(NestedObj.class);
    nestedObj.setField1("nestedfield1value");
    Map<String, Serializable> src = newInstance(HashMap.class);
    src.put("field1", "mapnestedfield1value");

    SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class);
    assertEquals(src.get("field1"), result.getField1());
  }

  @Test
  public void testMapToVoWithRenameField() throws Exception {
    // Test simple Map --> Vo with custom mappings defined.
    mapper = getMapper(new String[] {"mappings/mapMapping2.xml"});

    NestedObj nestedObj = newInstance(NestedObj.class);
    nestedObj.setField1("nestedfield1value");
    Map<String, Object> src = new HashMap<String, Object>();
    src.put("first", "mapnestedfield1value");
    src.put("nested", nestedObj);

    SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class, "caseC");
    assertEquals(src.get("first"), result.getField1());
    assertEquals(nestedObj.getField1(), result.getNested().getField1());
  }

  @Test
  public void testMapToVoWithRenameFieldReverse() throws Exception {
    // Test simple Map --> Vo with custom mappings defined.
    mapper = getMapper(new String[] {"mappings/mapMapping2.xml"});

    NestedObj nestedObj = newInstance(NestedObj.class);
    nestedObj.setField1("nestedfield1value");
    Map<String, Object> src = new HashMap<String, Object>();
    src.put("first", "mapnestedfield1value");
    src.put("nested", nestedObj);

    SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class, "caseD");
    assertEquals(src.get("first"), result.getField1());
    assertEquals(nestedObj.getField1(), result.getNested().getField1());
  }

  @Test
  public void testMapToVo_CustomMappings() throws Exception {
    // Test simple Map --> Vo with custom mappings defined.
    mapper = getMapper(new String[] {"mappings/mapMapping2.xml"});
    Map<String, String> src = newInstance(HashMap.class);
    src.put("field1", "field1value");
    src.put("field2", "field2value");

    SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class, "caseB");
    assertNull(result.getField1());
    assertEquals(src.get("field2"), result.getField2());
  }

  @Test
  public void testMapToVoUsingMapId() {
    // Simple map --> vo using a map-id
    mapper = super.getMapper(new String[] {"mappings/mapMapping.xml"});
    Map<String, String> src = newInstance(HashMap.class);
    src.put("field1", "field1value");
    src.put("field2", "field2value");

    NestedObjPrime dest = mapper.map(src, NestedObjPrime.class, "caseB");
    assertEquals(src.get("field1"), dest.getField1());
    assertEquals(src.get("field2"), dest.getField2());
  }

  @Test
  public void testMapToVoUsingMapId_FieldExclude() {
    // Simple map --> vo using a map-id
    mapper = super.getMapper(new String[] {"mappings/mapMapping.xml"});
    Map<String, String> src = newInstance(HashMap.class);
    src.put("field1", "field1value");
    src.put("field2", "field2value");

    NestedObjPrime dest = mapper.map(src, NestedObjPrime.class, "caseC");
    assertNull("field was excluded and should be null", dest.getField1());
    assertEquals(src.get("field2"), dest.getField2());
  }

  @Test
  public void testNestedMapToVoUsingMapId() {
    // Another test for nested Map --> Vo using <field map-id=....>
    mapper = super.getMapper("mappings/mapMapping.xml");

    SimpleObj src = newInstance(SimpleObj.class);

    src.setField1("field1");

    NestedObj nested = newInstance(NestedObj.class);
    nested.setField1("nestedfield1");
    src.setNested(nested);

    Map<String, String> nested2 = newInstance(HashMap.class);
    nested2.put("field1", "field1MapValue");
    src.setNested2(nested2);

    SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class, "caseA2");

    assertNull(result.getNested2().getField1());// field was excluded
    assertEquals(src.getField1(), result.getField1());
    assertEquals(src.getNested().getField1(), result.getNested().getField1());
  }

  @Test
  public void testMapToVo_NoCustomMappings() throws Exception {
    // Test simple Map --> Vo without any custom mappings defined.
    NestedObj nestedObj = newInstance(NestedObj.class);
    nestedObj.setField1("nestedfield1value");
    Map<String, Serializable> src = newInstance(HashMap.class);
    src.put("field1", "mapnestedfield1value");
    src.put("nested", nestedObj);

    SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class);
    assertEquals(src.get("field1"), result.getField1());
    assertEquals(nestedObj.getField1(), result.getNested().getField1());
  }

  @Test
  public void testVoToMap_NoCustomMappings() throws Exception {
    // Test simple Vo --> Map without any custom mappings defined.
    SimpleObjPrime src = newInstance(SimpleObjPrime.class);
    src.setField1("someValueField1");
    src.setField2("someValueField2");
    src.setSimpleobjprimefield("someOtherValue");

    NestedObjPrime nested = newInstance(NestedObjPrime.class);
    nested.setField1("field1Value");
    nested.setField2("field2Value");
    src.setNested(nested);

    NestedObjPrime nested2 = newInstance(NestedObjPrime.class);
    src.setNested2(nested2);

    // Map complex object to HashMap
    Map<?, ?> destMap = newInstance(HashMap.class);
    mapper.map(src, destMap);

    // Map HashMap back to new instance of the complex object
    SimpleObjPrime mappedSrc = mapper.map(destMap, SimpleObjPrime.class);

    // Remapped complex type should equal original src if all fields were mapped both ways.
    assertEquals(src, mappedSrc);
  }

  @Test
  public void testMapToMap() throws Exception {
    Mapper mapper = getMapper(new String[] {"mappings/mapInterfaceMapping.xml", "testDozerBeanMapping.xml"});
    TestObject to = newInstance(TestObject.class);
    to.setOne("one");
    TestObject to2 = newInstance(TestObject.class);
    to2.setTwo(new Integer(2));
    Map<String, TestObject> map = newInstance(HashMap.class);
    map.put("to", to);
    map.put("to2", to2);

    Map<String, TestObject> map2 = newInstance(HashMap.class);
    map2.put("to", to);
    map2.put("to2", to2);

    MapToMap mtm = new MapToMap(map, map2);

    MapToMapPrime mtmp = mapper.map(mtm, MapToMapPrime.class);
    assertEquals("one", ((TestObject) mtmp.getStandardMap().get("to")).getOne());
    assertEquals(2, ((TestObject) mtmp.getStandardMap().get("to2")).getTwo().intValue());
    // verify that we transformed from object to object prime
    assertEquals("one", ((TestObjectPrime) mtmp.getStandardMapWithHint().get("to")).getOnePrime());
    assertEquals(2, ((TestObjectPrime) mtmp.getStandardMapWithHint().get("to2")).getTwoPrime().intValue());
  }

  @Test
  public void testMapToMapExistingDestination() throws Exception {
    Mapper mapper = getMapper(new String[] {"mappings/mapInterfaceMapping.xml", "testDozerBeanMapping.xml"});
    TestObject to = newInstance(TestObject.class);
    to.setOne("one");
    TestObject to2 = newInstance(TestObject.class);
    to2.setTwo(new Integer(2));
    Map<String, TestObject> map = newInstance(HashMap.class);
    map.put("to", to);
    map.put("to2", to2);
    MapToMap mtm = newInstance(MapToMap.class);
    mtm.setStandardMap(map);

    // create an existing map and set a value so we can test if it exists after
    // mapping
    MapToMapPrime mtmp = newInstance(MapToMapPrime.class);
    Map<String, Serializable> map2 = newInstance(Hashtable.class);
    map2.put("toDest", to);
    mtmp.setStandardMap(map2);

    mapper.map(mtm, mtmp);
    assertEquals("one", ((TestObject) mtmp.getStandardMap().get("to")).getOne());
    assertEquals(2, ((TestObject) mtmp.getStandardMap().get("to2")).getTwo().intValue());
    assertEquals("one", ((TestObject) mtmp.getStandardMap().get("toDest")).getOne());
  }

  @Test
  public void testPropertyClassLevelMap() throws Exception {
    mapper = getMapper(new String[] {"testDozerBeanMapping.xml"});
    PropertyToMap ptm = newInstance(PropertyToMap.class);
    ptm.setStringProperty("stringPropertyValue");
    ptm.addStringProperty2("stringProperty2Value");
    Map<?, ?> map = mapper.map(ptm, HashMap.class, "myTestMapping");
    assertEquals("stringPropertyValue", map.get("stringProperty"));
    assertEquals("stringProperty2Value", map.get("myStringProperty"));

    CustomMapIF customMap = mapper.map(ptm, CustomMap.class, "myCustomTestMapping");
    assertEquals("stringPropertyValue", customMap.getValue("stringProperty"));
    assertEquals("stringProperty2Value", customMap.getValue("myStringProperty"));

    CustomMapIF custom = newInstance(CustomMap.class);
    custom.putValue("myKey", "myValue");
    mapper.map(ptm, custom, "myCustomTestMapping");
    assertEquals("stringPropertyValue", custom.getValue("stringProperty"));
    assertEquals("myValue", custom.getValue("myKey"));
  }

  @Test
  public void testPropertyClassLevelMap2() throws Exception {
    mapper = getMapper(new String[] {"testDozerBeanMapping.xml"});
    PropertyToMap ptm = newInstance(PropertyToMap.class);
    ptm.setStringProperty("stringPropertyValue");
    ptm.addStringProperty2("stringProperty2Value");

    CustomMapIF customMap = mapper.map(ptm, CustomMap.class, "myCustomTestMapping");
    assertEquals("stringPropertyValue", customMap.getValue("stringProperty"));
    assertEquals("stringProperty2Value", customMap.getValue("myStringProperty"));
  }

  @Test
  public void testPropertyClassLevelMapBack() throws Exception {
    // Map Back
    mapper = getMapper(new String[] {"testDozerBeanMapping.xml"});
    Map<String, Object> map = newInstance(HashMap.class);
    map.put("stringProperty", "stringPropertyValue");
    map.put("integerProperty", new Integer("567"));
    PropertyToMap property = mapper.map(map, PropertyToMap.class, "myTestMapping");
    assertEquals("stringPropertyValue", property.getStringProperty());

    CustomMapIF custom = newInstance(CustomMap.class);
    custom.putValue("stringProperty", "stringPropertyValue");
    PropertyToMap property2 = mapper.map(custom, PropertyToMap.class, "myCustomTestMapping");
    assertEquals("stringPropertyValue", property2.getStringProperty());

    map.put("stringProperty3", "myValue");
    mapper.map(map, property, "myTestMapping");
    assertEquals("myValue", property.getStringProperty3());
  }

  @Test
  public void testPropertyToMap() throws Exception {
    mapper = getMapper(new String[] {"testDozerBeanMapping.xml"});
    PropertyToMap ptm = newInstance(PropertyToMap.class);
    ptm.setStringProperty("stringPropertyValue");
    ptm.addStringProperty2("stringProperty2Value");
    ptm.setStringProperty6("string6Value");
    Map<String, Object> hashMap = newInstance(HashMap.class);
    hashMap.put("reverseMapString", "reverseMapStringValue");
    hashMap.put("reverseMapInteger", new Integer("567"));
    ptm.setReverseMap(hashMap);
    MapToProperty mtp = mapper.map(ptm, MapToProperty.class);
    assertTrue(mtp.getHashMap().containsKey("stringProperty"));
    assertTrue(mtp.getHashMap().containsValue("stringPropertyValue"));
    assertTrue(mtp.getHashMap().containsKey("myStringProperty"));
    assertTrue(mtp.getHashMap().containsValue("stringProperty2Value"));
    assertFalse(mtp.getHashMap().containsValue("nullStringProperty"));
    assertTrue(mtp.getNullHashMap().containsValue("string6Value"));
    assertEquals("reverseMapStringValue", mtp.getReverseMapString());
    assertEquals(((Integer) hashMap.get("reverseMapInteger")).toString(), mtp.getReverseMapInteger());

    // Map Back
    PropertyToMap dest = mapper.map(mtp, PropertyToMap.class);
    assertTrue(dest.getStringProperty().equals("stringPropertyValue"));
    assertTrue(dest.getStringProperty2().equals("stringProperty2Value"));
    assertTrue(dest.getReverseMap().containsKey("reverseMapString"));
    assertTrue(dest.getReverseMap().containsValue("reverseMapStringValue"));
    assertNull(dest.getNullStringProperty());
  }

  @Test
  public void testPropertyToCustomMap() throws Exception {
    mapper = getMapper(new String[] {"testDozerBeanMapping.xml"});
    PropertyToMap ptm = newInstance(PropertyToMap.class);
    ptm.setStringProperty3("stringProperty3Value");
    ptm.setStringProperty4("stringProperty4Value");
    ptm.setStringProperty5("stringProperty5Value");
    MapToProperty mtp = mapper.map(ptm, MapToProperty.class);
    assertEquals("stringProperty3Value", mtp.getCustomMap().getValue("myCustomProperty"));
    assertEquals("stringProperty5Value", mtp.getCustomMap().getValue("stringProperty5"));
    assertEquals("stringProperty4Value", mtp.getNullCustomMap().getValue("myCustomNullProperty"));
    assertEquals("stringProperty5Value", mtp.getCustomMapWithDiffSetMethod().getValue("stringProperty5"));

    // Map Back
    PropertyToMap dest = mapper.map(mtp, PropertyToMap.class);
    assertEquals("stringProperty3Value", dest.getStringProperty3());
    assertEquals("stringProperty4Value", dest.getStringProperty4());
    assertEquals("stringProperty5Value", dest.getStringProperty5());
  }

  @Test
  public void testPropertyToClassLevelMap() throws Exception {
    mapper = getMapper(new String[] {"testDozerBeanMapping.xml"});
    MapTestObject mto = newInstance(MapTestObject.class);
    PropertyToMap ptm = newInstance(PropertyToMap.class);
    Map<String, String> map = newInstance(HashMap.class);
    map.put("reverseClassLevelMapString", "reverseClassLevelMapStringValue");
    mto.setPropertyToMapMapReverse(map);
    ptm.setStringProperty("stringPropertyValue");
    ptm.addStringProperty2("stringProperty2Value");
    ptm.setStringProperty3("stringProperty3Value");
    ptm.setStringProperty4("stringProperty4Value");
    ptm.setStringProperty5("stringProperty5Value");
    mto.setPropertyToMap(ptm);
    PropertyToMap ptm2 = newInstance(PropertyToMap.class);
    ptm2.setStringProperty("stringPropertyValue");
    mto.setPropertyToMapToNullMap(ptm2);
    MapTestObjectPrime mtop = mapper.map(mto, MapTestObjectPrime.class);

    assertTrue(mtop.getPropertyToMapMap().containsKey("stringProperty"));
    assertTrue(mtop.getPropertyToMapMap().containsKey("myStringProperty"));
    assertTrue(mtop.getPropertyToMapMap().containsKey("stringProperty3"));
    assertTrue(mtop.getPropertyToMapMap().containsKey("stringProperty4"));
    assertTrue(mtop.getPropertyToMapMap().containsKey("stringProperty5"));
    assertTrue(mtop.getPropertyToMapMap().containsKey("nullStringProperty"));
    assertTrue(mtop.getPropertyToMapMap().containsValue("stringPropertyValue"));
    assertTrue(mtop.getPropertyToMapMap().containsValue("stringProperty2Value"));
    assertTrue(mtop.getPropertyToMapMap().containsValue("stringProperty3Value"));
    assertTrue(mtop.getPropertyToMapMap().containsValue("stringProperty4Value"));
    assertTrue(mtop.getPropertyToMapMap().containsValue("stringProperty5Value"));
    assertFalse(mtop.getPropertyToMapMap().containsValue("nullStringProperty"));
    assertFalse(mtop.getPropertyToMapMap().containsKey("excludeMe"));
    
    assertEquals("reverseClassLevelMapStringValue", mtop.getPropertyToMapReverse().getReverseClassLevelMapString());
    assertTrue(mtop.getNullPropertyToMapMap().containsKey("stringProperty"));
    assertEquals("stringPropertyValue", mtop.getNullPropertyToMapMap().get("stringProperty"));

    // Map Back
    MapTestObject mto2 = mapper.map(mtop, MapTestObject.class);
    assertEquals("stringPropertyValue", mto2.getPropertyToMap().getStringProperty());
    assertEquals("stringProperty2Value", mto2.getPropertyToMap().getStringProperty2());
    assertEquals("stringProperty3Value", mto2.getPropertyToMap().getStringProperty3());
    assertEquals("stringProperty4Value", mto2.getPropertyToMap().getStringProperty4());
    assertEquals("stringProperty5Value", mto2.getPropertyToMap().getStringProperty5());
    assertTrue(mto2.getPropertyToMapMapReverse().containsKey("reverseClassLevelMapString"));
    assertEquals("reverseClassLevelMapStringValue", mto2.getPropertyToMapMapReverse().get("reverseClassLevelMapString"));

  }

  @Test
  public void testPropertyToCustomClassLevelMap() throws Exception {
    mapper = getMapper(new String[] {"testDozerBeanMapping.xml"});
    MapTestObject mto = newInstance(MapTestObject.class);
    PropertyToMap ptm = newInstance(PropertyToMap.class);
    ptm.setStringProperty("stringPropertyValue");
    ptm.setStringProperty2("stringProperty2Value");
    mto.setPropertyToCustomMap(ptm);
    CustomMapIF customMap = newInstance(CustomMap.class);
    customMap.putValue("stringProperty", "stringPropertyValue");
    mto.setPropertyToCustomMapMapWithInterface(customMap);
    MapTestObjectPrime mtop = mapper.map(mto, MapTestObjectPrime.class);
    assertEquals("stringPropertyValue", mtop.getPropertyToCustomMapMap().getValue("stringProperty"));
    assertNull(mtop.getPropertyToCustomMapMap().getValue("excludeMe"));
    assertEquals("stringProperty2Value", mtop.getPropertyToCustomMapMap().getValue("myStringProperty"));
    assertEquals("stringPropertyValue", mtop.getPropertyToCustomMapWithInterface().getStringProperty());

    // Map Back
    MapTestObject mto2 = mapper.map(mtop, MapTestObject.class);
    assertEquals("stringPropertyValue", mto2.getPropertyToCustomMap().getStringProperty());
    assertEquals("stringProperty2Value", mto2.getPropertyToCustomMap().getStringProperty2());
    assertNull(mto2.getPropertyToCustomMap().getExcludeMe());
    assertEquals("stringPropertyValue", mto2.getPropertyToCustomMapMapWithInterface().getValue("stringProperty"));
  }

  @Test
  public void testMapGetSetMethod_ClassLevel() throws Exception {
    runMapGetSetMethodTest("useCase1");
  }

  @Test
  public void testMapGetSetMethod_FieldLevel() throws Exception {
    runMapGetSetMethodTest("useCase2");
  }

  @Test
  public void testDateFormat_CustomMapType() throws Exception {
    // Test that date format works for mapping between String and Custom Map Type
    mapper = getMapper(new String[] {"mappings/mapMapping3.xml"});
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    String dateStr = "10/15/2005";
    CustomMap src = newInstance(CustomMap.class);
    src.putValue("fieldA", dateStr);

    org.dozer.vo.SimpleObj dest = mapper.map(src, org.dozer.vo.SimpleObj.class);
    assertNotNull("dest field should not be null", dest.getField5());
    assertEquals("dest field contains wrong date value", df.parse(dateStr), dest.getField5().getTime());

    CustomMap remappedSrc = mapper.map(dest, CustomMap.class);
    assertEquals("remapped src field contains wrong date string", dateStr, remappedSrc.getValue("fieldA"));
  }

  private void runMapGetSetMethodTest(String mapId) throws Exception {
    // Test that custom field converter works for Custom Map Types
    mapper = getMapper(new String[] {"mappings/mapGetSetMethodMapping.xml"});
    CustomMap src = newInstance(CustomMap.class);
    src.putValue("fieldA", "someStringValue");
    src.putValue("field2", "someOtherStringValue");
    src.putValue("fieldC", "1");
    src.putValue("fieldD", "2");
    src.putValue("fieldE", "10-15-2005");

    SimpleObj dest = mapper.map(src, SimpleObj.class, mapId);
    assertEquals("wrong value for field1", src.getValue("fieldA"), dest.getField1());
    assertEquals("wrong value for field2", src.getValue("field2"), dest.getField2());
    assertEquals("wrong value for field3", Integer.valueOf("1"), dest.getField3());
    assertEquals("wrong value for field4", Integer.valueOf("2"), dest.getField4());
    Calendar expected = Calendar.getInstance();
    expected.set(2005, 10, 15);
    assertEquals(expected.get(Calendar.YEAR), dest.getField5().get(Calendar.YEAR));
    assertEquals(Calendar.OCTOBER, dest.getField5().get(Calendar.MONTH));
    assertEquals(expected.get(Calendar.DATE), dest.getField5().get(Calendar.DATE));

    // Remap to test bi-directional mapping
    CustomMap remappedSrc = mapper.map(dest, CustomMap.class, mapId);
    assertTrue("remapped src should equal original src", EqualsBuilder.reflectionEquals(src.getMap(), remappedSrc.getMap()));
  }

  @Test
  public void testMapType_NestedMapToVo_NoCustomMappings() throws Exception {
    // Simple test checking that Maps get mapped to a VO without any custom mappings or map-id.
    // Should behave like Vo --> Vo, matching on common attr(key) names.
    Map<String, String> nested2 = newInstance(HashMap.class);
    nested2.put("field1", "mapnestedfield1");
    nested2.put("field2", null);

    SimpleObj src = newInstance(SimpleObj.class);
    src.setNested2(nested2);

    SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class);
    assertNotNull(result.getNested2());
    assertEquals(nested2.get("field1"), result.getNested2().getField1());

    SimpleObj result2 = mapper.map(result, SimpleObj.class);
    assertEquals(src, result2);
  }

  @Test
  public void testMapType_MapToVo_CustomMapping_NoMapId() {
    // Test nested Map --> Vo using custom mappings without map-id
    mapper = getMapper("mappings/mapMapping3.xml");

    NestedObj nested = newInstance(NestedObj.class);
    nested.setField1("field1Value");

    Map<String, String> nested2 = newInstance(HashMap.class);
    nested2.put("field1", "mapnestedfield1value");
    nested2.put("field2", "mapnestedfield2value");

    SimpleObj src = newInstance(SimpleObj.class);
    src.setNested2(nested2);

    SimpleObjPrime result = mapper.map(src, SimpleObjPrime.class);
    assertNull(result.getNested2().getField1());// field exclude in mappings file
    assertEquals(nested2.get("field2"), result.getNested2().getField2());
  }

  @Ignore("Started failing for some reason. Tests very exotic functionality.")
  @Test
  public void testNestedCustomMap() {
    mapper = getMapper("mappings/mapMapping4.xml");

    ParentDOM src = newInstance(ParentDOM.class);
    src.setTest("someTestValue");
    ChildDOM child = newInstance(ChildDOM.class);
    child.setChildName("someChildName");
    src.setChild(child);

    GenericDOM result = mapper.map(src, GenericDOM.class);
    assertEquals("someTestValue", result.get("test"));
    GenericDOM resultChild = (GenericDOM) result.get("child");
    assertEquals("someChildName", resultChild.get("childName"));
  }

  @Test
  public void testMapToVoUsingMapInterface() throws Exception {
    // Test simple Map --> Vo with custom mappings defined.
    mapper = getMapper("mappings/mapMapping5.xml");

    Map<String, String> src = newInstance(HashMap.class);
    src.put("stringValue", "somevalue");

    SimpleObj dest = mapper.map(src, SimpleObj.class, "test-id");

    assertEquals("wrong value found for field1", "somevalue", dest.getField1());
  }
  
  @Test
  @Ignore("Known bug")
  public void testEmptyMapToVo() throws Exception {
    mapper = getMapper("mappings/mapMapping5.xml");

    Map<String, String> src = newInstance(HashMap.class);
    assertTrue(src.isEmpty());

    SimpleObj dest = new SimpleObj();
    dest.setField1("existingValue");
    mapper.map(src, dest, "test-id");

    assertEquals("existingValue", dest.getField1());
  }

  @Test
  public void testMapToVoOverwritesExistingValue() throws Exception {
    mapper = getMapper("mappings/mapMapping5.xml");

    Map<String, String> src = newInstance(HashMap.class);
    src.put("stringValue", "overwritten");

    SimpleObj dest = new SimpleObj();
    dest.setField1("existingValue");
    mapper.map(src, dest, "test-id");

    assertEquals("overwritten", dest.getField1());
  }

  @Test
  public void testTreeMap() {
    TreeMap map = new TreeMap();
    map.put("a", "b");

    TreeMap result = mapper.map(map, TreeMap.class);

    assertNotNull(result);
    assertEquals(1, result.size());
  }

}
