package org.dozer.functional_tests.builder;

import org.dozer.classmap.RelationshipType;
import org.dozer.loader.api.BeanMappingBuilder;
import org.junit.Test;

import static org.dozer.loader.api.FieldsMappingOptions.collectionStrategy;
import static org.dozer.loader.api.FieldsMappingOptions.copyByReference;

public class DozerBuilderTest {

  @Test
  public void test() {

    BeanMappingBuilder builder = new BeanMappingBuilder() {
      protected void configure() {
        mapping(String.class, Integer.class)
                .exclude("excluded")
                .fields("src", "dest", 
                        copyByReference(),
                        collectionStrategy(true, RelationshipType.NON_CUMULATIVE)
                );

        mapping(Double.class, String.class)
                .fields("a", "b");
      }
    };

  }



}
