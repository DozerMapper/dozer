package org.dozer.functional_tests;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    mapper = getMapper("untypedCollection.xml");
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
