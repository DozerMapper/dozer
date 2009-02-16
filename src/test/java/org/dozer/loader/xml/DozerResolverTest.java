package org.dozer.loader.xml;

import junit.framework.TestCase;

import org.dozer.loader.xml.DozerResolver;
import org.junit.Test;
import org.junit.Before;
import org.xml.sax.InputSource;

/**
 * @author Dmitry Buzdin
 */
public class DozerResolverTest extends TestCase {

  private DozerResolver dozerResolver;

  @Before
  protected void setUp() throws Exception {
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
