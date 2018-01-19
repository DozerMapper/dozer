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
package com.github.dozermapper.protobuf.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.ProtocolMessageEnum;

import org.apache.commons.lang3.StringUtils;
import org.dozer.config.BeanContainer;
import org.dozer.util.MappingUtils;

/**
 * Protobuf utility methods
 */
public final class ProtoUtils {

    private ProtoUtils() {
    }

    /**
     * Gets the {@link Message.Builder} instance associated with the clazz
     *
     * @param clazz {@link Message} clazz
     * @return {@link Message.Builder} instance associated with the clazz
     */
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

    /**
     * Gets a list of {@link Descriptors.FieldDescriptor} associated with the clazz
     *
     * @param clazz {@link Message} clazz
     * @return list of {@link Descriptors.FieldDescriptor} associated with the clazz
     */
    public static List<Descriptors.FieldDescriptor> getFieldDescriptors(Class<? extends Message> clazz) {
        Message.Builder protoBuilder = getBuilder(clazz);

        return getFieldDescriptors(protoBuilder);
    }

    /**
     * Gets a list of {@link Descriptors.FieldDescriptor} associated with the {@link Message.Builder}
     *
     * @param protoBuilder {@link Message.Builder} instance
     * @return list of {@link Descriptors.FieldDescriptor} associated with the {@link Message.Builder}
     */
    public static List<Descriptors.FieldDescriptor> getFieldDescriptors(Message.Builder protoBuilder) {
        return protoBuilder.getDescriptorForType().getFields();
    }

    /**
     * Returns the first FieldDescriptor matching the specified field name, either with an exact match,
     * or after applying a transformation to camel-case.  Returns null if there is no match.
     *
     * @param clazz     clazz to look up
     * @param fieldName field to look up
     * @return field descriptor or null if none found
     */

    /**
     * Gets a {@link Descriptors.FieldDescriptor} associated with the clazz, which matches the fieldName,
     * either with an exact match, or after applying a transformation to camel-case.
     * <p>
     * Returns null if there is no match.
     *
     * @param clazz     {@link Message} clazz
     * @param fieldName fieldName to find
     * @return {@link Descriptors.FieldDescriptor} associated with the clazz and which matches the fieldName
     */
    public static Descriptors.FieldDescriptor getFieldDescriptor(Class<? extends Message> clazz, String fieldName) {
        final List<Descriptors.FieldDescriptor> protoFieldDescriptors = getFieldDescriptors(clazz);

        for (Descriptors.FieldDescriptor descriptor : protoFieldDescriptors) {
            if (sameField(fieldName, descriptor.getName())) {
                return descriptor;
            }
        }

        return null;
    }

    private static boolean sameField(String fieldName, String protoFieldName) {
        if (fieldName.equals(protoFieldName)) {
            return true;
        }

        // Try to compare field name with Protobuf official snake case syntax
        return fieldName.equals(toCamelCase(protoFieldName));
    }

    /**
     * Gets the field value from the {@link Message}, which matches the fieldName,
     * either with an exact match, or after applying a transformation to camel-case.
     * <p>
     * Returns null if there is no match.
     *
     * @param message   {@link Message} instance
     * @param fieldName fieldName to find
     * @return field value from the {@link Message} for the specified field name, or null if none found
     */
    public static Object getFieldValue(Object message, String fieldName) {
        Map<Descriptors.FieldDescriptor, Object> fieldsMap = ((Message)message).getAllFields();
        for (Map.Entry<Descriptors.FieldDescriptor, Object> field : fieldsMap.entrySet()) {
            if (sameField(fieldName, field.getKey().getName())) {
                if (field.getKey().isMapField()) {
                    //capitalize the first letter of the string;
                    String propertyName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                    String methodName = String.format("get%sMap", propertyName);

                    try {
                        Method mapGetter = message.getClass().getMethod(methodName);
                        return mapGetter.invoke(message);
                    } catch (Exception e) {
                        throw new RuntimeException("Could not introspect map field with method " + methodName, e);
                    }
                }

                return field.getValue();
            }
        }

        return null;
    }

    /**
     * Gets the class type of the {@link Descriptors.FieldDescriptor}
     *
     * @param descriptor    {@link Descriptors.FieldDescriptor} instance
     * @param beanContainer {@link BeanContainer} instance
     * @return class type of the {@link Descriptors.FieldDescriptor}
     */
    public static Class<?> getJavaClass(final Descriptors.FieldDescriptor descriptor, BeanContainer beanContainer) {
        if (descriptor.isMapField()) {
            return Map.class;
        }

        if (descriptor.isRepeated()) {
            return List.class;
        }

        return getJavaClassIgnoreRepeated(descriptor, beanContainer);
    }

    /**
     * Gets the class type of the {@link Descriptors.FieldDescriptor}
     *
     * @param descriptor    {@link Descriptors.FieldDescriptor} instance
     * @param beanContainer {@link BeanContainer} instance
     * @return class type of the {@link Descriptors.FieldDescriptor} or null, if {@link Descriptors.FieldDescriptor#isRepeated()}
     */
    public static Class<?> getJavaGenericClassForCollection(final Descriptors.FieldDescriptor descriptor, BeanContainer beanContainer) {
        if (!descriptor.isRepeated()) {
            return null;
        }

        return getJavaClassIgnoreRepeated(descriptor, beanContainer);
    }

    private static Class<?> getJavaClassIgnoreRepeated(final Descriptors.FieldDescriptor descriptor, BeanContainer beanContainer) {
        switch (descriptor.getJavaType()) {
            case INT:
                return Integer.class;
            case LONG:
                return Long.class;
            case FLOAT:
                return Float.class;
            case DOUBLE:
                return Double.class;
            case BOOLEAN:
                return Boolean.class;
            case STRING:
                return String.class;
            case BYTE_STRING:
                return ByteString.class;
            //code duplicate, but GenericDescriptor interface is private in protobuf
            case ENUM:
                return getEnumClassByEnumDescriptor(descriptor.getEnumType(), beanContainer);
            case MESSAGE:
                return MappingUtils.loadClass(StringUtils.join(
                        getFullyQualifiedClassName(descriptor.getMessageType().getFile().getOptions(), descriptor.getMessageType().getName()), '.'), beanContainer);
            default:
                throw new RuntimeException();
        }
    }

    private static String[] getFullyQualifiedClassName(DescriptorProtos.FileOptions options, String name) {
        if (options.getJavaMultipleFiles()) {
            return new String[]{options.getJavaPackage(), name};
        }

        return new String[] {options.getJavaPackage(), options.getJavaOuterClassname(), name};
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Enum> getEnumClassByEnumDescriptor(Descriptors.EnumDescriptor descriptor, BeanContainer beanContainer) {
        String name = StringUtils.join(getFullyQualifiedClassName(descriptor.getFile().getOptions(), descriptor.getName()), '.');
        return (Class<? extends Enum>)MappingUtils.loadClass(name, beanContainer);
    }

    /**
     * Wrap {@link ProtocolMessageEnum} or a {@link List} to a {@link Descriptors.EnumValueDescriptor}
     * If the value is neither {@link ProtocolMessageEnum} or a {@link List}, the value is returned.
     *
     * @param value {@link ProtocolMessageEnum} or a {@link List}
     * @return {@link Descriptors.EnumValueDescriptor} if value is {@link ProtocolMessageEnum}, else a {@link List} of {@link Descriptors.EnumValueDescriptor}
     */
    @SuppressWarnings("unchecked")
    public static Object wrapEnums(Object value) {
        if (value instanceof ProtocolMessageEnum) {
            return ((ProtocolMessageEnum)value).getValueDescriptor();
        }

        //there is no other collections using in proto, only list
        if (value instanceof List) {
            List modifiedList = new ArrayList(((List)value).size());
            for (Object element : (List)value) {
                modifiedList.add(wrapEnums(element));
            }
            return modifiedList;
        }

        return value;
    }

    /**
     * Unwrap {@link Descriptors.EnumValueDescriptor} or a {@link Collection} to a raw {@link Enum}
     * If the value is neither {@link Descriptors.EnumValueDescriptor} or a {@link Collection}, the value is returned.
     *
     * @param value         {@link Descriptors.EnumValueDescriptor} or a {@link Collection}
     * @param beanContainer {@link BeanContainer} instance
     * @return {@link Enum} if value is {@link Descriptors.EnumValueDescriptor}, else a {@link Collection} of {@link Enum}
     */
    @SuppressWarnings("unchecked")
    public static Object unwrapEnums(Object value, BeanContainer beanContainer) {
        if (value instanceof Descriptors.EnumValueDescriptor) {
            Descriptors.EnumValueDescriptor descriptor = (Descriptors.EnumValueDescriptor)value;
            Class<? extends Enum> enumClass = getEnumClassByEnumDescriptor(descriptor.getType(), beanContainer);

            for (Enum enumValue : enumClass.getEnumConstants()) {
                if (((Descriptors.EnumValueDescriptor)value).getName().equals(enumValue.name())) {
                    return enumValue;
                }
            }

            return null;
        }

        if (value instanceof Collection) {
            Collection valueCollection = (Collection)value;
            List modifiedList = new ArrayList(valueCollection.size());

            for (Object element : valueCollection) {
                modifiedList.add(unwrapEnums(element, beanContainer));
            }

            return modifiedList;
        }

        return value;
    }

    /**
     * Converts name to CamelCase
     *
     * @param name name to convert
     * @return converted name to CamelCase
     */
    public static String toCamelCase(String name) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
    }
}
