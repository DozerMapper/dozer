package org.dozer.loader.xml;

import org.dozer.classmap.MappingFileData;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Dmitry Buzdin
 */
public class MappingStreamReaderTest {

  private MappingStreamReader streamReader;

  @Before
  public void setUp() throws Exception {
    streamReader = new MappingStreamReader(XMLParserFactory.getInstance());
  }

  @Test
  public void loadFromStreamTest() throws IOException {
    InputStream xmlStream = getClass().getClassLoader().getResourceAsStream("dozerBeanMapping.xml");
    MappingFileData data = streamReader.read(xmlStream);
    xmlStream.close();

    assertThat(data, notNullValue());
  }

  @Test(expected=IllegalArgumentException.class)
  public void nullLoadFromStreamsTest() throws IOException{
    streamReader.read(null);
  }

}
