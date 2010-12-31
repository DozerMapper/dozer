package org.dozer.functional_tests.builder;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.classmap.RelationshipType;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.FieldsMappingOptions;
import org.dozer.loader.api.TypeMappingOptions;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.dozer.loader.api.FieldsMappingOptions.*;
import static org.dozer.loader.api.TypeMappingOptions.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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
                TypeMappingOptions.oneWay(),
                mapId("A"),
                mapNull()
        )
                .exclude("excluded")
                .fields("src", "dest",
                        copyByReference(),
                        removeOrphans(),
                        FieldsMappingOptions.relationshipType(RelationshipType.NON_CUMULATIVE),
                        hintA(String.class),
                        hintB(Integer.class),
                        FieldsMappingOptions.oneWay(),
                        useMapId("A"),
                        customConverterId("id")
                )
                .fields("src", "dest",
                        customConverter("org.dozer.CustomConverter")
                );

        mapping(type(Bean.class), type("java.util.Map").mapNull(true),
                trimStrings(),
                TypeMappingOptions.relationshipType(RelationshipType.CUMULATIVE),
                stopOnErrors(),
                mapEmptyString()
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

  @Test
  public void shouldHaveIterateType() {
    BeanMappingBuilder builder = new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(type(IterateBean.class), type(IterateBean2.class))
                .fields(
                        field("integers"),
                        field("strings").iterate().setMethod("addString"),
                        hintB(String.class)
                );
      }
    };

    mapper.addMapping(builder);

    IterateBean bean = new IterateBean();
    bean.getIntegers().add(new Integer("1"));

    IterateBean2 result = mapper.map(bean, IterateBean2.class);

    assertThat(result.strings.size(), equalTo(1));
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

  public static class IterateBean {

    List<Integer> integers = new ArrayList<Integer>();
    List<String> strings = new ArrayList<String>();

    public List<Integer> getIntegers() {
      return integers;
    }

    public List<String> getStrings() {
      return strings;
    }

    public void addString(String string) {
      strings.add(string);
    }

  }

  public static class IterateBean2 extends IterateBean {

  }

}
