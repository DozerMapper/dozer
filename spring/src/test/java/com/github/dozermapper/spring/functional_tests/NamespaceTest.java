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
package com.github.dozermapper.spring.functional_tests;

import com.github.dozermapper.spring.functional_tests.support.ReferencingBean;

import org.dozer.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/springNameSpace.xml")
public class NamespaceTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void shouldRegisterMapper() {
        Mapper beanMapper = context.getBean("beanMapper", Mapper.class);
        ReferencingBean referencingBean = context.getBean(ReferencingBean.class);

        assertNotNull(beanMapper);
        assertNotNull(referencingBean);

        Mapper mapper = referencingBean.getMapper();

        assertNotNull(mapper);
        assertEquals(beanMapper, mapper);
    }
}
