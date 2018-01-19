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
package org.dozer.functional_tests.builder;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * @author dmitry.buzdin
 */
public class InheritanceTest extends Assert {

  private Mapper mapper;
  private A source;

  @Before
  public void setUp() {
    mapper = DozerBeanMapperBuilder.buildDefault();

    source = new A();
    source.property1 = "1";
    source.property2 = "2";
  }

  @Test
  public void shouldCopyProperties() {
    {
      C result = mapper.map(source, C.class);

      assertThat(result.property1, equalTo("1"));
      assertThat(result.property2, equalTo("2"));
    }

    {
      B result = mapper.map(source, B.class);

      assertThat(result.getClass(), equalTo(B.class));
      assertThat(result.property1, equalTo("1"));
    }
  }

  @Test
  public void shouldCopyProperties_Instances() {
    {
      C result = new C();

      mapper.map(source, result);

      assertThat(result.property1, equalTo("1"));
      assertThat(result.property2, equalTo("2"));
    }

    {
      B result = new B();

      mapper.map(source, result);

      assertThat(result.getClass(), equalTo(B.class));
      assertThat(result.property1, equalTo("1"));
    }
  }


  public static class A {
    String property1;
    String property2;

    public String getProperty1() {
      return property1;
    }

    public void setProperty1(String property1) {
      this.property1 = property1;
    }

    public String getProperty2() {
      return property2;
    }

    public void setProperty2(String property2) {
      this.property2 = property2;
    }
  }

  public static class B {
    String property1;

    public String getProperty1() {
      return property1;
    }

    public void setProperty1(String property1) {
      this.property1 = property1;
    }
  }

  public static class C extends B {
    String property2;

    public String getProperty2() {
      return property2;
    }

    public void setProperty2(String property2) {
      this.property2 = property2;
    }
  }

}
