/*
 * Copyright 2005-2009 the original author or authors.
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
package org.dozer.functional_tests.spring;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import java.util.Map;

/**
 * @author dmitry.buzdin
 */
public class SpringIntegrationTest extends TestCase {

  private ClassPathXmlApplicationContext context;

  @Before
  protected void setUp() throws Exception {
    context = new ClassPathXmlApplicationContext("applicationContext.xml");
  }

  @Test
  public void testCreation() {
    Map map = context.getBeansOfType(Mapper.class);
    assertFalse(map.isEmpty());
    Object bean = map.values().iterator().next();
    assertTrue(bean instanceof DozerBeanMapper);
  }

}
