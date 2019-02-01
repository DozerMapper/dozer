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
package com.github.dozermapper.protobuf.util;

import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.protobuf.vo.proto.FakeMessage;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ProtoUtilsTest {

    @Rule
    public ExpectedException handlesIncorrectMessageExpectedException = ExpectedException.none();

    @Test
    public void canGetBuilder() {
        Message.Builder result = ProtoUtils.getBuilder(ProtoTestObjects.ProtobufFieldNaming.class);

        assertNotNull(result);
    }

    @Test
    public void handlesIncorrectMessage() {
        handlesIncorrectMessageExpectedException.expect(MappingException.class);
        handlesIncorrectMessageExpectedException.expectMessage("Failed to create Message.Builder for com.github.dozermapper.protobuf.vo.proto.FakeMessage");
        handlesIncorrectMessageExpectedException.expectCause(IsInstanceOf.instanceOf(NoSuchMethodException.class));

        ProtoUtils.getBuilder(FakeMessage.class);
    }

    @Test
    public void getJavaGenericClassForCollectionHandlesNonCollections() {
        Descriptors.FieldDescriptor fieldDescriptor = ProtoUtils.getFieldDescriptor(ProtoTestObjects.SimpleProtoTestObjectWithoutRequired.class, "one");

        assertNull(ProtoUtils.getJavaGenericClassForCollection(fieldDescriptor, new BeanContainer()));
    }
}
