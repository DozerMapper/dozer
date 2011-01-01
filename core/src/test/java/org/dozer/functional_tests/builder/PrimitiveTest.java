package org.dozer.functional_tests.builder;

import org.dozer.DozerBeanMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;

public class PrimitiveTest extends Assert {

  private DozerBeanMapper mapper;

  @Before
  public void setUp() {
    mapper = new DozerBeanMapper();
  }

  @Test
  public void shouldMapPrimitiveTypes() throws Exception {
    Source source = new Source();
    source.file = "a";
    source.url = "http://a";
    source.type = "java.lang.String";

    Destination result = mapper.map(source, Destination.class);

    assertThat(result.file, equalTo(new File("a")));
    assertThat(result.url, equalTo(new URL("http://a")));
    assertThat(result.type, sameInstance(String.class));
  }

  public static class Source {
    String url;
    String type;
    String file;

    public String getUrl() {
      return url;
    }

    public String getType() {
      return type;
    }

    public String getFile() {
      return file;
    }
  }

  public static class Destination {
    URL url;
    Class<String> type;
    File file;

    public void setUrl(URL url) {
      this.url = url;
    }

    public void setType(Class<String> type) {
      this.type = type;
    }

    public void setFile(File file) {
      this.file = file;
    }
  }

}
