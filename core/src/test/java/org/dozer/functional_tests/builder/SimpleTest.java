package org.dozer.functional_tests.builder;

import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry Buzdin
 */
public class SimpleTest extends Assert {

  private DozerBeanMapper beanMapper;

  @Before
  public void setUp() {
    beanMapper = new DozerBeanMapper();

  }

  @Test
  public void shouldPerformSimpleMapping() {
    beanMapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(
                type(Source.class),
                type(Destination.class)
        )
                .fields(
                        field("stringValue").accessible(true),
                        field("destStringValue")
                );
      }
    });
    Source source = new Source();
    source.setStringValue("A");

    Destination result = beanMapper.map(source, Destination.class);
    assertEquals("A", result.getDestStringValue());
  }

  @Test
  public void shouldPerformMapBasedMapping() {
    beanMapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(
                Source.class,
                Map.class
        ).fields(
                field("stringValue").accessible(true),
                this_().mapKey("key").mapMethods("get", "put")
        );
      }
    });
    Source source = new Source();
    source.setStringValue("A");

    Map result = beanMapper.map(source, HashMap.class);
    assertEquals("A", result.get("key"));
  }


  public static class Source {
    private String stringValue;
    
    public void setStringValue(String stringValue) {
      this.stringValue = stringValue;
    }
  }

  public static class Destination {
    private String destStringValue;

    public String getDestStringValue() {
      return destStringValue;
    }

    public void setDestStringValue(String destStringValue) {
      this.destStringValue = destStringValue;
    }
  }

}
