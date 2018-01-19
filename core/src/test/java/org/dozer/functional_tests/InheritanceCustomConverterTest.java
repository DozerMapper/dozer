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
    mapper = getMapper("mappings/inheritanceBug.xml");
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
