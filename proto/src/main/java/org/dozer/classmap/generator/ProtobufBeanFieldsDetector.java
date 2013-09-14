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
package org.dozer.classmap.generator;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.dozer.util.ProtoUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Dmitry Spikhalskiy
 */
public class ProtobufBeanFieldsDetector implements BeanMappingGenerator.BeanFieldsDetector {
  public boolean accepts(Class<?> clazz) {
    return Message.class.isAssignableFrom(clazz);
  }

  public Set<String> getReadableFieldNames(Class<?> clazz) {
    return getFieldNames((Class<? extends Message>) clazz);
  }

  public Set<String> getWritableFieldNames(Class<?> clazz) {
    return getFieldNames((Class<? extends Message>) clazz);
  }

  private static Set<String> getFieldNames(Class<? extends Message> clazz) {
    Set<String> resultSet = new HashSet<String>();
    List<Descriptors.FieldDescriptor> descriptors = ProtoUtils.getFieldDescriptors(clazz);
    for (Descriptors.FieldDescriptor descriptor : descriptors) {
      resultSet.add(descriptor.getName());
    }
    return resultSet;
  }
}
