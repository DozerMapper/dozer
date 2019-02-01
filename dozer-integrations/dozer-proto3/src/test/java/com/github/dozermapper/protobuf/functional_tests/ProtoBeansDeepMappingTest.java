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

import java.util.Arrays;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.protobuf.vo.proto.LiteTestObject;
import com.github.dozermapper.protobuf.vo.proto.LiteTestObjectContainer;
import com.github.dozermapper.protobuf.vo.proto.ObjectWithCollection;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtoTestObjectWithNestedProtoObject;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufWithSimpleCollection;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufWithSimpleCollectionContainer;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.SimpleProtoTestObject;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProtoBeansDeepMappingTest {

    private static Mapper mapper;

    @BeforeClass
    public static void setUp() {
        mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/protoSrcDeepBeansMapping.xml")
                .build();
    }

    @Test
    public void canSrcCopySimpleOneLevelField() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder.setOne("smthOne");

        ProtoTestObjectWithNestedProtoObject.Builder protoTestObjectWithNestedProtoObjectBuilder = ProtoTestObjectWithNestedProtoObject.newBuilder();
        protoTestObjectWithNestedProtoObjectBuilder.setNestedObject(simpleProtoTestObjectBuilder);
        protoTestObjectWithNestedProtoObjectBuilder.setOne("smthAnother-neverMind");

        ProtoTestObjectWithNestedProtoObject protoTestObjectWithNestedProtoObject = protoTestObjectWithNestedProtoObjectBuilder.build();

        LiteTestObject result = mapper.map(protoTestObjectWithNestedProtoObject, LiteTestObject.class);

        assertNotNull(result);
        assertNotNull(result.getOne());
        assertEquals(protoTestObjectWithNestedProtoObject.getNestedObject().getOne(), result.getOne());
    }

    @Test
    public void canSrcCopyFieldFromListElement() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder1 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder1.setOne("smthAnother");

        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder2 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder2.setOne("smthOne");

        ProtobufWithSimpleCollection.Builder protobufWithSimpleCollectionBuilder = ProtobufWithSimpleCollection.newBuilder();
        protobufWithSimpleCollectionBuilder.addAllObject(Arrays.asList(simpleProtoTestObjectBuilder1.build(), simpleProtoTestObjectBuilder2.build()));

        ProtobufWithSimpleCollection protobufWithSimpleCollection = protobufWithSimpleCollectionBuilder.build();

        LiteTestObject result = mapper.map(protobufWithSimpleCollection, LiteTestObject.class);

        assertNotNull(result);
        assertNotNull(result.getOne());
        assertEquals(protobufWithSimpleCollection.getObject(1).getOne(), result.getOne());
    }

    @Test
    public void canSrcCopyList() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder1 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder1.setOne("smthAnother");

        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder2 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder2.setOne("smthOne");

        ProtobufWithSimpleCollection.Builder protoWithCollectionBuilder = ProtobufWithSimpleCollection.newBuilder();
        protoWithCollectionBuilder.addAllObject(Arrays.asList(simpleProtoTestObjectBuilder1.build(), simpleProtoTestObjectBuilder2.build()));

        ProtobufWithSimpleCollectionContainer.Builder protobufWithSimpleCollectionContainerBuilder = ProtobufWithSimpleCollectionContainer.newBuilder();
        protobufWithSimpleCollectionContainerBuilder.setObject(protoWithCollectionBuilder);

        ProtobufWithSimpleCollectionContainer src = protobufWithSimpleCollectionContainerBuilder.build();

        ObjectWithCollection result = mapper.map(src, ObjectWithCollection.class);

        assertNotNull(result);
        assertNotNull(result.getObjects());
        assertEquals(2, result.getObjects().size());
    }

    @Test
    public void canSrcCopyListElement() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder1 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder1.setOne("smthAnother");

        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder2 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder2.setOne("smthOne");

        ProtobufWithSimpleCollection.Builder builder = ProtobufWithSimpleCollection.newBuilder();
        builder.addAllObject(Arrays.asList(simpleProtoTestObjectBuilder1.build(), simpleProtoTestObjectBuilder2.build()));

        ProtobufWithSimpleCollection protobufWithSimpleCollection = builder.build();

        LiteTestObjectContainer result = mapper.map(protobufWithSimpleCollection, LiteTestObjectContainer.class);

        assertNotNull(result);
        assertNotNull(result.getObject());
        assertNotNull(result.getObject().getOne());
        assertEquals(protobufWithSimpleCollection.getObject(1).getOne(), result.getObject().getOne());
    }

    @Test
    public void canSrcCopyDeepListElement() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder1 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder1.setOne("smthOne");

        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder2 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder2.setOne("smthAnother");

        ProtobufWithSimpleCollection.Builder protobufWithSimpleCollectionBuilder = ProtobufWithSimpleCollection.newBuilder();
        protobufWithSimpleCollectionBuilder.addAllObject(Arrays.asList(simpleProtoTestObjectBuilder1.build(), simpleProtoTestObjectBuilder2.build()));

        ProtobufWithSimpleCollectionContainer.Builder protobufWithSimpleCollectionContainerBuilder = ProtobufWithSimpleCollectionContainer.newBuilder();
        protobufWithSimpleCollectionContainerBuilder.setObject(protobufWithSimpleCollectionBuilder);

        ProtobufWithSimpleCollectionContainer protobufWithSimpleCollectionContainer = protobufWithSimpleCollectionContainerBuilder.build();

        LiteTestObjectContainer result = mapper.map(protobufWithSimpleCollectionContainer, LiteTestObjectContainer.class);

        assertNotNull(result);
        assertNotNull(result.getObject().getOne());
        assertEquals(protobufWithSimpleCollectionContainer.getObject().getObject(0).getOne(), result.getObject().getOne());
    }
}
