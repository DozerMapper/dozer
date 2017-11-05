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
package com.github.dozermapper.springboot.autoconfigure;

import java.io.IOException;

import com.github.dozermapper.spring.DozerBeanMapperFactoryBean;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Dozer spring auto configuration.
 *
 */
@Configuration
@ConditionalOnClass({DozerBeanMapperFactoryBean.class, Mapper.class})
@ConditionalOnMissingBean(Mapper.class)
@EnableConfigurationProperties(DozerConfigurationProperties.class)
public class DozerAutoConfiguration {

    private final DozerConfigurationProperties configurationProperties;

    /**
     * Constructor for creating auto configuration.
     * @param configurationProperties properties
     */
    @Autowired
    public DozerAutoConfiguration(DozerConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    /**
     * Creates default Dozer mapper
     * @return Dozer mapper
     * @throws IOException if there is an exception during initialization.
     */
    @Bean
    public DozerBeanMapperFactoryBean dozerMapper() throws IOException {
        DozerBeanMapperFactoryBean factoryBean = new DozerBeanMapperFactoryBean();
        factoryBean.setMappingFiles(configurationProperties.getMappingFiles());
        return factoryBean;
    }

}
