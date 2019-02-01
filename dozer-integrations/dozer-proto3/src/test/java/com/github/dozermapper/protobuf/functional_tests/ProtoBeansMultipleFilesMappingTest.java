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

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.protobuf.vo.proto.TestContainerObject;
import com.github.dozermapper.protobuf.vo.proto.NestedObject;
import com.github.dozermapper.protobuf.vo.proto.TestObject;
import com.github.dozermapper.protobuf.vo.proto.TestContainerObject.TestContainedEnum;
import com.github.dozermapper.protobuf.vo.protomultiple.ContainerObject;
import com.github.dozermapper.protobuf.vo.protomultiple.ContainerObject.ContainedEnum;
import com.github.dozermapper.protobuf.vo.protomultiple.SimpleProtoTestObject;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProtoBeansMultipleFilesMappingTest {

    private static Mapper mapper;

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

        NestedObject nestedObject = new NestedObject();
        nestedObject.setNested(testObject);

        SimpleProtoTestObject result = mapper.map(nestedObject, SimpleProtoTestObject.class);

        assertNotNull(result);
        assertNotNull(result.getNested());
        assertNotNull(result.getNested().getOne());
        assertEquals(nestedObject.getNested().getOne(), result.getNested().getOne());
    }

    @Test
    public void canSimpleFromProto() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder.getNestedBuilder().setOne("ABC");

        SimpleProtoTestObject simpleProtoTestObject = simpleProtoTestObjectBuilder.build();

        NestedObject result = mapper.map(simpleProtoTestObject, NestedObject.class);

        assertNotNull(result);
        assertNotNull(result.getNested());
        assertNotNull(result.getNested().getOne());
        assertEquals(simpleProtoTestObject.getNested().getOne(), result.getNested().getOne());
    }
    
    @Test
    public void canNestedObjectToProto() {
        TestContainerObject containerObject = new TestContainerObject();
        containerObject.setEnum1(TestContainedEnum.VALUE2);
        containerObject.setObj1(new TestContainerObject.TestContainedObject());
        containerObject.getObj1().setC1("ABC");
        
        ContainerObject result = mapper.map(containerObject, ContainerObject.class);
        assertNotNull(result);
        assertNotNull(result.getEnum1());
        assertNotNull(result.getObj1());
        assertNotNull(result.getObj1().getC1());
        assertEquals(containerObject.getObj1().getC1(), result.getObj1().getC1());
        assertEquals(containerObject.getEnum1().name(), result.getEnum1().name());
    }

    @Test
    public void canNestedObjectFromProto() {
        ContainerObject.Builder containerBuiler = ContainerObject.newBuilder();
        containerBuiler.setEnum1(ContainedEnum.VALUE2);
        containerBuiler.setObj1(ContainerObject.ContainedObject.newBuilder().setC1("ABC").build());
        ContainerObject container = containerBuiler.build();
        TestContainerObject result = mapper.map(container, TestContainerObject.class);
        assertNotNull(result);
        assertNotNull(result.getEnum1());
        assertNotNull(result.getObj1());
        assertNotNull(result.getObj1().getC1());
        assertEquals(container.getObj1().getC1(), result.getObj1().getC1());
        assertEquals(container.getEnum1().name(), result.getEnum1().name());
        
        
    }
}
