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
package com.github.dozermapper.core.classmap;

import java.util.List;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.classmap.generator.BeanMappingGenerator;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.fieldmap.FieldMap;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.util.DozerConstants;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClassMapBuilderTest extends AbstractDozerTest {

    private ClassMapBuilder.CollectionMappingGenerator collectionMappingGenerator;
    private ClassMapBuilder.MapMappingGenerator mapMappingGenerator;
    private BeanMappingGenerator beanMappingGenerator;
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        BeanContainer beanContainer = new BeanContainer();
        DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);
        PropertyDescriptorFactory propertyDescriptorFactory = new PropertyDescriptorFactory();
        collectionMappingGenerator = new ClassMapBuilder.CollectionMappingGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory);
        mapMappingGenerator = new ClassMapBuilder.MapMappingGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory);
        beanMappingGenerator = new BeanMappingGenerator(beanContainer, destBeanCreator, propertyDescriptorFactory);

        configuration = new Configuration();
    }

    @Test
    public void shouldPrepareMappingsForCollection() {
        ClassMap classMap = new ClassMap(null);

        collectionMappingGenerator.apply(classMap, configuration);

        List<FieldMap> fieldMaps = classMap.getFieldMaps();
        assertEquals(1, fieldMaps.size());
        assertEquals(DozerConstants.SELF_KEYWORD, fieldMaps.get(0).getSrcFieldName());
        assertEquals(DozerConstants.SELF_KEYWORD, fieldMaps.get(0).getDestFieldName());
    }
}
