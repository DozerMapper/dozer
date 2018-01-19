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
package com.github.dozermapper.protobuf.functional_tests;

import java.util.Arrays;

import com.github.dozermapper.protobuf.vo.proto.LiteTestObject;
import com.github.dozermapper.protobuf.vo.proto.MapExample;
import com.github.dozermapper.protobuf.vo.proto.ObjectWithCollection;
import com.github.dozermapper.protobuf.vo.proto.ObjectWithEnumCollection;
import com.github.dozermapper.protobuf.vo.proto.ObjectWithEnumField;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtoEnum;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtoObjectWithEnumField;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtoTestObjectWithNestedProtoObject;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufFieldNaming;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufMapExample;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufWithEnumCollection;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufWithSimpleCollection;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.SimpleProtoTestObject;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.SimpleProtoTestObjectWithoutRequired;
import com.github.dozermapper.protobuf.vo.proto.SimpleEnum;
import com.github.dozermapper.protobuf.vo.proto.TestObject;
import com.github.dozermapper.protobuf.vo.proto.TestObjectContainer;

import org.dozer.Mapper;
import org.dozer.MappingException;
import org.dozer.config.BeanContainer;
import org.dozer.util.MappingUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ProtoBeansMappingTest extends ProtoAbstractTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Mapper mapper;
    private BeanContainer beanContainer;

    @Before
    public void setUp() throws Exception {
        beanContainer = new BeanContainer();
        mapper = getMapper("mappings/protoBeansMapping.xml");
    }

    @Test
    public void testTrivial() {
        Class<?> type = MappingUtils.loadClass(SimpleProtoTestObject.class.getName(), beanContainer);

        assertNotNull(type);
    }

    @Test
    public void testSimpleToProto() {
        TestObject source = new TestObject();
        source.setOne("ABC");

        SimpleProtoTestObject protoResult = mapper.map(source, SimpleProtoTestObject.class);

        assertNotNull(protoResult);
        assertEquals("ABC", protoResult.getOne());
    }

    @Test
    public void testSimpleFromProto() {
        SimpleProtoTestObject.Builder builder = SimpleProtoTestObject.newBuilder();
        builder.setOne("ABC");

        SimpleProtoTestObject source = builder.build();

        TestObject pojoResult = mapper.map(source, TestObject.class);

        assertNotNull(pojoResult);
        assertEquals("ABC", pojoResult.getOne());
    }

    @Test
    public void testSimpleWildcardToProto() {
        LiteTestObject source = new LiteTestObject();
        source.setOne("ABC");

        SimpleProtoTestObject protoResult = mapper.map(source, SimpleProtoTestObject.class);

        assertNotNull(protoResult);
        assertEquals("ABC", protoResult.getOne());
    }

    @Test
    public void testSimpleWildcardFromProto() {
        SimpleProtoTestObject.Builder sourceBuilder = SimpleProtoTestObject.newBuilder();
        sourceBuilder.setOne("ABC");

        LiteTestObject pojoResult = mapper.map(sourceBuilder.build(), LiteTestObject.class);

        assertNotNull(pojoResult);
        assertEquals("ABC", pojoResult.getOne());
    }

    @Test
    public void testSimpleFromProtoWithNull() {
        SimpleProtoTestObjectWithoutRequired.Builder builder = SimpleProtoTestObjectWithoutRequired.newBuilder();
        SimpleProtoTestObjectWithoutRequired source = builder.build();

        TestObject pojoResult = mapper.map(source, TestObject.class);

        assertNotNull(pojoResult);
        assertNull(pojoResult.getOne());
    }

    @Test
    public void testSimpleToProtoWithNull() {
        TestObject source = new TestObject();

        SimpleProtoTestObjectWithoutRequired protoResult = mapper.map(source, SimpleProtoTestObjectWithoutRequired.class);

        assertNotNull(protoResult);
        assertTrue(protoResult.getOne().length() == 0);
    }

    @Test
    public void testNestedProtoFieldToProto() {
        TestObject innerSource = new TestObject();
        innerSource.setOne("InnerName");

        TestObjectContainer source = new TestObjectContainer(innerSource, "Name");

        ProtoTestObjectWithNestedProtoObject result = mapper.map(source, ProtoTestObjectWithNestedProtoObject.class);

        assertNotNull(result);
        assertNotNull(result.getNestedObject());
        assertEquals("Name", result.getOne());
        assertEquals("InnerName", result.getNestedObject().getOne());
    }

    @Test
    public void testNestedProtoFieldFromProto() {
        SimpleProtoTestObject.Builder nestedBuilder = SimpleProtoTestObject.newBuilder();
        nestedBuilder.setOne("InnerName");

        ProtoTestObjectWithNestedProtoObject.Builder builder = ProtoTestObjectWithNestedProtoObject.newBuilder();
        builder.setNestedObject(nestedBuilder.build());
        builder.setOne("Name");

        ProtoTestObjectWithNestedProtoObject source = builder.build();

        TestObjectContainer result = mapper.map(source, TestObjectContainer.class);

        assertNotNull(result);
        assertNotNull(result.getNested());
        assertEquals("Name", result.getOne());
        assertEquals("InnerName", result.getNested().getOne());
    }

    @Test
    public void testEnumProtoFieldToProto() {
        ObjectWithEnumField source = new ObjectWithEnumField();
        source.setEnumField(SimpleEnum.VALUE1);

        ProtoObjectWithEnumField result = mapper.map(source, ProtoObjectWithEnumField.class);

        assertNotNull(result);
        assertNotNull(result.getEnumField());
        assertEquals(ProtoEnum.VALUE1, result.getEnumField());
    }

    @Test
    public void testEnumProtoFieldFromProto() {
        ProtoObjectWithEnumField.Builder builder = ProtoObjectWithEnumField.newBuilder();
        builder.setEnumField(ProtoEnum.VALUE1);

        ProtoObjectWithEnumField source = builder.build();

        ObjectWithEnumField result = mapper.map(source, ObjectWithEnumField.class);

        assertNotNull(result);
        assertNotNull(result.getEnumField());
        assertEquals(SimpleEnum.VALUE1, result.getEnumField());
    }

    @Test
    public void testRepeatedFieldToProto() {
        TestObject innerTestObject = new TestObject();
        innerTestObject.setOne("One");

        ObjectWithCollection source = new ObjectWithCollection();
        source.setObjects(Arrays.asList(innerTestObject));

        ProtobufWithSimpleCollection result = mapper.map(source, ProtobufWithSimpleCollection.class);

        assertNotNull(result);
        assertNotNull(result.getObjectList());
        assertEquals(1, result.getObjectCount());
        assertEquals("One", result.getObject(0).getOne());
    }

    @Test
    public void testRepeatedFieldFromProto() {
        SimpleProtoTestObject.Builder innerTestObjectBuilder = SimpleProtoTestObject.newBuilder();
        innerTestObjectBuilder.setOne("One");

        ProtobufWithSimpleCollection.Builder sourceBuilder = ProtobufWithSimpleCollection.newBuilder();
        sourceBuilder.addAllObject(Arrays.asList(innerTestObjectBuilder.build()));

        ObjectWithCollection result = mapper.map(sourceBuilder.build(), ObjectWithCollection.class);

        assertNotNull(result);
        assertNotNull(result.getObjects());
        assertEquals(1, result.getObjects().size());
        assertEquals("One", result.getObjects().get(0).getOne());
    }

    @Test
    public void testRepeatedEnumFieldToProto() {
        ObjectWithEnumCollection source = new ObjectWithEnumCollection();
        source.setEnums(Arrays.asList(SimpleEnum.VALUE1));

        ProtobufWithEnumCollection result = mapper.map(source, ProtobufWithEnumCollection.class);

        assertNotNull(result);
        assertNotNull(result.getObjectList());
        assertEquals(1, result.getObjectCount());
        assertEquals(ProtoEnum.VALUE1, result.getObject(0));
    }

    @Test
    public void testRepeatedEnumFieldFromProto() {
        ProtobufWithEnumCollection.Builder sourceBuilder = ProtobufWithEnumCollection.newBuilder();
        sourceBuilder.addAllObject(Arrays.asList(ProtoEnum.VALUE1));

        ObjectWithEnumCollection result = mapper.map(sourceBuilder.build(), ObjectWithEnumCollection.class);

        assertNotNull(result);
        assertNotNull(result.getEnums());
        assertEquals(1, result.getEnums().size());
        assertEquals(SimpleEnum.VALUE1, result.getEnums().get(0));
    }

    @Test
    public void testBadMapToProto() {
        thrown.expect(MappingException.class);

        MapExample mapExample = new MapExample();
        mapExample.put("test", null);
        mapExample.put("foo", "bar");

        mapper.map(mapExample, ProtobufMapExample.class);
    }

    @Test
    public void testMapToProto() {
        MapExample mapExample = new MapExample();

        // put a valid value
        mapExample.put("test", "value");
        ProtobufMapExample protoMapExample = mapper.map(mapExample, ProtobufMapExample.class);

        // mapping OK
        assertEquals(mapExample.getValue(), protoMapExample.getValueMap());
    }

    @Test
    public void testBadMapFromProto() {
        thrown.expect(NullPointerException.class);

        ProtobufMapExample.Builder protoMapExample = ProtobufMapExample.newBuilder();
        protoMapExample.putValue("test", null);
    }

    @Test
    public void testMapFromProto() {
        ProtobufMapExample.Builder protoMapExample = ProtobufMapExample.newBuilder();
        protoMapExample.putValue("test", "value");
        protoMapExample.putValue("foo", "bar");

        MapExample mapExample = mapper.map(protoMapExample, MapExample.class);

        assertEquals(protoMapExample.getValueMap(), mapExample.getValue());
    }

    @Test
    public void testSnakeCaseFieldFromProto() {
        ProtobufFieldNaming.Builder protoFieldNaming = ProtobufFieldNaming.newBuilder();
        protoFieldNaming.setSnakeCaseField("some value");

        FieldNaming fieldNaming = mapper.map(protoFieldNaming, FieldNaming.class);

        assertEquals(protoFieldNaming.getSnakeCaseField(), fieldNaming.getSnakeCaseField());
    }

    @Test
    public void testSnakeCaseFieldToProto() {
        FieldNaming fieldNaming = new FieldNaming();
        fieldNaming.setSnakeCaseField("some value");

        ProtobufFieldNaming protoFieldNaming = mapper.map(fieldNaming, ProtobufFieldNaming.class);

        assertEquals(fieldNaming.getSnakeCaseField(), protoFieldNaming.getSnakeCaseField());
    }
}
