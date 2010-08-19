package org.dozer.functional_tests.builder;

import org.dozer.DozerBeanMapper;
import org.dozer.classmap.RelationshipType;
import org.dozer.loader.api.BeanMappingBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.dozer.loader.api.FieldsMappingOptions.*;

public class DozerBuilderTest {

  private DozerBeanMapper mapper;

  @Before
  public void setUp() {
    mapper = new DozerBeanMapper();
  }

  @Test
  public void testAllOfTheApi() {

    BeanMappingBuilder builder = new BeanMappingBuilder() {
      protected void configure() {
        mapping(Bean.class, Bean.class)
                .exclude("excluded")
                .fields("src", "dest",
                        copyByReference(),
                        collectionStrategy(true, RelationshipType.NON_CUMULATIVE),
                        hintA(String.class),
                        hintB(Integer.class),
                        oneWay()
                );
      }
    };

    mapper.addMapping(builder);
         
    mapper.map(1, String.class);
  }

  public static class Bean {
    private String excluded;
    private String src;
    private Integer dest;

    public String getExcluded() {
      return excluded;
    }

    public void setExcluded(String excluded) {
      this.excluded = excluded;
    }

    public String getSrc() {
      return src;
    }

    public void setSrc(String src) {
      this.src = src;
    }

    public Integer getDest() {
      return dest;
    }

    public void setDest(Integer dest) {
      this.dest = dest;
    }
  }

}
