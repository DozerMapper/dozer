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
package org.dozer.propertydescriptor;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.dozer.BeanBuilder;
import org.dozer.builder.ProtoBeanBuilder;
import org.dozer.fieldmap.FieldMap;
import org.dozer.fieldmap.HintContainer;
import org.dozer.util.DeepHierarchyUtils;
import org.dozer.util.MappingUtils;
import org.dozer.util.ProtoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dmitry Spikhalskiy
 */
public class ProtoFieldPropertyDescriptor extends AbstractPropertyDescriptor {
  private final Logger logger = LoggerFactory.getLogger(ProtoFieldPropertyDescriptor.class);

  public ProtoFieldPropertyDescriptor(Class<?> clazz, String fieldName, boolean isIndexed, int index, HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer) {
    super(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer);
  }

  private Class<?> _propertyType;

  @Override
  public Class<?> getPropertyType() {
    if (this._propertyType == null) {

      Class<?> result;
      if (MappingUtils.isDeepMapping(fieldName)) {
        try {
          result = DeepHierarchyUtils.getDeepFieldType(clazz, fieldName, srcDeepIndexHintContainer);
        } catch (Exception ignore) {
          logger.info("Determine field type by srcDeepIndexHintContainer failed");
          try {
            result = DeepHierarchyUtils.getDeepFieldType(clazz, fieldName, destDeepIndexHintContainer);
          } catch (Exception secondIgnore) {
            logger.info("Determine field type by destDeepIndexHintContainer failed");
            result = null;
          }
        }
      } else {
        result = ProtoUtils.getJavaClass(getFieldDescriptor());
      }

      this._propertyType = result;
    }

    return this._propertyType;
  }

  @Override
  public Object getPropertyValue(Object bean) {
    Object result;
    if (MappingUtils.isDeepMapping(fieldName)) {
      result = DeepHierarchyUtils.getDeepFieldValue(bean, fieldName, isIndexed, index, srcDeepIndexHintContainer);
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
      builder.internalProtoBuilder().setField(getFieldDescriptor(), value);
    } else {
      builder.internalProtoBuilder().clearField(getFieldDescriptor());
    }
  }

  private Class<?> _genericType;

  @Override
  public Class<?> genericType() {
    if (this._genericType == null) {

      Class<?> result;
      if (MappingUtils.isDeepMapping(fieldName)) {
        try {
          result = DeepHierarchyUtils.getDeepGenericType(clazz, fieldName, srcDeepIndexHintContainer);
        } catch (Exception ignore) {
          logger.info("Determine field generic type by srcDeepIndexHintContainer failed");
          try {
            result = DeepHierarchyUtils.getDeepGenericType(clazz, fieldName, destDeepIndexHintContainer);
          } catch (Exception secondIgnore) {
            logger.info("Determine field generic type by destDeepIndexHintContainer failed");
            result = null;
          }
        }
      } else {
        result = ProtoUtils.getJavaGenericClassForCollection(getFieldDescriptor());
      }

      this._genericType = result;
    }

    return this._genericType;
  }

  private Descriptors.FieldDescriptor _fieldDescriptor;

  private Descriptors.FieldDescriptor getFieldDescriptor() {
    if (_fieldDescriptor == null) {
      this._fieldDescriptor = ProtoUtils.getFieldDescriptor((Class<? extends Message>) clazz, fieldName);
      if (this._fieldDescriptor == null && !MappingUtils.isDeepMapping(fieldName)) {
        MappingUtils.throwMappingException("No field descriptor for field with name: " + fieldName);
      }
    }

    return _fieldDescriptor;
  }

}
