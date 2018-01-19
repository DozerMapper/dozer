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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author dmitry buzdin
 * @since 09.10.2011
 */
public class UntypedCollectionTest extends AbstractFunctionalTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();
    mapper = getMapper("mappings/untypedCollection.xml");
  }

  @Test
  public void testMapperDeepIndexForUntypedList() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("name", "fooname");
    map.put("bars[0]_id", "1234");
    map.put("bars[1]_id", "2345");

    Foo f = mapper.map(map, Foo.class);

    assertThat(f.getName(), equalTo("fooname"));
    assertThat(((Bar) f.getBars().get(0)).getId(), equalTo("1234"));
    assertThat(((Bar) f.getBars().get(1)).getId(), equalTo("2345"));

    Map<String, String> m2 = new HashMap<String, String>();
    mapper.map(f, m2);

    assertThat(m2.get("name"), equalTo("fooname"));
    assertThat(m2.get("bars[0]_id"), equalTo("1234"));
    assertThat(m2.get("bars[1]_id"), equalTo("2345"));
  }

  public static class Bar {
    private String id;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }

  public static class Foo {

    private String name;
    private List bars;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List getBars() {
      return bars;
    }

    public void setBars(List bars) {
      this.bars = bars;
    }
  }

}
