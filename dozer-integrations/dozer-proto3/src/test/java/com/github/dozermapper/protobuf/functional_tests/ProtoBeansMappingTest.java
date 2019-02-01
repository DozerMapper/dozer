/*
 * Copyright 2005-2019 Dozer Project
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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.protobuf.vo.proto.FieldNaming;
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

import org.hamcrest.core.IsInstanceOf;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ProtoBeansMappingTest {

    private static Mapper mapper;

    @Rule
    public ExpectedException badMapToProtoExpectedException = ExpectedException.none();

    @Rule
    public ExpectedException badMapFromProtoExpectedException = ExpectedException.none();

    @BeforeClass
    public static void setUp() {
        mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/protoBeansMapping.xml")
                .build();
    }

    @Test
    public void canSimpleToProto() {
        TestObject testObject = new TestObject();
        testObject.setOne("ABC");

        SimpleProtoTestObject result = mapper.map(testObject, SimpleProtoTestObject.class);

        assertNotNull(result);
        assertNotNull(result.getOne());
        assertEquals(testObject.getOne(), result.getOne());
    }

    @Test
    public void canSimpleFromProto() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder.setOne("ABC");

        SimpleProtoTestObject simpleProtoTestObject = simpleProtoTestObjectBuilder.build();

        TestObject result = mapper.map(simpleProtoTestObject, TestObject.class);

        assertNotNull(result);
        assertNotNull(result.getOne());
        assertEquals(simpleProtoTestObject.getOne(), result.getOne());
    }

    @Test
    public void canSimpleWildcardToProto() {
        LiteTestObject liteTestObject = new LiteTestObject();
        liteTestObject.setOne("ABC");

        SimpleProtoTestObject result = mapper.map(liteTestObject, SimpleProtoTestObject.class);

        assertNotNull(result);
        assertNotNull(result.getOne());
        assertEquals("ABC", result.getOne());
    }

    @Test
    public void canSimpleWildcardFromProto() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder.setOne("ABC");

        LiteTestObject result = mapper.map(simpleProtoTestObjectBuilder.build(), LiteTestObject.class);

        assertNotNull(result);
        assertNotNull(result.getOne());
        assertEquals("ABC", result.getOne());
    }

    @Test
    public void canSimpleFromProtoWithNull() {
        SimpleProtoTestObjectWithoutRequired.Builder simpleProtoTestObjectWithoutRequiredBuilder = SimpleProtoTestObjectWithoutRequired.newBuilder();
        SimpleProtoTestObjectWithoutRequired simpleProtoTestObjectWithoutRequired = simpleProtoTestObjectWithoutRequiredBuilder.build();

        TestObject result = mapper.map(simpleProtoTestObjectWithoutRequired, TestObject.class);

        assertNotNull(result);
        assertNull(result.getOne());
    }

    @Test
    public void canSimpleToProtoWithNull() {
        TestObject testObject = new TestObject();

        SimpleProtoTestObjectWithoutRequired result = mapper.map(testObject, SimpleProtoTestObjectWithoutRequired.class);

        assertNotNull(result);
        assertNotNull(result.getOne());
        assertTrue(result.getOne().length() == 0);
    }

    @Test
    public void canNestedProtoFieldToProto() {
        TestObject testObject = new TestObject();
        testObject.setOne("InnerName");

        TestObjectContainer testObjectContainer = new TestObjectContainer(testObject, "Name");

        ProtoTestObjectWithNestedProtoObject result = mapper.map(testObjectContainer, ProtoTestObjectWithNestedProtoObject.class);

        assertNotNull(result);
        assertNotNull(result.getOne());
        assertNotNull(result.getNestedObject());
        assertEquals(testObjectContainer.getOne(), result.getOne());
        assertEquals(testObjectContainer.getNested().getOne(), result.getNestedObject().getOne());
    }

    @Test
    public void canNestedProtoFieldFromProto() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder.setOne("InnerName");

        ProtoTestObjectWithNestedProtoObject.Builder protoTestObjectWithNestedProtoObjectBuilder = ProtoTestObjectWithNestedProtoObject.newBuilder();
        protoTestObjectWithNestedProtoObjectBuilder.setNestedObject(simpleProtoTestObjectBuilder.build());
        protoTestObjectWithNestedProtoObjectBuilder.setOne("Name");

        ProtoTestObjectWithNestedProtoObject protoTestObjectWithNestedProtoObject = protoTestObjectWithNestedProtoObjectBuilder.build();

        TestObjectContainer result = mapper.map(protoTestObjectWithNestedProtoObject, TestObjectContainer.class);

        assertNotNull(result);
        assertNotNull(result.getOne());
        assertNotNull(result.getNested());
        assertNotNull(result.getNested().getOne());
        assertEquals(protoTestObjectWithNestedProtoObject.getOne(), result.getOne());
        assertEquals(protoTestObjectWithNestedProtoObject.getNestedObject().getOne(), result.getNested().getOne());
    }

    @Test
    public void canEnumProtoFieldToProto() {
        ObjectWithEnumField objectWithEnumField = new ObjectWithEnumField();
        objectWithEnumField.setEnumField(SimpleEnum.VALUE1);

        ProtoObjectWithEnumField result = mapper.map(objectWithEnumField, ProtoObjectWithEnumField.class);

        assertNotNull(result);
        assertNotNull(result.getEnumField());
        assertEquals(objectWithEnumField.getEnumField().name(), result.getEnumField().name());
    }

    @Test
    public void canEnumProtoFieldFromProto() {
        ProtoObjectWithEnumField.Builder protoObjectWithEnumFieldBuilder = ProtoObjectWithEnumField.newBuilder();
        protoObjectWithEnumFieldBuilder.setEnumField(ProtoEnum.VALUE1);

        ProtoObjectWithEnumField protoObjectWithEnumField = protoObjectWithEnumFieldBuilder.build();

        ObjectWithEnumField result = mapper.map(protoObjectWithEnumField, ObjectWithEnumField.class);

        assertNotNull(result);
        assertNotNull(result.getEnumField());
        assertEquals(protoObjectWithEnumField.getEnumField().name(), result.getEnumField().name());
    }

    @Test
    public void canRepeatedFieldToProto() {
        TestObject testObject = new TestObject();
        testObject.setOne("One");

        ObjectWithCollection objectWithCollection = new ObjectWithCollection();
        objectWithCollection.setObjects(Arrays.asList(testObject));

        ProtobufWithSimpleCollection result = mapper.map(objectWithCollection, ProtobufWithSimpleCollection.class);

        assertNotNull(result);
        assertNotNull(result.getObjectList());
        assertEquals(1, result.getObjectCount());
        assertNotNull(result.getObject(0));
        assertNotNull(result.getObject(0).getOne());
        assertEquals(objectWithCollection.getObjects().get(0).getOne(), result.getObject(0).getOne());
    }

    @Test
    public void canRepeatedFieldFromProto() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder.setOne("One");

        ProtobufWithSimpleCollection.Builder protobufWithSimpleCollectionBuilder = ProtobufWithSimpleCollection.newBuilder();
        protobufWithSimpleCollectionBuilder.addAllObject(Arrays.asList(simpleProtoTestObjectBuilder.build()));

        ProtobufWithSimpleCollection protobufWithSimpleCollection = protobufWithSimpleCollectionBuilder.build();

        ObjectWithCollection result = mapper.map(protobufWithSimpleCollection, ObjectWithCollection.class);

        assertNotNull(result);
        assertNotNull(result.getObjects());
        assertEquals(1, result.getObjects().size());
        assertNotNull(result.getObjects().get(0));
        assertNotNull(result.getObjects().get(0).getOne());
        assertEquals(protobufWithSimpleCollection.getObject(0).getOne(), result.getObjects().get(0).getOne());
    }

    @Test
    public void canRepeatedEnumFieldToProto() {
        ObjectWithEnumCollection objectWithEnumCollection = new ObjectWithEnumCollection();
        objectWithEnumCollection.setEnums(Arrays.asList(SimpleEnum.VALUE1));

        ProtobufWithEnumCollection result = mapper.map(objectWithEnumCollection, ProtobufWithEnumCollection.class);

        assertNotNull(result);
        assertNotNull(result.getObjectList());
        assertEquals(1, result.getObjectCount());
        assertNotNull(result.getObject(0));
        assertEquals(objectWithEnumCollection.getEnums().get(0).name(), result.getObject(0).name());
    }

    @Test
    public void canRepeatedEnumFieldFromProto() {
        ProtobufWithEnumCollection.Builder protobufWithEnumCollectionBuilder = ProtobufWithEnumCollection.newBuilder();
        protobufWithEnumCollectionBuilder.addAllObject(Arrays.asList(ProtoEnum.VALUE1));

        ProtobufWithEnumCollection protobufWithEnumCollection = protobufWithEnumCollectionBuilder.build();

        ObjectWithEnumCollection result = mapper.map(protobufWithEnumCollection, ObjectWithEnumCollection.class);

        assertNotNull(result);
        assertNotNull(result.getEnums());
        assertEquals(1, result.getEnums().size());
        assertNotNull(result.getEnums().get(0));
        assertEquals(protobufWithEnumCollection.getObject(0).name(), result.getEnums().get(0).name());
    }

    @Test
    public void canBadMapToProto() {
        badMapToProtoExpectedException.expect(MappingException.class);
        badMapToProtoExpectedException.expectMessage("Could not call map setter method putAllValue");
        badMapToProtoExpectedException.expectCause(IsInstanceOf.instanceOf(InvocationTargetException.class));

        MapExample mapExample = new MapExample();
        mapExample.put("test", null);
        mapExample.put("foo", "bar");

        mapper.map(mapExample, ProtobufMapExample.class);
    }

    @Test
    public void canMapToProto() {
        MapExample mapExample = new MapExample();
        mapExample.put("test", "value");

        ProtobufMapExample result = mapper.map(mapExample, ProtobufMapExample.class);

        assertNotNull(result);
        assertNotNull(result.getValueMap());
        assertEquals(mapExample.getValue(), result.getValueMap());
    }

    @Test
    public void canBadMapFromProto() {
        badMapFromProtoExpectedException.expect(NullPointerException.class);

        ProtobufMapExample.Builder protoMapExample = ProtobufMapExample.newBuilder();
        protoMapExample.putValue("test", null);
    }

    @Test
    public void canMapFromProto() {
        ProtobufMapExample.Builder protobufMapExampleBuilder = ProtobufMapExample.newBuilder();
        protobufMapExampleBuilder.putValue("test", "value");
        protobufMapExampleBuilder.putValue("foo", "bar");

        MapExample result = mapper.map(protobufMapExampleBuilder, MapExample.class);

        assertNotNull(result);
        assertNotNull(result.getValue());
        assertEquals(protobufMapExampleBuilder.getValueMap(), result.getValue());
    }

    @Test
    public void canSnakeCaseFieldFromProto() {
        ProtobufFieldNaming.Builder protobufFieldNamingBuilder = ProtobufFieldNaming.newBuilder();
        protobufFieldNamingBuilder.setSnakeCaseField("some value");

        ProtobufFieldNaming protobufFieldNaming = protobufFieldNamingBuilder.build();

        FieldNaming result = mapper.map(protobufFieldNaming, FieldNaming.class);

        assertNotNull(result);
        assertNotNull(result.getSnakeCaseField());
        assertEquals(protobufFieldNaming.getSnakeCaseField(), result.getSnakeCaseField());
    }

    @Test
    public void canSnakeCaseFieldToProto() {
        FieldNaming fieldNaming = new FieldNaming();
        fieldNaming.setSnakeCaseField("some value");

        ProtobufFieldNaming result = mapper.map(fieldNaming, ProtobufFieldNaming.class);

        assertNotNull(result);
        assertNotNull(result.getSnakeCaseField());
        assertEquals(fieldNaming.getSnakeCaseField(), result.getSnakeCaseField());
    }
}
