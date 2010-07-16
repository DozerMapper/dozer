/*
 * Copyright 2005-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dozer.spring;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.dozer.AbstractDozerTest;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author <a href="mailto:buzdin@gmail.com">Dmitry Buzdin</a>
 */
public class DozerBeanMapperFactoryBeanTest extends AbstractDozerTest {

  private DozerBeanMapperFactoryBean factory;
  private MockResource mockResource;

  @Override
  @Before
  public void setUp() throws Exception {
    factory = new DozerBeanMapperFactoryBean();
    mockResource = new MockResource();
  }

  @Test
  public void testOk() throws Exception {
    factory.setCustomConverters(Collections.EMPTY_LIST);
    factory.setCustomConvertersWithId(Collections.EMPTY_MAP);
    factory.setEventListeners(Collections.EMPTY_LIST);
    factory.setFactories(Collections.EMPTY_MAP);
    factory.setMappingFiles(new Resource[] { mockResource });

    URL url = this.getClass().getClassLoader().getResource("contextMapping.xml");
    mockResource.setURL(url);

    factory.afterPropertiesSet();

    assertEquals(Mapper.class, factory.getObjectType());
    assertTrue(factory.isSingleton());

    DozerBeanMapper mapper = (DozerBeanMapper) factory.getObject();
    List<?> files = mapper.getMappingFiles();
    assertEquals(1, files.size());
    assertEquals("file:" + url.getFile(), files.iterator().next());
  }

  @Test
  public void testEmpty() throws Exception {
    factory.afterPropertiesSet();
  }

  @Test
  public void testDestroy() throws Exception {
    factory.beanMapper = mock(DozerBeanMapper.class);
    factory.destroy();
    verify(factory.beanMapper).destroy();
  }

  private class MockResource implements Resource {
    private URL url;

    public boolean exists() {
      return false;
    }

    public boolean isOpen() {
      return false;
    }

    public URL getURL() throws IOException {
      return url;
    }

    public void setURL(URL url) {
      this.url = url;
    }

    public File getFile() throws IOException {
      return null;
    }

    public Resource createRelative(String s) throws IOException {
      return null;
    }

    public String getFilename() {
      return null;
    }

    public String getDescription() {
      return null;
    }

    public InputStream getInputStream() throws IOException {
      return null;
    }
  }

}
