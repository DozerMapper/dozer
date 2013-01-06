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
@ContextConfiguration(locations = "/springNamespace.xml")
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
