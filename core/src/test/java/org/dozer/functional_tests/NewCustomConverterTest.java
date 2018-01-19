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

import java.util.Collection;
import java.util.HashSet;

import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.vo.SimpleObj;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author dmitry.buzdin
 */
public class NewCustomConverterTest extends AbstractFunctionalTest {

  private Mapper mapper;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    mapper = getMapper("mappings/newCustomConverter.xml");
  }

  @Test
  public void test_DirectMapping() {
    SimpleObj source = new SimpleObj();
    source.setField1("yes");

    SimpleObj destination = mapper.map(source, SimpleObj.class);

    assertTrue(destination.getField7());
  }

  @Test
  public void test_ParametrizedMapping() {
    SimpleObj source = new SimpleObj();
    source.setField1("*");
    source.setField7(Boolean.TRUE);

    SimpleObj destination = mapper.map(source, SimpleObj.class);

    assertNull("yes", destination.getField1());
  }

  @Test
  public void test_NoParameterConfigured() {
    assertEquals("yes", mapper.map(Boolean.TRUE, String.class));
    assertEquals("no", mapper.map(Boolean.FALSE, String.class));

    assertEquals(Boolean.TRUE, mapper.map("yes", Boolean.class));
    assertEquals(Boolean.FALSE, mapper.map("no", Boolean.class));
  }

  @Test
  public void test_Autoboxing() {
    HashSet<String> strings = new HashSet<String>();
    strings.add("A");
    strings.add("B");
    IntContainer result = mapper.map(strings, IntContainer.class);
    assertNotNull(result);
    assertEquals(2, result.getValue());
  }

  public static class IntContainer {
    int value;

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }
  }

  public static class IntConverter extends DozerConverter<Integer, Collection> {

    public IntConverter() {
      super(Integer.class, Collection.class);
    }

    @Override
    public Collection convertTo(Integer source, Collection destination) {
      throw new IllegalStateException();
    }

    @Override
    public Integer convertFrom(Collection source, Integer destination) {
      return source.size();
    }
  }

}
