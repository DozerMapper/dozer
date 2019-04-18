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
package com.github.dozermapper.spring.functional_tests.support;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.spring.DozerBeanMapperBuilderCustomizer;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;

public class MyBeanMapperBuilderCustomizer implements DozerBeanMapperBuilderCustomizer, BeanNameAware, Ordered {

    private static List<String> histories = new ArrayList<>();

    private final int order;
    private String beanName;

    public MyBeanMapperBuilderCustomizer() {
        this.order = 0;
    }

    public MyBeanMapperBuilderCustomizer(int order) {
        this.order = order;
    }

    @Override
    public void customize(DozerBeanMapperBuilder builder) {
        histories.add(beanName);
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public int getOrder() {
        return order;
    }

    public static List<String> getHistories() {
        return histories;
    }

    public static void clear() {
        histories.clear();
    }

}
