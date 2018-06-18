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
package com.github.dozermaper.springboot.autoconfigure;

import java.util.Map;

import com.github.dozermaper.springboot.autoconfigure.vo.Dest;
import com.github.dozermaper.springboot.autoconfigure.vo.Source;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.metadata.ClassMappingMetadata;
import com.github.dozermapper.spring.DozerBeanMapperFactoryBean;
import com.github.dozermapper.springboot.autoconfigure.DozerAutoConfiguration;

import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link DozerAutoConfiguration}.
 */
public class DozerAutoConfigurationTests {

    @Test
    public void testDefaultMapperCreated() {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class);
        Mapper mapper = context.getBean(Mapper.class);
        assertNotNull(mapper);
        Map<String, Mapper> beansMap = context.getBeansOfType(Mapper.class);
        assertTrue(beansMap.containsKey("dozerMapper"));
        assertEquals(1, beansMap.keySet().size());
    }

    @Test
    public void overrideDefaultMapper() {
        ConfigurableApplicationContext context = SpringApplication.run(ConfigWithCustomMapper.class);
        Map<String, Mapper> beansMap = context.getBeansOfType(Mapper.class);
        assertEquals(1, beansMap.keySet().size());
        assertTrue(beansMap.containsKey("customDozerMapper"));
        assertFalse(beansMap.containsKey("dozerMapper"));
    }

    @Test
    public void autoConfigurationDisabled() {
        ConfigurableApplicationContext context = SpringApplication.run(DisabledAutoDozerConfiguration.class);
        Map<String, Mapper> beansMap = context.getBeansOfType(Mapper.class);
        assertEquals(0, beansMap.keySet().size());
    }

    @Test
    public void testWithMappingFilesConfiguration() {
        ConfigurableApplicationContext context = SpringApplication
                .run(Application.class, "--dozer.mappingFiles=classpath:/sample_mapping/sample_mapping.xml");
        Mapper mapper = context.getBean(Mapper.class);
        assertNotNull(mapper);
        ClassMappingMetadata mapping = mapper.getMappingMetadata().getClassMapping(Source.class, Dest.class);
        assertNotNull("Mapping configuration not loaded", mapping);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    public static class Application {

    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = DozerAutoConfiguration.class)
    public static class DisabledAutoDozerConfiguration {

    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    public static class ConfigWithCustomMapper {

        @Bean
        public DozerBeanMapperFactoryBean customDozerMapper() {
            return new DozerBeanMapperFactoryBean();
        }
    }
}
