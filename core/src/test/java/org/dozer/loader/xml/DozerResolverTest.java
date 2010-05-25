package org.dozer.loader.xml;

import org.dozer.AbstractDozerTest;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * @author Dmitry Buzdin
 */
public class DozerResolverTest extends AbstractDozerTest {

  private DozerResolver dozerResolver;

  @Before
  public void setUp() throws Exception {
    dozerResolver = new DozerResolver();
  }

  @Test
  public void testResolveEntity_OK() {
    InputSource inputSource = dozerResolver.resolveEntity(null, "http://nowhere.tosearch.url/beanmapping.xsd");
    assertNotNull(inputSource);
  }

  @Test
  public void testResolveEntity_Direct() {
    InputSource inputSource = dozerResolver.resolveEntity(null, "beanmapping.xsd");
    assertNotNull(inputSource);
  }

}
