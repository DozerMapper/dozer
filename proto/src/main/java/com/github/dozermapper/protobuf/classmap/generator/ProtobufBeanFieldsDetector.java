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
package com.github.dozermapper.protobuf.classmap.generator;

import java.util.HashSet;
import java.util.Set;

import com.github.dozermapper.protobuf.util.ProtoUtils;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

import org.dozer.classmap.generator.BeanFieldsDetector;

/**
 * {@link BeanFieldsDetector} that accepts {@link Message}
 */
public class ProtobufBeanFieldsDetector implements BeanFieldsDetector {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accepts(Class<?> clazz) {
        return Message.class.isAssignableFrom(clazz);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getReadableFieldNames(Class<?> clazz) {
        return getFieldNames((Class<? extends Message>)clazz);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getWritableFieldNames(Class<?> clazz) {
        return getFieldNames((Class<? extends Message>)clazz);
    }

    private static Set<String> getFieldNames(Class<? extends Message> clazz) {
        Set<String> resultSet = new HashSet<>();
        for (Descriptors.FieldDescriptor descriptor : ProtoUtils.getFieldDescriptors(clazz)) {
            resultSet.add(ProtoUtils.toCamelCase(descriptor.getName()));
        }

        return resultSet;
    }
}
