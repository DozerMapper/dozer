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
import org.dozer.functional_tests.AbstractFunctionalTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author dmitry.buzdin
 */
public class AnnotationMappingTest extends AbstractFunctionalTest {

  private User source;
  private SubUser subSource;
  private UserDto destination;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    source = new User();
    subSource = new SubUser();
    destination = new UserDto();
  }

  @Test
  public void shouldMapProperties() {
    source.setId(1L);

    UserDto result = mapper.map(source, UserDto.class);
    
    assertThat(result.getPk(), equalTo("1"));
    assertThat(result.getId(), nullValue());
  }

  @Test
  public void shouldMapProperties_Backwards() {
    source.setAge(new Short("1"));

    UserDto result = mapper.map(source, UserDto.class);
                                         
    assertThat(result.getYears(), equalTo("1"));
  }

  @Test
  public void shouldMapFields_Custom() {
    source.setName("name");

    UserDto result = mapper.map(source, UserDto.class);

    assertThat(result.username, equalTo("name"));
    assertThat(result.name, nullValue());
  }

  @Test
  public void shouldMapFields_Custom_Backwards() {
    source.freeText = "text";

    UserDto result = mapper.map(source, UserDto.class);

    assertThat(result.comment, equalTo("text"));
  }

  @Test
  public void shouldMapFields_Default() {
    source.setRole("role");

    UserDto result = mapper.map(source, UserDto.class);

    assertThat(result.role, equalTo("role"));
  }

  @Test
  public void shouldMapFields_Default_Backwards() {
    source.setZip("12345");

    UserDto result = mapper.map(source, UserDto.class);

    assertThat(result.zip, equalTo("12345"));
  }

  @Test
  public void shouldMapFields_Inherited() {
    subSource.setName("name");
    subSource.setRole("role");

    UserDto result = mapper.map(subSource, UserDto.class);

    assertThat(result.username, equalTo("name"));
    assertThat(result.role, equalTo("role"));
    assertThat(result.name, nullValue());
  }

  @Test
  public void shouldMapFields_Backwards_Inherited() {
    subSource.freeText = "text";
    subSource.setZip("12345");

    UserDto result = mapper.map(subSource, UserDto.class);

    assertThat(result.comment, equalTo("text"));
    assertThat(result.zip, equalTo("12345"));
  }

  @Test(expected = NoSuchFieldException.class)
  public void shouldMapFields_Optional() throws NoSuchFieldException {
    source.setPassword("some value");

    UserDto result = mapper.map(source, UserDto.class);

    result.getClass().getField("password");
  }

  @Test
  public void shouldMapFields_Optional_Exists() {
    source.setToken("some value");

    UserDto result = mapper.map(source, UserDto.class);

    assertThat(result.token, equalTo("some value"));
  }

  @Test
  public void shouldMapFields_Optional_Exists_Backwards() {
    source.token = "some value";

    UserDto result = mapper.map(source, UserDto.class);

    assertThat(result.token, equalTo("some value"));
  }

  public static class User {

    Long id;

    Short age;

    private String zip;

    @Mapping
    private String role;

    @Mapping("username")
    private String name;

    String freeText;

    @Mapping(optional = true)
    private String password;

    @Mapping(optional = true)
    private String token;

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

    public void setName(String name) {
      this.name = name;
    }

    public void setRole(String role) {
      this.role = role;
    }

    public void setZip(String zip) {
      this.zip = zip;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public void setToken(String token) {
      this.token = token;
    }
  }

  public static class SubUser extends User {}

  public static class UserDto {

    String id;

    String years;

    String pk;

    @Mapping
    String zip;

    String role;

    String name;

    String username;

    @Mapping("freeText")
    String comment;

    String token;

    public String getPk() {
      return pk;
    }

    public void setPk(String pk) {
      this.pk = pk;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
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
