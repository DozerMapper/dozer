/**
 * Copyright 2005-2013 Dozer Project
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

import org.dozer.DozerBeanMapper;
import org.dozer.util.MappingUtils;
import org.dozer.vo.proto.*;
import org.dozer.vo.proto.ProtoTestObjects.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Dmitry Spikhalskiy
 */
public class ProtoBeansMappingTest extends ProtoAbstractTest {
  protected DozerBeanMapper mapper;

  @Before
  public void setUp() throws Exception {
    mapper = getMapper("protoBeansMapping.xml");
  }

  @Test
  public void testTrivial() {
    Class<?> type = MappingUtils.loadClass("org.dozer.vo.proto.ProtoTestObjects.SimpleProtoTestObject");
    assertNotNull(type);
  }

  @Test
  public void testSimple_toProto() {
    TestObject source = new TestObject();
    source.setOne("ABC");
    SimpleProtoTestObject protoResult = mapper.map(source, SimpleProtoTestObject.class);
    assertNotNull(protoResult);
    Assert.assertEquals("ABC", protoResult.getOne());
  }

  @Test
  public void testSimple_fromProto() {
    SimpleProtoTestObject.Builder builder = SimpleProtoTestObject.newBuilder();
    builder.setOne("ABC");
    SimpleProtoTestObject source = builder.build();

    TestObject pojoResult = mapper.map(source, TestObject.class);
    assertNotNull(pojoResult);
    Assert.assertEquals("ABC", pojoResult.getOne());
  }

  @Test
  public void testSimple_wildcard_toProto() {
    LiteTestObject source = new LiteTestObject();
    source.setOne("ABC");
    SimpleProtoTestObject protoResult = mapper.map(source, SimpleProtoTestObject.class);
    assertNotNull(protoResult);
    Assert.assertEquals("ABC", protoResult.getOne());
  }

  @Test
  public void testSimple_wildcard_fromProto() {
    SimpleProtoTestObject.Builder sourceBuilder = SimpleProtoTestObject.newBuilder();
    sourceBuilder.setOne("ABC");
    LiteTestObject pojoResult = mapper.map(sourceBuilder.build(), LiteTestObject.class);
    assertNotNull(pojoResult);
    Assert.assertEquals("ABC", pojoResult.getOne());
  }

  @Test
  public void testSimple_fromProtoWithNull() {
    SimpleProtoTestObjectWithoutRequired.Builder builder = SimpleProtoTestObjectWithoutRequired.newBuilder();
    SimpleProtoTestObjectWithoutRequired source = builder.build();

    TestObject pojoResult = mapper.map(source, TestObject.class);
    assertNotNull(pojoResult);
    assertNull(pojoResult.getOne());
  }

  @Test
  public void testSimple_toProtoWithNull() {
    TestObject source = new TestObject();

    SimpleProtoTestObjectWithoutRequired protoResult = mapper.map(source, SimpleProtoTestObjectWithoutRequired.class);
    assertNotNull(protoResult);
    assertFalse(protoResult.hasOne());
  }

  @Test
  public void testNestedProtoField_toProto() {
    TestObject innerSource = new TestObject();
    innerSource.setOne("InnerName");
    TestObjectContainer source = new TestObjectContainer(innerSource, "Name");

    ProtoTestObjectWithNestedProtoObject result = mapper.map(source, ProtoTestObjectWithNestedProtoObject.class);
    assertNotNull(result);
    Assert.assertEquals("Name", result.getOne());
    assertNotNull(result.getNestedObject());
    Assert.assertEquals("InnerName", result.getNestedObject().getOne());
  }

  @Test
  public void testNestedProtoField_fromProto() {
    SimpleProtoTestObject.Builder nestedBuilder = SimpleProtoTestObject.newBuilder();
    nestedBuilder.setOne("InnerName");
    ProtoTestObjectWithNestedProtoObject.Builder builder = ProtoTestObjectWithNestedProtoObject.newBuilder();
    builder.setNestedObject(nestedBuilder.build());
    builder.setOne("Name");
    ProtoTestObjectWithNestedProtoObject source = builder.build();

    TestObjectContainer result = mapper.map(source, TestObjectContainer.class);
    assertNotNull(result);
    Assert.assertEquals("Name", result.getOne());
    assertNotNull(result.getNested());
    Assert.assertEquals("InnerName", result.getNested().getOne());
  }

  @Test
  public void testEnumProtoField_toProto() {
    ObjectWithEnumField source = new ObjectWithEnumField();
    source.setEnumField(SimpleEnum.VALUE1);

    ProtoObjectWithEnumField result = mapper.map(source, ProtoObjectWithEnumField.class);
    assertNotNull(result);
    assertNotNull(result.getEnumField());
    Assert.assertEquals(ProtoEnum.VALUE1, result.getEnumField());
  }

  @Test
  public void testEnumProtoField_fromProto() {
    ProtoObjectWithEnumField.Builder builder = ProtoObjectWithEnumField.newBuilder();
    builder.setEnumField(ProtoEnum.VALUE1);
    ProtoObjectWithEnumField source = builder.build();

    ObjectWithEnumField result = mapper.map(source, ObjectWithEnumField.class);
    assertNotNull(result);
    assertNotNull(result.getEnumField());
    Assert.assertEquals(SimpleEnum.VALUE1, result.getEnumField());
  }

  @Test
  public void testRepeatedField_toProto() {
    ObjectWithCollection source = new ObjectWithCollection();
    TestObject innerTestObject = new TestObject();
    innerTestObject.setOne("One");

    source.setObjects(Arrays.<TestObject>asList(innerTestObject));

    ProtobufWithSimpleCollection result = mapper.map(source, ProtobufWithSimpleCollection.class);
    assertNotNull(result);
    assertNotNull(result.getObjectList());
    Assert.assertEquals(1, result.getObjectCount());
    Assert.assertEquals("One", result.getObject(0).getOne());
  }

  @Test
  public void testRepeatedField_fromProto() {
    SimpleProtoTestObject.Builder innerTestObjectBuilder = SimpleProtoTestObject.newBuilder();
    innerTestObjectBuilder.setOne("One");

    ProtobufWithSimpleCollection.Builder sourceBuilder = ProtobufWithSimpleCollection.newBuilder();
    sourceBuilder.addAllObject(Arrays.asList(innerTestObjectBuilder.build()));

    ObjectWithCollection result = mapper.map(sourceBuilder.build(), ObjectWithCollection.class);
    assertNotNull(result);
    assertNotNull(result.getObjects());
    Assert.assertEquals(1, result.getObjects().size());
    Assert.assertEquals("One", result.getObjects().get(0).getOne());
  }

  @Test
  public void testRepeatedEnumField_toProto() {
    ObjectWithEnumCollection source = new ObjectWithEnumCollection();
    source.setEnums(Arrays.asList(SimpleEnum.VALUE1));

    ProtobufWithEnumCollection result = mapper.map(source, ProtobufWithEnumCollection.class);
    assertNotNull(result);
    assertNotNull(result.getObjectList());
    Assert.assertEquals(1, result.getObjectCount());
    Assert.assertEquals(ProtoEnum.VALUE1, result.getObject(0));
  }

  @Test
  public void testRepeatedEnumField_fromProto() {
    ProtobufWithEnumCollection.Builder sourceBuilder = ProtobufWithEnumCollection.newBuilder();
    sourceBuilder.addAllObject(Arrays.asList(ProtoEnum.VALUE1));

    ObjectWithEnumCollection result = mapper.map(sourceBuilder.build(), ObjectWithEnumCollection.class);
    assertNotNull(result);
    assertNotNull(result.getEnums());
    Assert.assertEquals(1, result.getEnums().size());
    Assert.assertEquals(SimpleEnum.VALUE1, result.getEnums().get(0));
  }
}
