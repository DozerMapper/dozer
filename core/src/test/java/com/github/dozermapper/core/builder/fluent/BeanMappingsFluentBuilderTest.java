/*
 * Copyright 2005-2018 Dozer Project
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
package com.github.dozermapper.core.builder.fluent;

import java.util.List;

import com.github.dozermapper.core.builder.model.jaxb.FieldType;
import com.github.dozermapper.core.builder.model.jaxb.Relationship;
import com.github.dozermapper.core.builder.model.jaxb.Type;
import com.github.dozermapper.core.classmap.MappingFileData;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.el.DefaultELEngine;
import com.github.dozermapper.core.el.ELEngine;
import com.github.dozermapper.core.el.ELExpressionFactory;
import com.github.dozermapper.core.factory.DestBeanCreator;
import com.github.dozermapper.core.factory.JAXBBeanFactory;
import com.github.dozermapper.core.functional_tests.builder.DozerBuilderTest;
import com.github.dozermapper.core.functional_tests.support.TestCustomConverter;
import com.github.dozermapper.core.propertydescriptor.PropertyDescriptorFactory;
import com.github.dozermapper.core.vo.CustomDoubleObjectIF;
import com.github.dozermapper.core.vo.SimpleEnum;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BeanMappingsFluentBuilderTest {

    private class TestFluentBuilder extends BeanMappingsFluentBuilder {

        TestFluentBuilder(ELEngine elEngine) {
            super(elEngine);
        }

        @Override
        protected void configure() {
            //NOTE: The below is not a valid mapping - it is simply testing all options dont fail

            // @formatter:off
            configuration()
                .withAllowedExceptions()
                    .addException(RuntimeException.class.getName())
                .end()
                .withCopyByReferences()
                    .addCopyByReference(SimpleEnum.class.getName())
                .end()
                .withCustomConverters()
                    .withConverter()
                        .withType(TestCustomConverter.class.getName())
                        .withClassA()
                            .withClazz(CustomDoubleObjectIF.class.getName())
                        .endType()
                        .withClassB()
                            .withClazz(Double.class.getName())
                        .endType()
                    .end()
                .end()
                .withVariables()
                    .withVariable()
                        .withName("container")
                        .withClazz("com.github.dozermapper.core.functional_tests.VariablesTest$Container")
                    .end()
                .end()
                .withBeanFactory(JAXBBeanFactory.class.getName())
                .withMapNull(true)
                .withStopOnErrors(true)
                .withTrimStrings(true)
                .withWildcard(true)
                .withWildcardCaseInsensitive(false)
                .withMapEmptyString(true)
                .withDateFormat("MM/dd/yyyy HH:mm")
                .withRelationshipType(Relationship.fromValue("cumulative"))
                .end();
            // @formatter:on

            // @formatter:off
            mapping()
                .withMapNull(true)
                .withStopOnErrors(true)
                .withTrimStrings(true)
                .withWildcard(true)
                .withWildcardCaseInsensitive(false)
                .withMapEmptyString(true)
                .withDateFormat("MM/dd/yyyy HH:mm")
                .withMapId("A")
                .withType(Type.fromValue("one-way"))
                .withBeanFactory(JAXBBeanFactory.class.getName())
                .withRelationshipType(Relationship.CUMULATIVE)
                .withClassA()
                    .withClazz(DozerBuilderTest.Bean.class.getName())
                    .withAccessible(true)
                    .withBeanFactory(JAXBBeanFactory.class.getName())
                    .withCreateMethod("create")
                    .withFactoryBeanId("bean")
                    .withMapEmptyString(true)
                    .withMapNull(true)
                    .withMapGetMethod("get")
                    .withMapSetMethod("put")
                    .withSkipConstructor(false)
                .end()
                .withClassB()
                    .withClazz(DozerBuilderTest.Bean.class.getName())
                    .withAccessible(true)
                    .withBeanFactory(JAXBBeanFactory.class.getName())
                    .withCreateMethod("create")
                    .withFactoryBeanId("bean")
                    .withMapEmptyString(true)
                    .withMapNull(true)
                    .withMapGetMethod("get")
                    .withMapSetMethod("put")
                    .withSkipConstructor(false)
                .end()
                .withFieldExclude()
                    .withA()
                        .withName("excluded")
                        .withAccessible(true)
                        .withCreateMethod("create")
                        .withMapGetMethod("get")
                        .withMapSetMethod("put")
                        .withDateFormat("MM/dd/yyyy HH:mm")
                        .withKey("id")
                        .withType(FieldType.fromValue("generic"))
                        .withSetMethod("set")
                        .withGetMethod("get")
                    .end()
                    .withB()
                        .withName("excluded")
                        .withAccessible(true)
                        .withCreateMethod("create")
                        .withMapGetMethod("get")
                        .withMapSetMethod("put")
                        .withDateFormat("MM/dd/yyyy HH:mm")
                        .withKey("id")
                        .withType(FieldType.GENERIC)
                        .withSetMethod("set")
                        .withGetMethod("get")
                    .end()
                    .withType(Type.ONE_WAY)
                .end()
                .withField()
                    .withCopyByReference(true)
                    .withRemoveOrphans(true)
                    .withRelationshipType(Relationship.NON_CUMULATIVE)
                    .withAHint(String.class.getName())
                    .withBHint(Integer.class.getName())
                    .withType(Type.ONE_WAY)
                    .withMapId("A")
                    .withCustomConverterId("id")
                    .withADeepIndexHint(String.class.getName())
                    .withBDeepIndexHint(Integer.class.getName())
                    .withCustomConverter(TestCustomConverter.class.getName())
                    .withCustomConverterId("id")
                    .withCustomConverterParam("param")
                    .withA()
                        .withName("src")
                        .withAccessible(true)
                        .withCreateMethod("create")
                        .withMapGetMethod("get")
                        .withMapSetMethod("put")
                        .withDateFormat("MM/dd/yyyy HH:mm")
                        .withKey("id")
                        .withType(FieldType.GENERIC)
                        .withSetMethod("set")
                        .withGetMethod("get")
                    .endField()
                    .withB()
                        .withName("dest")
                        .withAccessible(true)
                        .withCreateMethod("create")
                        .withMapGetMethod("get")
                        .withMapSetMethod("put")
                        .withDateFormat("MM/dd/yyyy HH:mm")
                        .withKey("id")
                        .withType(FieldType.GENERIC)
                        .withSetMethod("set")
                        .withGetMethod("get")
                    .endField()
                .end()
                .end();
            // @formatter:on
        }
    }

    @Test
    public void canBuild() {
        ELEngine elEngine = new DefaultELEngine(ELExpressionFactory.newInstance());
        BeanMappingsFluentBuilder builder = new TestFluentBuilder(elEngine);

        BeanContainer beanContainer = new BeanContainer();
        DestBeanCreator destBeanCreator = new DestBeanCreator(beanContainer);
        PropertyDescriptorFactory propertyDescriptorFactory = new PropertyDescriptorFactory();

        List<MappingFileData> answer = builder.build(beanContainer, destBeanCreator, propertyDescriptorFactory);

        assertNotNull(answer);
        assertTrue(answer.size() > 0);
    }
}
