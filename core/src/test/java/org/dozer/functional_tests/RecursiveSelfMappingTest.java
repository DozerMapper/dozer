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
package org.dozer.functional_tests;

import java.util.ArrayList;
import java.util.List;

import org.dozer.Mapper;
import org.dozer.MappingException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author dmitry.buzdin
 */
public class RecursiveSelfMappingTest extends AbstractFunctionalTest {

  private ContainerBean c1;

  @Before
  @Override
  public void setUp() {
    SelfReferencingBean b1 = newInstance(SelfReferencingBean.class);
    b1.setId(1L);

    SelfReferencingBean b2 = newInstance(SelfReferencingBean.class);
    b2.setId(2L);

    b1.setOneToOne(b2);
    b2.setOneToOne(b1);

    c1 = new ContainerBean();
    c1.getBeans().add(b1);
    c1.getBeans().add(b2);
  }

  @Test
  public void testRecursiveSelfMapping() {
    testMapping(c1, "mappings/selfreference_recursive.xml");
  }

  @Test
  public void testRecursiveSelfMapping_Iterate() {
    testMapping(c1, "mappings/selfreference_recursive_iterate.xml");
  }

  private void testMapping(ContainerBean c1, String mappingFile) {
    Mapper mapper = getMapper(mappingFile);

    try {
      ContainerBean2 c2 = mapper.map(c1, ContainerBean2.class);
      SelfReferencingBean mb1 = c2.getBeans().get(0);
      SelfReferencingBean mb2 = c2.getBeans().get(1);
      assertEquals(mb1.getId().longValue(), 1L);
      assertEquals(mb2.getId().longValue(), 2L);
      assertEquals(mb1.getOneToOne(), mb2);
      assertEquals(mb2.getOneToOne(), mb1);
      assertEquals(mb1.getOneToOne().getId().longValue(), 2L);
      assertEquals(mb1.getOneToOne().getOneToOne(), mb1);
      assertEquals(mb1.getOneToOne().getOneToOne().getId().longValue(), 1L);
    } catch (MappingException e) {
      if (e.getCause() instanceof StackOverflowError) {
        fail("Recursive mapping caused a stack overflow.");
      } else {
        throw e;
      }
    }
  }

  public static class SelfReferencingBean {
    private Long id;
    private SelfReferencingBean oneToOne;
    private DetailBean detailBean;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public SelfReferencingBean getOneToOne() {
      return oneToOne;
    }

    public void setOneToOne(SelfReferencingBean oneToOne) {
      this.oneToOne = oneToOne;
    }

    public DetailBean getDetailBean() {
      return detailBean;
    }

    public void setDetailBean(DetailBean detailBean) {
      this.detailBean = detailBean;
    }
  }

  public static class DetailBean {
    private SelfReferencingBean oneToOne;
    private Long id;

    public SelfReferencingBean getOneToOne() {
      return oneToOne;
    }

    public void setOneToOne(SelfReferencingBean oneToOne) {
      this.oneToOne = oneToOne;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }
  }

  public static class ContainerBean {
    private List<SelfReferencingBean> beans = new ArrayList<SelfReferencingBean>();

    public List<SelfReferencingBean> getBeans() {
      return beans;
    }

    public void setBeans(List<SelfReferencingBean> beans) {
      this.beans = beans;
    }

    public void add(SelfReferencingBean bean) {
      this.beans.add(bean);
    }
  }

  // For the sake of controlling mapping direction
  public static class ContainerBean2 extends ContainerBean {
  }

}
