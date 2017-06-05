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
package org.dozer.propertydescriptor;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

import org.dozer.config.BeanContainer;
import org.dozer.factory.DestBeanCreator;
import org.dozer.fieldmap.HintContainer;
import org.dozer.util.MappingUtils;
import org.dozer.util.ProtoUtils;

/**
 * @author Dmitry Spikhalskiy
 */
public class ProtoFieldPropertyDescriptorCreationStrategy implements PropertyDescriptorCreationStrategy {

  private final BeanContainer beanContainer;
  private final DestBeanCreator destBeanCreator;

  public ProtoFieldPropertyDescriptorCreationStrategy(BeanContainer beanContainer, DestBeanCreator destBeanCreator) {
    this.beanContainer = beanContainer;
    this.destBeanCreator = destBeanCreator;
  }

  @Override
  public DozerPropertyDescriptor buildFor(Class<?> clazz, String fieldName, boolean isIndexed, int index, HintContainer srcDeepIndexHintContainer, HintContainer destDeepIndexHintContainer) {
    return new ProtoFieldPropertyDescriptor(clazz, fieldName, isIndexed, index, srcDeepIndexHintContainer, destDeepIndexHintContainer, beanContainer, destBeanCreator);
  }

  @Override
  public boolean isApplicable(Class<?> clazz, String fieldName) {
    if (!Message.class.isAssignableFrom(clazz)) {
      return false;
    }

    Class<? extends Message> messageClass = (Class<? extends Message>)clazz;
    Descriptors.FieldDescriptor foundDescriptor = ProtoUtils.getFieldDescriptor(messageClass, fieldName);

    return foundDescriptor != null || MappingUtils.isDeepMapping(fieldName);
  }
}
