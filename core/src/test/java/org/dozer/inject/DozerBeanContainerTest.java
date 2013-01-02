package org.dozer.inject;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.*;
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
