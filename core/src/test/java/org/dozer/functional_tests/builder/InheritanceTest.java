package org.dozer.functional_tests.builder;

import org.dozer.DozerBeanMapper;
import org.dozer.functional_tests.AbstractFunctionalTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * @author dmitry.buzdin
 */
public class InheritanceTest extends Assert {

  private DozerBeanMapper mapper;
  private A source;

  @Before
  public void setUp() {
    mapper = new DozerBeanMapper();

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

      assertThat((Class<B>) result.getClass(), equalTo(B.class));
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

      assertThat((Class<B>) result.getClass(), equalTo(B.class));
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
