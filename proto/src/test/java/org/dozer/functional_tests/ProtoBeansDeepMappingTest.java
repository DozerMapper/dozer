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
import org.dozer.vo.proto.LiteTestObject;
import org.dozer.vo.proto.LiteTestObjectContainer;
import org.dozer.vo.proto.ObjectWithCollection;
import org.dozer.vo.proto.ProtoTestObjects.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Dmitry Spikhalskiy
 */
public class ProtoBeansDeepMappingTest extends ProtoAbstractTest {
  private DozerBeanMapper mapper;

  @Before
  public void setUp() throws Exception {
    mapper = getMapper("protoSrcDeepBeansMapping.xml");
  }

  @Test
  public void protoSrc_copySimpleOneLevelField() {
    final String ONE_VALUE = "smthOne";

    ProtoTestObjectWithNestedProtoObject.Builder builder = ProtoTestObjectWithNestedProtoObject.newBuilder();
    SimpleProtoTestObject.Builder nestedObjectBuilder = SimpleProtoTestObject.newBuilder();
    nestedObjectBuilder.setOne(ONE_VALUE);
    builder.setNestedObject(nestedObjectBuilder);
    builder.setOne("smthAnother-neverMind");
    ProtoTestObjectWithNestedProtoObject src = builder.build();
    LiteTestObject result = mapper.map(src, LiteTestObject.class);
    assertEquals(ONE_VALUE, result.getOne());
  }

  @Test
  public void protoSrc_copyFieldFromListElement() {
    final String ONE_VALUE = "smthOne";

    ProtobufWithSimpleCollection.Builder builder = ProtobufWithSimpleCollection.newBuilder();
    SimpleProtoTestObject.Builder nestedObjectBuilder1 = SimpleProtoTestObject.newBuilder();
    nestedObjectBuilder1.setOne("smthAnother");
    SimpleProtoTestObject.Builder nestedObjectBuilder2 = SimpleProtoTestObject.newBuilder();
    nestedObjectBuilder2.setOne(ONE_VALUE);
    builder.addAllObject(Arrays.asList(nestedObjectBuilder1.build(), nestedObjectBuilder2.build()));
    ProtobufWithSimpleCollection src = builder.build();
    LiteTestObject result = mapper.map(src, LiteTestObject.class);
    assertEquals(ONE_VALUE, result.getOne());
  }

  @Test
  public void protoSrc_copyList() {
    final String ONE_VALUE = "smthOne";

    ProtobufWithSimpleCollectionContainer.Builder builder = ProtobufWithSimpleCollectionContainer.newBuilder();
    ProtobufWithSimpleCollection.Builder protoWithCollectionBuilder = ProtobufWithSimpleCollection.newBuilder();
    SimpleProtoTestObject.Builder nestedObjectBuilder1 = SimpleProtoTestObject.newBuilder();
    nestedObjectBuilder1.setOne("smthAnother");
    SimpleProtoTestObject.Builder nestedObjectBuilder2 = SimpleProtoTestObject.newBuilder();
    nestedObjectBuilder2.setOne(ONE_VALUE);
    protoWithCollectionBuilder.addAllObject(Arrays.asList(nestedObjectBuilder1.build(), nestedObjectBuilder2.build()));
    builder.setObject(protoWithCollectionBuilder);

    ProtobufWithSimpleCollectionContainer src = builder.build();
    ObjectWithCollection result = mapper.map(src, ObjectWithCollection.class);
    assertEquals(2, result.getObjects().size());
  }

  @Test
  public void protoSrc_copyListElement() {
    final String ONE_VALUE = "smthOne";

    ProtobufWithSimpleCollection.Builder builder = ProtobufWithSimpleCollection.newBuilder();
    SimpleProtoTestObject.Builder nestedObjectBuilder1 = SimpleProtoTestObject.newBuilder();
    nestedObjectBuilder1.setOne("smthAnother");
    SimpleProtoTestObject.Builder nestedObjectBuilder2 = SimpleProtoTestObject.newBuilder();
    nestedObjectBuilder2.setOne(ONE_VALUE);
    builder.addAllObject(Arrays.asList(nestedObjectBuilder1.build(), nestedObjectBuilder2.build()));
    ProtobufWithSimpleCollection src = builder.build();

    LiteTestObjectContainer result = mapper.map(src, LiteTestObjectContainer.class);
    assertEquals(ONE_VALUE, result.getObject().getOne());
  }

  @Test
  public void protoSrc_copyDeepListElement() {
    final String ONE_VALUE = "smthOne";

    ProtobufWithSimpleCollectionContainer.Builder srcBuilder = ProtobufWithSimpleCollectionContainer.newBuilder();
    ProtobufWithSimpleCollection.Builder innerBuilder = ProtobufWithSimpleCollection.newBuilder();
    SimpleProtoTestObject.Builder nestedObjectBuilder1 = SimpleProtoTestObject.newBuilder();
    nestedObjectBuilder1.setOne(ONE_VALUE);
    SimpleProtoTestObject.Builder nestedObjectBuilder2 = SimpleProtoTestObject.newBuilder();
    nestedObjectBuilder2.setOne("smthAnother");
    innerBuilder.addAllObject(Arrays.asList(nestedObjectBuilder1.build(), nestedObjectBuilder2.build()));
    srcBuilder.setObject(innerBuilder);

    LiteTestObjectContainer result = mapper.map(srcBuilder.build(), LiteTestObjectContainer.class);
    assertEquals(ONE_VALUE, result.getObject().getOne());
  }
}