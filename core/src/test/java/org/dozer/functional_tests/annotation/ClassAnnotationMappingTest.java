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
package org.dozer.functional_tests.annotation;

import org.dozer.Mapping;
import org.dozer.OptionValue;
import org.dozer.functional_tests.AbstractFunctionalTest;
import org.dozer.util.MappingOptions;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author dmitry.buzdin, chas
 */
public class ClassAnnotationMappingTest extends AbstractFunctionalTest {

  private User source;
  private UserDto destination;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    source = new User();
    destination = new UserDto();
  }

  @Test
  public void shouldMapNonEmptyString() {
    source.name = "name";

    UserDto result = mapper.map(source, UserDto.class);

    assertThat(result.username, equalTo("name"));
  }

  @Test
  public void shouldNotMapEmptyString() {
    destination.username = "name";

    mapper.map(source, destination);

    assertThat(destination.username, equalTo("name"));
  }

  @Test
  public void shouldMapNonEmptyString_Backwards() {
    source.freeText = "text";
    source.zip = "12345";

    UserDto result = mapper.map(source, UserDto.class);

    assertThat(result.comment, equalTo("text"));
    assertThat(result.zip, equalTo("12345"));
  }

  @Test
  public void shouldNotMapEmptyString_Backwards() {
    destination.comment = "text";
    destination.zip = "12345";

    mapper.map(source, destination);

    assertThat(destination.comment, equalTo("text"));
    assertThat(destination.zip, equalTo("12345"));
  }

  @Test
  public void shouldMapNonNull() {
    source.id = 4L;
    source.age = 64;
    destination.pk = "1064";
    destination.years = "531";

    mapper.map(source, destination);

    assertThat(destination.pk, equalTo("4"));
    assertThat(destination.years, equalTo("64"));
  }

  @Test
  public void shouldNotMapNull() {
    destination.pk = "4310";
    destination.years = "4353";

    mapper.map(source, destination);

    assertThat(destination.pk, equalTo("4310"));
    assertThat(destination.years, equalTo("4353"));
  }

  @MappingOptions(mapNull = OptionValue.OFF)
  public static class User {

    Long id;

    Short age;

    @Mapping("username")
    String name;

    String freeText;

    String zip;

    @Mapping("pk")
    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public Short getAge() {
      return age;
    }

    public void setAge(Short age) {
      this.age = age;
    }
  }

  @MappingOptions(mapEmptyString = OptionValue.OFF)
  public static class UserDto {

    String years;

    String pk;

    @Mapping
    String zip;

    String username;

    @Mapping("freeText")
    String comment;

    public String getPk() {
      return pk;
    }

    public void setPk(String pk) {
      this.pk = pk;
    }

    @Mapping("age")
    public String getYears() {
      return years;
    }

    public void setYears(String years) {
      this.years = years;
    }
  }
}
