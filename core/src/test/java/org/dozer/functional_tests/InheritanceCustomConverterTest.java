package org.dozer.functional_tests;

import org.dozer.vo.inheritance.cc.A;
import org.dozer.vo.inheritance.cc.C;
import org.dozer.vo.inheritance.cc.X;
import org.dozer.vo.inheritance.cc.Z;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author dmitry buzdin
 * @since 09.10.2011
 */
public class InheritanceCustomConverterTest extends AbstractFunctionalTest {

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    mapper = getMapper("inheritanceBug.xml");
  }

  /*
   * Bug #1953410
   */
  @Test
  public void shouldUseConverter() {
    Z source = new Z();
    source.setTest("testString");

    C result = mapper.map(source, C.class);

    assertThat(result.getTest(), equalTo("customConverter"));
  }

  @Test
  public void shouldUseConverter2() {
    X source = new X();
    source.setTest("testString");

    A result = mapper.map(source, A.class);

    assertThat(result.getTest(), equalTo("customConverter"));
  }

  @Test
  public void shouldUseConverter3() {
    X source = new X();
    source.setTest("testString");

    C result = mapper.map(source, C.class);

    assertThat(result.getTest(), equalTo("customConverter"));
  }

  @Test
  public void shouldUseConverter4() {
    Z source = new Z();
    source.setTest("testString");

    A result = mapper.map(source, A.class);

    assertThat(result.getTest(), equalTo("customConverter"));
  }

}
