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
package org.dozer.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.ProtocolMessageEnum;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Dmitry Spikhalskiy
 */
public class ProtoUtils {
  public static Message.Builder getBuilder(Class<? extends Message> clazz) {
    final Message.Builder protoBuilder;
    try {
      Method newBuilderMethod = clazz.getMethod("newBuilder");
      protoBuilder = (Message.Builder)newBuilderMethod.invoke(null);
    } catch (Exception e) {
      MappingUtils.throwMappingException(e);
      return null;
    }

    return protoBuilder;
  }

  public static List<Descriptors.FieldDescriptor> getFieldDescriptors(Class<? extends Message> clazz) {
    Message.Builder protoBuilder = getBuilder(clazz);

    return getFieldDescriptors(protoBuilder);
  }

  public static List<Descriptors.FieldDescriptor> getFieldDescriptors(Message.Builder protoBuilder) {
    return protoBuilder.getDescriptorForType().getFields();
  }

  public static Descriptors.FieldDescriptor getFieldDescriptor(Class<? extends Message> clazz, String fieldName) {
    final List<Descriptors.FieldDescriptor> protoFieldDescriptors = getFieldDescriptors(clazz);

    Descriptors.FieldDescriptor foundDescriptor = null;
    for (Descriptors.FieldDescriptor descriptor : protoFieldDescriptors) {
      if (fieldName.equals(descriptor.getName())) {
        foundDescriptor = descriptor;
      }
    }

    return foundDescriptor;
  }

  public static Object getFieldValue(Message message, String fieldName) {
    Map<Descriptors.FieldDescriptor, Object> fieldsMap = message.getAllFields();
    for (Map.Entry<Descriptors.FieldDescriptor, Object> field : fieldsMap.entrySet()) {
      if (fieldName.equals(field.getKey().getName())) {
        return field.getValue();
      }
    }

    return null;
  }

  public static Class<?> getJavaClass(final Descriptors.FieldDescriptor descriptor) {
    if (descriptor.isRepeated()) return List.class;
    return getJavaClassIgnoreRepeated(descriptor);
  }

  public static Class<?> getJavaGenericClassForCollection(final Descriptors.FieldDescriptor descriptor) {
    if (!descriptor.isRepeated()) return null;
    return getJavaClassIgnoreRepeated(descriptor);
  }

  private static Class<?> getJavaClassIgnoreRepeated(final Descriptors.FieldDescriptor descriptor) {
    switch (descriptor.getJavaType()) {
      case INT        : return Integer.class;
      case LONG       : return Long.class;
      case FLOAT      : return Float.class;
      case DOUBLE     : return Double.class;
      case BOOLEAN    : return Boolean.class;
      case STRING     : return String.class;
      case BYTE_STRING: return ByteString.class;
      //code duplicate, but GenericDescriptor interface is private in protobuf
      case ENUM       : return getEnumClassByEnumDescriptor(descriptor.getEnumType());
      case MESSAGE    :
        return MappingUtils.loadClass(StringUtils.join(new String[]{
                descriptor.getMessageType().getFile().getOptions().getJavaPackage(),
                descriptor.getMessageType().getFile().getOptions().getJavaOuterClassname(),
                descriptor.getMessageType().getFullName()}, '.'));
      default         : throw new RuntimeException();
    }
  }

  private static Class<? extends Enum> getEnumClassByEnumDescriptor(Descriptors.EnumDescriptor descriptor) {
    return (Class<? extends Enum>)MappingUtils.loadClass(StringUtils.join(new String[]{
            descriptor.getFile().getOptions().getJavaPackage(),
            descriptor.getFile().getOptions().getJavaOuterClassname(),
            descriptor.getFullName()}, '.'));
  }

  public static Object wrapEnums(Object value) {
    if (value instanceof ProtocolMessageEnum) {
      return ((ProtocolMessageEnum) value).getValueDescriptor();
    }
    //there is no other collections using in proto, only list
    if (value instanceof List) {
      List modifiedList = new ArrayList(((List) value).size());
      for (Object element : (List)value) {
        modifiedList.add(wrapEnums(element));
      }
      return modifiedList;
    }
    return value;
  }

  public static Object unwrapEnums(Object value) {
    if (value instanceof Descriptors.EnumValueDescriptor) {
      Descriptors.EnumValueDescriptor descriptor = (Descriptors.EnumValueDescriptor)value;
      Class<? extends Enum> enumClass = getEnumClassByEnumDescriptor(descriptor.getType());
      Enum[] enumValues = enumClass.getEnumConstants();
      for (Enum enumValue : enumValues) {
        if (((Descriptors.EnumValueDescriptor) value).getName().equals(enumValue.name())) return enumValue;
      }
      return null;
    }
    if (value instanceof Collection) {
      List modifiedList = new ArrayList(((List) value).size());
      for (Object element : (List)value) {
        modifiedList.add(unwrapEnums(element));
      }
      return modifiedList;
    }
    return value;
  }
}
