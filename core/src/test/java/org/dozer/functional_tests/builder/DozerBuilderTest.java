package org.dozer.functional_tests.builder;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.classmap.RelationshipType;
import org.dozer.loader.api.BeanMappingBuilder;
import org.junit.Before;
import org.junit.Test;

public class DozerBuilderTest {

  private DozerBeanMapper mapper;

  @Before
  public void setUp() {
    mapper = new DozerBeanMapper();
  }

  @Test
  public void testApi() {

    BeanMappingBuilder builder = new BeanMappingBuilder() {
      protected void configure() {
        mapping(Bean.class, Bean.class,
                oneWay(),
                mapId("A"),
                mapNull(true)
        )
                .exclude("excluded")
                .fields("src", "dest",
                        copyByReference(),
                        collectionStrategy(true, RelationshipType.NON_CUMULATIVE),
                        hintA(String.class),
                        hintB(Integer.class),
                        fieldOneWay(),
                        useMapId("A"),
                        customConverterId("id")
                )
                .fields("src", "dest",
                        customConverter("org.dozer.CustomConverter")
                );

        mapping(type(Bean.class), type("java.util.Map").mapNull(true),
                trimStrings(true),
                relationshipType(RelationshipType.CUMULATIVE),
                stopOnErrors(true),
                mapEmptyString(true)
        )
                .fields(field("src")
                              .accessible(true),
                        this_()
                              .mapKey("value")
                              .mapMethods("get", "put"),
                        customConverter(CustomConverter.class)
                )
                .fields("src", this_(),
                        deepHintA(Integer.class),
                        deepHintB(String.class),
                        customConverter(CustomConverter.class, "param")
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
