package org.dozer;

import org.dozer.vo.A;
import org.dozer.vo.B;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dmitry Spikhalskiy
 * @since 03.01.13
 */
public class MappingProcessorTest extends AbstractDozerTest {
  private DozerBeanMapper mapper;
  private MappingProcessor mappingProcessor;

  @Before
  public void setUp() {
    mapper = new DozerBeanMapper();
    mappingProcessor = (MappingProcessor) mapper.getMappingProcessor();
  }

  @Test
  public void testTwiceObjectToObjectConvert() {
    A src = new A();
    src.setB(new B());

    A dest1 = new A();
    mappingProcessor.map(src, dest1);
    A dest2 = new A();
    mappingProcessor.map(src, dest2);

    assertSame(dest1.getB(), dest2.getB());
  }
}
