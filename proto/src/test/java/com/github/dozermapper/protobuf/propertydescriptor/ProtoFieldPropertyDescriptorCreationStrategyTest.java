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
package com.github.dozermapper.protobuf.propertydescriptor;

import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects;

import org.dozer.config.BeanContainer;
import org.dozer.factory.DestBeanCreator;
import org.dozer.propertydescriptor.PropertyDescriptorFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ProtoFieldPropertyDescriptorCreationStrategyTest {

    private ProtoFieldPropertyDescriptorCreationStrategy strategy;

    @Before
    public void setUp() throws Exception {
        BeanContainer beanContainer = new BeanContainer();
        DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);
        strategy = new ProtoFieldPropertyDescriptorCreationStrategy(beanContainer, destBeanCreator, new PropertyDescriptorFactory());
    }

    @Test
    public void isApplicableTrueIfMessageAndDeepMapping() {
        assertTrue(strategy.isApplicable(
            /*any proto message class*/ProtoTestObjects.SimpleProtoTestObject.class,
            "deep.mapping"));
    }
}
