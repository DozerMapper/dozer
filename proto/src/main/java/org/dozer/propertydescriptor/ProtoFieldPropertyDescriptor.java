/*
 * Copyright 2005-2012 the original author or authors.
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
package org.dozer.propertydescriptor;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.dozer.BeanBuilder;
import org.dozer.builder.ProtoBeanBuilder;
import org.dozer.fieldmap.FieldMap;
import org.dozer.fieldmap.HintContainer;
import org.dozer.propertydescriptor.deep.DeepHierarchyUtils;
import org.dozer.util.MappingUtils;
import org.dozer.util.ProtoUtils;

/**
 * @author Dmitry Spikhalskiy
 */
public class ProtoFieldPropertyDescriptor extends AbstractPropertyDescriptor {
  private Descriptors.FieldDescriptor fieldDescriptor;

  public ProtoFieldPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index, HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer) {
    super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
    this.fieldDescriptor = ProtoUtils.getFieldDescriptor((Class<? extends Message>) clazz, fieldName);
    if (this.fieldDescriptor == null) {
      MappingUtils.throwMappingException("No field descriptor for field with name: " + fieldName);
    }
  }

  @Override
  public Class<?> getPropertyType() {
    return ProtoUtils.getJavaClass(fieldDescriptor);
  }

  @Override
  public Object getPropertyValue(Object bean) {
    Object result;
    if (MappingUtils.isDeepMapping(fieldName)) {
      result = DeepHierarchyUtils.getDeepSrcFieldValue(bean, fieldName, isIndexed, index, srcDeepIndexHintContainer);
    } else {
      result = getSimplePropertyValue(bean);
      if (isIndexed) {
        result = MappingUtils.getIndexedValue(result, index);
      }
    }
    return result;
  }

  private Object getSimplePropertyValue(Object bean) {
    //proto builder can't contains already created object and even if contain - it's fields can't be changed
    if (bean instanceof BeanBuilder) return null;
    if (!(bean instanceof Message)) {
      MappingUtils.throwMappingException("Try to pass non proto object to ProtoFieldPropertyDescriptor");
    }
    Message message = (Message)bean;

    Object value = ProtoUtils.getFieldValue(message, fieldName);
    return ProtoUtils.unwrapEnums(value);

  }

  @Override
  public void setPropertyValue(Object bean, Object value, FieldMap fieldMap) {
    if (!(bean instanceof ProtoBeanBuilder)) MappingUtils.throwMappingException("should be a ProtoBeanBuilder instance");
    ProtoBeanBuilder builder = (ProtoBeanBuilder)bean;

    value = ProtoUtils.wrapEnums(value);
    if (value != null) {
      builder.internalProtoBuilder().setField(fieldDescriptor, value);
    } else {
      builder.internalProtoBuilder().clearField(fieldDescriptor);
    }
}

  @Override
  public Class<?> genericType() {
    return ProtoUtils.getJavaGenericClassForCollection(fieldDescriptor);
  }
}
