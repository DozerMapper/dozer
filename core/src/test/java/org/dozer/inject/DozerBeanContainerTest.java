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
package org.dozer.inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * @author Dmitry Buzdin
 */
public class DozerBeanContainerTest {

  DozerBeanContainer container;

  @Before
  public void setUp() {
    container = new DozerBeanContainer();
  }

  @Test(expected = IllegalStateException.class)
  public void notRegistered() {
    container.getBean(String.class);
  }

  @Test
  public void register() {
    container.register(String.class);
    String bean = container.getBean(String.class);
    assertThat(bean, notNullValue());
  }

  @Test
  public void viaInterface() {
    container.register(HashSet.class);
    container.register(ArrayList.class);
    Collection<Collection> beans = container.getBeans(Collection.class);
    assertThat(beans.size(), equalTo(2));
  }

  @Test(expected = IllegalStateException.class)
  public void moreThanOne() {
    container.register(HashSet.class);
    container.register(ArrayList.class);
    container.getBean(Collection.class);
  }

  @Test
  public void injection() {
    container.register(TestBean.class);
    TestBean bean = container.getBean(TestBean.class);
    assertThat(bean.string, notNullValue());
  }

  @Test
  public void deepInjection() {
    container.register(ContainerBean.class);
    ContainerBean bean = container.getBean(ContainerBean.class);
    assertThat(bean.testBean, notNullValue());
    assertThat(bean.testBean.string, notNullValue());
  }

  @Test
  public void caching() {
    container.register(ContainerBean.class);
    ContainerBean bean1 = container.getBean(ContainerBean.class);
    ContainerBean bean2 = container.getBean(ContainerBean.class);
    assertThat(bean1, sameInstance(bean2));
    assertThat(bean1.testBean, sameInstance(bean2.testBean));
    assertThat(bean1.testBean.string, sameInstance(bean2.testBean.string));
  }

  public static class ContainerBean {
    @Inject
    TestBean testBean;
  }

  public static class TestBean {
    @Inject
    String string;
  }

}
