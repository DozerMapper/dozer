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
package org.dozer.spring.functional_tests;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.spring.functional_tests.support.ReferencingBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * @author Dmitry Buzdin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/springNameSpace.xml")
public class NamespaceTest {

  @Autowired
  ApplicationContext context;

  @Test
  public void shouldRegisterMapper() {
    DozerBeanMapper beanMapper = (DozerBeanMapper) context.getBean("beanMapper");
    assertThat(beanMapper, notNullValue());

    ReferencingBean referencingBean = context.getBean(ReferencingBean.class);
    Mapper mapper = referencingBean.getMapper();
    assertThat(mapper, notNullValue());

    assertThat(beanMapper, sameInstance(mapper));
  }

}
