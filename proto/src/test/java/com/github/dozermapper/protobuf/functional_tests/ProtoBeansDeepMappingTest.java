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
import com.github.dozermapper.protobuf.vo.proto.LiteTestObjectContainer;
import com.github.dozermapper.protobuf.vo.proto.ObjectWithCollection;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtoTestObjectWithNestedProtoObject;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufWithSimpleCollection;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufWithSimpleCollectionContainer;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.SimpleProtoTestObject;

import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProtoBeansDeepMappingTest extends ProtoAbstractTest {

    private Mapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = getMapper("mappings/protoSrcDeepBeansMapping.xml");
    }

    @Test
    public void protoSrcCopySimpleOneLevelField() {
        final String oneValue = "smthOne";

        ProtoTestObjectWithNestedProtoObject.Builder builder = ProtoTestObjectWithNestedProtoObject.newBuilder();
        SimpleProtoTestObject.Builder nestedObjectBuilder = SimpleProtoTestObject.newBuilder();
        nestedObjectBuilder.setOne(oneValue);
        builder.setNestedObject(nestedObjectBuilder);
        builder.setOne("smthAnother-neverMind");
        ProtoTestObjectWithNestedProtoObject src = builder.build();
        LiteTestObject result = mapper.map(src, LiteTestObject.class);
        assertEquals(oneValue, result.getOne());
    }

    @Test
    public void protoSrcCopyFieldFromListElement() {
        final String oneValue = "smthOne";

        ProtobufWithSimpleCollection.Builder builder = ProtobufWithSimpleCollection.newBuilder();
        SimpleProtoTestObject.Builder nestedObjectBuilder1 = SimpleProtoTestObject.newBuilder();
        nestedObjectBuilder1.setOne("smthAnother");
        SimpleProtoTestObject.Builder nestedObjectBuilder2 = SimpleProtoTestObject.newBuilder();
        nestedObjectBuilder2.setOne(oneValue);
        builder.addAllObject(Arrays.asList(nestedObjectBuilder1.build(), nestedObjectBuilder2.build()));
        ProtobufWithSimpleCollection src = builder.build();
        LiteTestObject result = mapper.map(src, LiteTestObject.class);
        assertEquals(oneValue, result.getOne());
    }

    @Test
    public void protoSrcCopyList() {
        final String oneValue = "smthOne";

        ProtobufWithSimpleCollectionContainer.Builder builder = ProtobufWithSimpleCollectionContainer.newBuilder();
        ProtobufWithSimpleCollection.Builder protoWithCollectionBuilder = ProtobufWithSimpleCollection.newBuilder();
        SimpleProtoTestObject.Builder nestedObjectBuilder1 = SimpleProtoTestObject.newBuilder();
        nestedObjectBuilder1.setOne("smthAnother");
        SimpleProtoTestObject.Builder nestedObjectBuilder2 = SimpleProtoTestObject.newBuilder();
        nestedObjectBuilder2.setOne(oneValue);
        protoWithCollectionBuilder.addAllObject(Arrays.asList(nestedObjectBuilder1.build(), nestedObjectBuilder2.build()));
        builder.setObject(protoWithCollectionBuilder);

        ProtobufWithSimpleCollectionContainer src = builder.build();
        ObjectWithCollection result = mapper.map(src, ObjectWithCollection.class);
        assertEquals(2, result.getObjects().size());
    }

    @Test
    public void protoSrcCopyListElement() {
        final String oneValue = "smthOne";

        ProtobufWithSimpleCollection.Builder builder = ProtobufWithSimpleCollection.newBuilder();
        SimpleProtoTestObject.Builder nestedObjectBuilder1 = SimpleProtoTestObject.newBuilder();
        nestedObjectBuilder1.setOne("smthAnother");
        SimpleProtoTestObject.Builder nestedObjectBuilder2 = SimpleProtoTestObject.newBuilder();
        nestedObjectBuilder2.setOne(oneValue);
        builder.addAllObject(Arrays.asList(nestedObjectBuilder1.build(), nestedObjectBuilder2.build()));
        ProtobufWithSimpleCollection src = builder.build();

        LiteTestObjectContainer result = mapper.map(src, LiteTestObjectContainer.class);
        assertEquals(oneValue, result.getObject().getOne());
    }

    @Test
    public void protoSrcCopyDeepListElement() {
        final String oneValue = "smthOne";

        ProtobufWithSimpleCollectionContainer.Builder srcBuilder = ProtobufWithSimpleCollectionContainer.newBuilder();
        ProtobufWithSimpleCollection.Builder innerBuilder = ProtobufWithSimpleCollection.newBuilder();
        SimpleProtoTestObject.Builder nestedObjectBuilder1 = SimpleProtoTestObject.newBuilder();
        nestedObjectBuilder1.setOne(oneValue);
        SimpleProtoTestObject.Builder nestedObjectBuilder2 = SimpleProtoTestObject.newBuilder();
        nestedObjectBuilder2.setOne("smthAnother");
        innerBuilder.addAllObject(Arrays.asList(nestedObjectBuilder1.build(), nestedObjectBuilder2.build()));
        srcBuilder.setObject(innerBuilder);

        LiteTestObjectContainer result = mapper.map(srcBuilder.build(), LiteTestObjectContainer.class);
        assertEquals(oneValue, result.getObject().getOne());
    }
}
