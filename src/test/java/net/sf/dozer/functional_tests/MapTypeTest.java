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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import net.sf.dozer.util.mapping.MapperIF;
import net.sf.dozer.util.mapping.vo.TestObject;
import net.sf.dozer.util.mapping.vo.TestObjectPrime;
import net.sf.dozer.util.mapping.vo.map.ChildDOM;
import net.sf.dozer.util.mapping.vo.map.CustomMap;
import net.sf.dozer.util.mapping.vo.map.CustomMapIF;
import net.sf.dozer.util.mapping.vo.map.GenericDOM;
import net.sf.dozer.util.mapping.vo.map.MapTestObject;
import net.sf.dozer.util.mapping.vo.map.MapTestObjectPrime;
import net.sf.dozer.util.mapping.vo.map.MapToMap;
import net.sf.dozer.util.mapping.vo.map.MapToMapPrime;
import net.sf.dozer.util.mapping.vo.map.MapToProperty;
import net.sf.dozer.util.mapping.vo.map.NestedObj;
import net.sf.dozer.util.mapping.vo.map.NestedObjPrime;
import net.sf.dozer.util.mapping.vo.map.ParentDOM;
import net.sf.dozer.util.mapping.vo.map.PropertyToMap;
import net.sf.dozer.util.mapping.vo.map.SimpleObj;
import net.sf.dozer.util.mapping.vo.map.SimpleObjPrime;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class MapTypeTest extends AbstractMapperTest {
  public void testMapToVo() throws Exception {
    // Test simple Map --> Vo with custom mappings defined.
    mapper = getMapper(new String[] { "mapMapping2.xml" });

    NestedObj nestedObj = (NestedObj) newInstance(NestedObj.class);
    nestedObj.setField1("nestedfield1value");
    Map src = (Map) newInstance(HashMap.class);
    src.put("field1", "mapnestedfield1value");
    src.put("nested", nestedObj);

    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class, "caseA");
    assertEquals(src.get("field1"), result.getField1());
    assertEquals(nestedObj.getField1(), result.getNested().getField1());
  }

  public void testMapToVo_CustomMappings() throws Exception {
    // Test simple Map --> Vo with custom mappings defined.
    mapper = getMapper(new String[] { "mapMapping2.xml" });
    Map src = (Map) newInstance(HashMap.class);
    src.put("field1", "field1value");
    src.put("field2", "field2value");

    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class, "caseB");
    assertNull(result.getField1());
    assertEquals(src.get("field2"), result.getField2());
  }

  public void testMapToVoUsingMapId() {
    // Simple map --> vo using a map-id
    mapper = super.getMapper(new String[] { "mapMapping.xml" });
    Map src = (Map) newInstance(HashMap.class);
    src.put("field1", "field1value");
    src.put("field2", "field2value");

    NestedObjPrime dest = (NestedObjPrime) mapper.map(src, NestedObjPrime.class, "caseB");
    assertEquals(src.get("field1"), dest.getField1());
    assertEquals(src.get("field2"), dest.getField2());
  }

  public void testMapToVoUsingMapId_FieldExclude() {
    // Simple map --> vo using a map-id
    mapper = super.getMapper(new String[] { "mapMapping.xml" });
    Map src = (Map) newInstance(HashMap.class);
    src.put("field1", "field1value");
    src.put("field2", "field2value");

    NestedObjPrime dest = (NestedObjPrime) mapper.map(src, NestedObjPrime.class, "caseC");
    assertNull("field was excluded and should be null", dest.getField1());
    assertEquals(src.get("field2"), dest.getField2());
  }

  public void testNestedMapToVoUsingMapId() {
    // Another test for nested Map --> Vo using <field map-id=....>
    mapper = super.getMapper(new String[] { "mapMapping.xml" });

    SimpleObj src = (SimpleObj) newInstance(SimpleObj.class);

    src.setField1("field1");

    NestedObj nested = (NestedObj) newInstance(NestedObj.class);
    nested.setField1("nestedfield1");
    src.setNested(nested);

    Map nested2 = (Map) newInstance(HashMap.class);
    nested2.put("field1", "field1MapValue");
    src.setNested2(nested2);

    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class, "caseA2");

    assertNull(result.getNested2().getField1());// field was excluded
    assertEquals(src.getField1(), result.getField1());
    assertEquals(src.getNested().getField1(), result.getNested().getField1());
  }

  public void testMapToVo_NoCustomMappings() throws Exception {
    // Test simple Map --> Vo without any custom mappings defined.
    NestedObj nestedObj = (NestedObj) newInstance(NestedObj.class);
    nestedObj.setField1("nestedfield1value");
    Map src = (Map) newInstance(HashMap.class);
    src.put("field1", "mapnestedfield1value");
    src.put("nested", nestedObj);

    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class);
    assertEquals(src.get("field1"), result.getField1());
    assertEquals(nestedObj.getField1(), result.getNested().getField1());
  }

  public void testVoToMap_NoCustomMappings() throws Exception {
    // Test simple Vo --> Map without any custom mappings defined.
    SimpleObjPrime src = (SimpleObjPrime) newInstance(SimpleObjPrime.class);
    src.setField1("someValueField1");
    src.setField2("someValueField2");
    src.setSimpleobjprimefield("someOtherValue");

    NestedObjPrime nested = (NestedObjPrime) newInstance(NestedObjPrime.class);
    nested.setField1("field1Value");
    nested.setField2("field2Value");
    src.setNested(nested);

    NestedObjPrime nested2 = (NestedObjPrime) newInstance(NestedObjPrime.class);
    src.setNested2(nested2);

    // Map complex object to HashMap
    Map destMap = (Map) newInstance(HashMap.class);
    mapper.map(src, destMap);

    // Map HashMap back to new instance of the complex object
    SimpleObjPrime mappedSrc = (SimpleObjPrime) mapper.map(destMap, SimpleObjPrime.class);

    // Remapped complex type should equal original src if all fields were mapped both ways.
    assertEquals(src, mappedSrc);
  }

  public void testMapToMap() throws Exception {
    MapperIF mapper = getMapper(new String[] { "mapInterfaceMapping.xml", "dozerBeanMapping.xml" });
    TestObject to = (TestObject) newInstance(TestObject.class);
    to.setOne("one");
    TestObject to2 = (TestObject) newInstance(TestObject.class);
    to2.setTwo(new Integer(2));
    Map map = (Map) newInstance(HashMap.class);
    map.put("to", to);
    map.put("to2", to2);

    Map map2 = (Map) newInstance(HashMap.class);
    map2.put("to", to);
    map2.put("to2", to2);

    MapToMap mtm = new MapToMap(map, map2);

    MapToMapPrime mtmp = (MapToMapPrime) mapper.map(mtm, MapToMapPrime.class);
    assertEquals("one", ((TestObject) mtmp.getStandardMap().get("to")).getOne());
    assertEquals(2, ((TestObject) mtmp.getStandardMap().get("to2")).getTwo().intValue());
    // verify that we transformed from object to object prime
    assertEquals("one", ((TestObjectPrime) mtmp.getStandardMapWithHint().get("to")).getOnePrime());
    assertEquals(2, ((TestObjectPrime) mtmp.getStandardMapWithHint().get("to2")).getTwoPrime().intValue());
  }

  public void testMapToMapExistingDestination() throws Exception {
    MapperIF mapper = getMapper(new String[] { "mapInterfaceMapping.xml", "dozerBeanMapping.xml" });
    TestObject to = (TestObject) newInstance(TestObject.class);
    to.setOne("one");
    TestObject to2 = (TestObject) newInstance(TestObject.class);
    to2.setTwo(new Integer(2));
    Map map = (Map) newInstance(HashMap.class);
    map.put("to", to);
    map.put("to2", to2);
    MapToMap mtm = (MapToMap) newInstance(MapToMap.class);
    mtm.setStandardMap(map);

    // create an existing map and set a value so we can test if it exists after
    // mapping
    MapToMapPrime mtmp = (MapToMapPrime) newInstance(MapToMapPrime.class);
    Map map2 = (Map) newInstance(Hashtable.class);
    map2.put("toDest", to);
    mtmp.setStandardMap(map2);

    mapper.map(mtm, mtmp);
    assertEquals("one", ((TestObject) mtmp.getStandardMap().get("to")).getOne());
    assertEquals(2, ((TestObject) mtmp.getStandardMap().get("to2")).getTwo().intValue());
    assertEquals("one", ((TestObject) mtmp.getStandardMap().get("toDest")).getOne());
  }

  public void testPropertyClassLevelMap() throws Exception {
    mapper = getMapper(new String[] { "dozerBeanMapping.xml" });
    PropertyToMap ptm = (PropertyToMap) newInstance(PropertyToMap.class);
    ptm.setStringProperty("stringPropertyValue");
    ptm.addStringProperty2("stringProperty2Value");
    Map map = (Map) mapper.map(ptm, HashMap.class, "myTestMapping");
    assertEquals("stringPropertyValue", map.get("stringProperty"));
    assertEquals("stringProperty2Value", map.get("myStringProperty"));

    CustomMapIF customMap = (CustomMapIF) mapper.map(ptm, CustomMap.class, "myCustomTestMapping");
    assertEquals("stringPropertyValue", customMap.getValue("stringProperty"));
    assertEquals("stringProperty2Value", customMap.getValue("myStringProperty"));

    CustomMapIF custom = (CustomMapIF) newInstance(CustomMap.class);
    custom.putValue("myKey", "myValue");
    mapper.map(ptm, custom, "myCustomTestMapping");
    assertEquals("stringPropertyValue", custom.getValue("stringProperty"));
    assertEquals("myValue", custom.getValue("myKey"));
  }

  public void testPropertyClassLevelMap2() throws Exception {
    mapper = getMapper(new String[] { "dozerBeanMapping.xml" });
    PropertyToMap ptm = (PropertyToMap) newInstance(PropertyToMap.class);
    ptm.setStringProperty("stringPropertyValue");
    ptm.addStringProperty2("stringProperty2Value");

    CustomMapIF customMap = (CustomMapIF) mapper.map(ptm, CustomMap.class, "myCustomTestMapping");
    assertEquals("stringPropertyValue", customMap.getValue("stringProperty"));
    assertEquals("stringProperty2Value", customMap.getValue("myStringProperty"));
  }

  public void testPropertyClassLevelMapBack() throws Exception {
    // Map Back
    mapper = getMapper(new String[] { "dozerBeanMapping.xml" });
    Map map = (Map) newInstance(HashMap.class);
    map.put("stringProperty", "stringPropertyValue");
    map.put("integerProperty", new Integer("567"));
    PropertyToMap property = (PropertyToMap) mapper.map(map, PropertyToMap.class, "myTestMapping");
    assertEquals("stringPropertyValue", property.getStringProperty());

    CustomMapIF custom = (CustomMapIF) newInstance(CustomMap.class);
    custom.putValue("stringProperty", "stringPropertyValue");
    PropertyToMap property2 = (PropertyToMap) mapper.map(custom, PropertyToMap.class, "myCustomTestMapping");
    assertEquals("stringPropertyValue", property2.getStringProperty());

    map.put("stringProperty3", "myValue");
    mapper.map(map, property, "myTestMapping");
    assertEquals("myValue", property.getStringProperty3());
  }

  public void testPropertyToMap() throws Exception {
    mapper = getMapper(new String[] { "dozerBeanMapping.xml" });
    PropertyToMap ptm = (PropertyToMap) newInstance(PropertyToMap.class);
    ptm.setStringProperty("stringPropertyValue");
    ptm.addStringProperty2("stringProperty2Value");
    ptm.setStringProperty6("string6Value");
    Map hashMap = (Map) newInstance(HashMap.class);
    hashMap.put("reverseMapString", "reverseMapStringValue");
    hashMap.put("reverseMapInteger", new Integer("567"));
    ptm.setReverseMap(hashMap);
    MapToProperty mtp = (MapToProperty) mapper.map(ptm, MapToProperty.class);
    assertTrue(mtp.getHashMap().containsKey("stringProperty"));
    assertTrue(mtp.getHashMap().containsValue("stringPropertyValue"));
    assertTrue(mtp.getHashMap().containsKey("myStringProperty"));
    assertTrue(mtp.getHashMap().containsValue("stringProperty2Value"));
    assertFalse(mtp.getHashMap().containsValue("nullStringProperty"));
    assertTrue(mtp.getNullHashMap().containsValue("string6Value"));
    assertEquals("reverseMapStringValue", mtp.getReverseMapString());
    assertEquals(((Integer) hashMap.get("reverseMapInteger")).toString(), mtp.getReverseMapInteger());

    // Map Back
    PropertyToMap dest = (PropertyToMap) mapper.map(mtp, PropertyToMap.class);
    assertTrue(dest.getStringProperty().equals("stringPropertyValue"));
    assertTrue(dest.getStringProperty2().equals("stringProperty2Value"));
    assertTrue(dest.getReverseMap().containsKey("reverseMapString"));
    assertTrue(dest.getReverseMap().containsValue("reverseMapStringValue"));
    assertNull(dest.getNullStringProperty());
  }

  public void testPropertyToCustomMap() throws Exception {
    mapper = getMapper(new String[] { "dozerBeanMapping.xml" });
    PropertyToMap ptm = (PropertyToMap) newInstance(PropertyToMap.class);
    ptm.setStringProperty3("stringProperty3Value");
    ptm.setStringProperty4("stringProperty4Value");
    ptm.setStringProperty5("stringProperty5Value");
    MapToProperty mtp = (MapToProperty) mapper.map(ptm, MapToProperty.class);
    assertEquals("stringProperty3Value", mtp.getCustomMap().getValue("myCustomProperty"));
    assertEquals("stringProperty5Value", mtp.getCustomMap().getValue("stringProperty5"));
    assertEquals("stringProperty4Value", mtp.getNullCustomMap().getValue("myCustomNullProperty"));
    assertEquals("stringProperty5Value", mtp.getCustomMapWithDiffSetMethod().getValue("stringProperty5"));

    // Map Back
    PropertyToMap dest = (PropertyToMap) mapper.map(mtp, PropertyToMap.class);
    assertEquals("stringProperty3Value", dest.getStringProperty3());
    assertEquals("stringProperty4Value", dest.getStringProperty4());
    assertEquals("stringProperty5Value", dest.getStringProperty5());
  }

  // TODO Enable this test in next release. JDK 1.6 compatibility issue
  public void disabled_testPropertyToClassLevelMap() throws Exception {
    mapper = getMapper(new String[] { "dozerBeanMapping.xml" });
    MapTestObject mto = (MapTestObject) newInstance(MapTestObject.class);
    PropertyToMap ptm = (PropertyToMap) newInstance(PropertyToMap.class);
    Map map = (Map) newInstance(HashMap.class);
    map.put("reverseClassLevelMapString", "reverseClassLevelMapStringValue");
    mto.setPropertyToMapMapReverse(map);
    ptm.setStringProperty("stringPropertyValue");
    ptm.addStringProperty2("stringProperty2Value");
    ptm.setStringProperty3("stringProperty3Value");
    ptm.setStringProperty4("stringProperty4Value");
    ptm.setStringProperty5("stringProperty5Value");
    mto.setPropertyToMap(ptm);
    PropertyToMap ptm2 = (PropertyToMap) newInstance(PropertyToMap.class);
    ptm2.setStringProperty("stringPropertyValue");
    mto.setPropertyToMapToNullMap(ptm2);
    MapTestObjectPrime mtop = (MapTestObjectPrime) mapper.map(mto, MapTestObjectPrime.class);
    assertTrue(mtop.getPropertyToMapMap().containsKey("stringProperty"));
    assertTrue(mtop.getPropertyToMapMap().containsKey("myStringProperty"));
    assertTrue(mtop.getPropertyToMapMap().containsKey("stringProperty3"));
    assertTrue(mtop.getPropertyToMapMap().containsKey("stringProperty4"));
    assertTrue(mtop.getPropertyToMapMap().containsKey("stringProperty5"));
    assertFalse(mtop.getPropertyToMapMap().containsKey("nullStringProperty"));
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
    MapTestObject mto2 = (MapTestObject) mapper.map(mtop, MapTestObject.class);
    assertEquals("stringPropertyValue", mto2.getPropertyToMap().getStringProperty());
    assertEquals("stringProperty2Value", mto2.getPropertyToMap().getStringProperty2());
    assertEquals("stringProperty3Value", mto2.getPropertyToMap().getStringProperty3());
    assertEquals("stringProperty4Value", mto2.getPropertyToMap().getStringProperty4());
    assertEquals("stringProperty5Value", mto2.getPropertyToMap().getStringProperty5());
    assertTrue(mto2.getPropertyToMapMapReverse().containsKey("reverseClassLevelMapString"));
    assertEquals("reverseClassLevelMapStringValue", mto2.getPropertyToMapMapReverse().get("reverseClassLevelMapString"));

  }

  public void testPropertyToCustomClassLevelMap() throws Exception {
    mapper = getMapper(new String[] { "dozerBeanMapping.xml" });
    MapTestObject mto = (MapTestObject) newInstance(MapTestObject.class);
    PropertyToMap ptm = (PropertyToMap) newInstance(PropertyToMap.class);
    ptm.setStringProperty("stringPropertyValue");
    ptm.setStringProperty2("stringProperty2Value");
    mto.setPropertyToCustomMap(ptm);
    CustomMapIF customMap = (CustomMapIF) newInstance(CustomMap.class);
    customMap.putValue("stringProperty", "stringPropertyValue");
    mto.setPropertyToCustomMapMapWithInterface(customMap);
    MapTestObjectPrime mtop = (MapTestObjectPrime) mapper.map(mto, MapTestObjectPrime.class);
    assertEquals("stringPropertyValue", mtop.getPropertyToCustomMapMap().getValue("stringProperty"));
    assertNull(mtop.getPropertyToCustomMapMap().getValue("excludeMe"));
    assertEquals("stringProperty2Value", mtop.getPropertyToCustomMapMap().getValue("myStringProperty"));
    assertEquals("stringPropertyValue", mtop.getPropertyToCustomMapWithInterface().getStringProperty());

    // Map Back
    MapTestObject mto2 = (MapTestObject) mapper.map(mtop, MapTestObject.class);
    assertEquals("stringPropertyValue", mto2.getPropertyToCustomMap().getStringProperty());
    assertEquals("stringProperty2Value", mto2.getPropertyToCustomMap().getStringProperty2());
    assertNull(mto2.getPropertyToCustomMap().getExcludeMe());
    assertEquals("stringPropertyValue", mto2.getPropertyToCustomMapMapWithInterface().getValue("stringProperty"));
  }

  public void testMapGetSetMethod_ClassLevel() throws Exception {
    runMapGetSetMethodTest("useCase1");
  }

  public void testMapGetSetMethod_FieldLevel() throws Exception {
    runMapGetSetMethodTest("useCase2");
  }

  public void testDateFormat_CustomMapType() throws Exception {
    // Test that date format works for mapping between String and Custom Map Type
    mapper = getMapper(new String[] { "mapMapping3.xml" });
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    String dateStr = "10/15/2005";
    CustomMap src = (CustomMap) newInstance(CustomMap.class);
    src.putValue("fieldA", dateStr);

    net.sf.dozer.util.mapping.vo.SimpleObj dest = (net.sf.dozer.util.mapping.vo.SimpleObj) mapper.map(src,
        net.sf.dozer.util.mapping.vo.SimpleObj.class);
    assertNotNull("dest field should not be null", dest.getField5());
    assertEquals("dest field contains wrong date value", df.parse(dateStr), dest.getField5().getTime());

    CustomMap remappedSrc = (CustomMap) mapper.map(dest, CustomMap.class);
    assertEquals("remapped src field contains wrong date string", dateStr, remappedSrc.getValue("fieldA"));
  }

  private void runMapGetSetMethodTest(String mapId) throws Exception {
    // Test that custom field converter works for Custom Map Types
    mapper = getMapper(new String[] { "mapGetSetMethodMapping.xml" });
    CustomMap src = (CustomMap) newInstance(CustomMap.class);
    src.putValue("fieldA", "someStringValue");
    src.putValue("field2", "someOtherStringValue");
    src.putValue("fieldC", "1");
    src.putValue("fieldD", "2");
    src.putValue("fieldE", "10-15-2005");

    SimpleObj dest = (SimpleObj) mapper.map(src, SimpleObj.class, mapId);
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
    CustomMap remappedSrc = (CustomMap) mapper.map(dest, CustomMap.class, mapId);
    assertTrue("remapped src should equal original src", EqualsBuilder.reflectionEquals(src.getMap(), remappedSrc.getMap()));
  }

  public void testMapType_NestedMapToVo_NoCustomMappings() throws Exception {
    // Simple test checking that Maps get mapped to a VO without any custom mappings or map-id.
    // Should behave like Vo --> Vo, matching on common attr(key) names.
    Map nested2 = (Map) newInstance(HashMap.class);
    nested2.put("field1", "mapnestedfield1");

    SimpleObj src = (SimpleObj) newInstance(SimpleObj.class);
    src.setNested2(nested2);

    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class);
    assertNotNull(result.getNested2());
    assertEquals(nested2.get("field1"), result.getNested2().getField1());

    SimpleObj result2 = (SimpleObj) mapper.map(result, SimpleObj.class);
    assertEquals(src, result2);
  }

  public void testMapType_MapToVo_CustomMapping_NoMapId() {
    // Test nested Map --> Vo using custom mappings without map-id
    mapper = getMapper(new String[] { "mapMapping3.xml" });

    NestedObj nested = (NestedObj) newInstance(NestedObj.class);
    nested.setField1("field1Value");

    Map nested2 = (Map) newInstance(HashMap.class);
    nested2.put("field1", "mapnestedfield1value");
    nested2.put("field2", "mapnestedfield2value");

    SimpleObj src = (SimpleObj) newInstance(SimpleObj.class);
    // src.setField1("field1value");
    // src.setNested(nested);
    src.setNested2(nested2);

    SimpleObjPrime result = (SimpleObjPrime) mapper.map(src, SimpleObjPrime.class);
    assertNull(result.getNested2().getField1());// field exclude in mappings file
    assertEquals(nested2.get("field2"), result.getNested2().getField2());
  }

  // TODO Enable this test in next release. JDK 1.6 compatibility issue
  public void disable_testNestedCustomMap() {
    mapper = getMapper(new String[] { "mapMapping4.xml" });

    ParentDOM src = (ParentDOM) newInstance(ParentDOM.class);
    src.setTest("someTestValue");
    ChildDOM child = (ChildDOM) newInstance(ChildDOM.class);
    child.setChildName("someChildName");
    src.setChild(child);

    GenericDOM result = (GenericDOM) mapper.map(src, GenericDOM.class);
    assertEquals("someTestValue", result.get("test"));
    GenericDOM resultChild = (GenericDOM) result.get("child");
    assertEquals("someChildName", resultChild.get("childName"));
  }

  public void testMapToVoUsingMapInterface() throws Exception {
    // Test simple Map --> Vo with custom mappings defined.
    mapper = getMapper(new String[] { "mapMapping5.xml" });

    Map src = new HashMap();
    src.put("stringValue", "somevalue");

    SimpleObj dest = (SimpleObj) mapper.map(src, SimpleObj.class, "test-id");

    assertEquals("wrong value found for field1", "somevalue", dest.getField1());
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}