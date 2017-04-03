/**
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

import org.dozer.DozerBeanMapper;
import org.dozer.DozerConverter;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeMappingOptions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * @author dmitry buzdin
 * @since 09.10.2011
 */
public class CollectionWithNullTest extends Assert {

  private DozerBeanMapper mapper;

  private Foo foo;
  private Bar bar;

  @Before
  public void setUp() {
    mapper = new DozerBeanMapper(Collections.singletonList("collectionsWithNull.xml"));

    foo = new Foo();
    bar = new Bar();
  }

  @Test
  public void shouldMapNullAsListFirstElement() {
    mapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(Foo.class, Bar.class, TypeMappingOptions.mapNull(false))
                .fields("wheeIds", "wheeList");
      }
    });

    bar.getWheeList().add(null);
    bar.getWheeList().add(new Whee("1"));

    Foo result = mapper.map(bar, Foo.class);

    assertThat(result.getWheeIds().size(), equalTo(1));
    assertThat(result.getWheeIds().get(0), equalTo("1"));
  }

  @Test
  public void shouldMapNullAsListSecondElement() {
    mapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(Foo.class, Bar.class, TypeMappingOptions.mapNull(false))
                .fields("wheeIds", "wheeList");
      }
    });

    bar.getWheeList().add(new Whee("1"));
    bar.getWheeList().add(null);

    Foo result = mapper.map(bar, Foo.class);

    assertThat(result.getWheeIds().size(), equalTo(1));
    assertThat(result.getWheeIds().get(0), equalTo("1"));
  }

  @Test
  public void shouldMapNullAsSetSecondElement() {
    mapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(Foo.class, Bar.class, TypeMappingOptions.mapNull(false))
                .fields("wheeIds", "wheeSet");
      }
    });

    bar.getWheeSet().add(new Whee("1"));
    bar.getWheeSet().add(null);

    Foo result = mapper.map(bar, Foo.class);

    assertThat(result.getWheeIds().size(), equalTo(1));
    assertThat(result.getWheeIds().get(0), equalTo("1"));
  }

  @Test
  public void shouldMapNullAsSetSecondElement_Reverse() {
    mapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(Foo.class, Bar.class, TypeMappingOptions.mapNull(false))
                .fields("wheeIds", "wheeSet");
      }
    });

    foo.getWheeIds().add("1");
    foo.getWheeIds().add(null);

    Bar result = mapper.map(foo, Bar.class);

    assertThat(result.getWheeSet().size(), equalTo(1));
    assertThat(result.getWheeSet().iterator().next().getId(), equalTo("1"));
  }

  public static class Foo {
    List<String> wheeIds = new ArrayList<String>();

    public List<String> getWheeIds() {
      return wheeIds;
    }

    public void setWheeIds(List<String> wheeIds) {
      this.wheeIds = wheeIds;
    }
  }

  public static class Bar {
    List<Whee> wheeList = new ArrayList<Whee>();
    Set<Whee> wheeSet = new HashSet<Whee>();

    public List<Whee> getWheeList() {
      return wheeList;
    }

    public void setWheeList(List<Whee> wheeList) {
      this.wheeList = wheeList;
    }

    public Set<Whee> getWheeSet() {
      return wheeSet;
    }

    public void setWheeSet(Set<Whee> wheeSet) {
      this.wheeSet = wheeSet;
    }
  }

  public static class Whee {
    String id;

    public Whee(String id) {
      this.id = id;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }

  public static class WheeConverter extends DozerConverter<String, Whee> {

    public WheeConverter() {
      super(String.class, Whee.class);
    }

    @Override
    public Whee convertTo(String source, Whee destination) {
      return new Whee(source);
    }

    @Override
    public String convertFrom(Whee source, String destination) {
      return source.getId();
    }

  }

}
