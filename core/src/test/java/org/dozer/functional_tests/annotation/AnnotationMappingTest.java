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
  private UserDto destination;

  @Before
  public void setUp() throws Exception {
    super.setUp();
    source = new User();
    destination = new UserDto();
  }

  @Test
  public void shouldMapProperies() {
    source.setId(1L);

    UserDto result = mapper.map(source, UserDto.class);
    
    assertThat(result.getPk(), equalTo("1"));
    assertThat(result.getId(), nullValue());
  }

  @Test
  public void shouldMapProperies_Backwards() {
    source.setAge(new Short("1"));

    UserDto result = mapper.map(source, UserDto.class);
                                         
    assertThat(result.getYears(), equalTo("1"));
  }

  @Test
  public void shouldMapFields() {
    source.name = "name";

    UserDto result = mapper.map(source, UserDto.class);

    assertThat(result.username, equalTo("name"));
    assertThat(result.name, nullValue());
  }

  @Test
  public void shouldMapFields_Backwards() {
    source.freeText = "text";

    UserDto result = mapper.map(source, UserDto.class);

    assertThat(result.comment, equalTo("text"));
  }

  public static class User {
    Long id;
    Short age;

    @Mapping("username")
    private String name;

    String freeText;

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

  public static class UserDto {
    String id;
    String years;
    String pk;
    String name;
    String username;
    @Mapping("freeText")
    String comment;

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
