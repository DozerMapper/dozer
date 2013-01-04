package org.dozer.propertydescriptor;

import org.dozer.vo.proto.ProtoTestObjects;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author Dmitry Spikhalskiy
 * @since 04.01.13
 */
public class ProtoFieldPropertyDescriptorCreationStrategyTest {
  private ProtoFieldPropertyDescriptorCreationStrategy strategy;

  @Before
  public void setUp() throws Exception {
    strategy = new ProtoFieldPropertyDescriptorCreationStrategy();
  }

  @Test
  public void isApplicable_true_ifMessageAndDeepMapping() {
    assertTrue(strategy.isApplicable(
            /*any proto message class*/ProtoTestObjects.SimpleProtoTestObject.class,
            "deep.mapping"));
  }
}
