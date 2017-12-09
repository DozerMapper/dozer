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

import com.github.dozermapper.protobuf.vo.proto.NestedObject;
import com.github.dozermapper.protobuf.vo.proto.TestObject;
import com.github.dozermapper.protobuf.vo.protomultiple.SimpleProtoTestObject;
import org.dozer.Mapper;
import org.dozer.config.BeanContainer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProtoBeansMultipleFilesMappingTest extends ProtoAbstractTest {

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
    public void testSimpleToProto() {
        NestedObject source = new NestedObject();
        TestObject testObject = new TestObject();
        testObject.setOne("ABC");
        source.setNested(testObject);

        SimpleProtoTestObject protoResult = mapper.map(source, SimpleProtoTestObject.class);

        assertNotNull(protoResult);
        assertEquals("ABC", protoResult.getNested().getOne());
    }

    @Test
    public void testSimpleFromProto() {
        SimpleProtoTestObject.Builder builder = SimpleProtoTestObject.newBuilder();
        builder.getNestedBuilder().setOne("ABC");

        SimpleProtoTestObject source = builder.build();

        NestedObject pojoResult = mapper.map(source, NestedObject.class);

        assertNotNull(pojoResult);
        assertEquals("ABC", pojoResult.getNested().getOne());
    }
}
