/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.sf.dozer.AbstractDozerTest;
import net.sf.dozer.util.ResourceLoader;

/**
 * @author tierney.matt
 */
public class ResourceLoaderTest extends AbstractDozerTest {
  private ResourceLoader loader = new ResourceLoader();

  public void testResourceNotFound() throws Exception {
    assertNull("file URL should not have been found", loader.getResource(String.valueOf(System.currentTimeMillis())));
  }
  
  public void testGetResourceWithWhitespace() {
    URL url = loader.getResource(" contextMapping.xml " );
    assertNotNull("URL should not be null", url);
  }
  
  public void testGetResourceWithNewlines() {
    URL url = loader.getResource("\ncontextMapping.xml\n" );
    assertNotNull("URL should not be null", url);
  }

  public void testGetResource_FileOutsideOfClasspath() throws Exception {
    // Create temp file.
    File temp = File.createTempFile("dozerfiletest", ".txt");

    // Delete temp file when program exits.
    temp.deleteOnExit();

    String resourceName = "file:" + temp.getAbsolutePath();
    URL url = loader.getResource(resourceName);
    assertNotNull("URL should not be null", url);

    InputStream is = url.openStream();
    assertNotNull("input stream should not be null", is);
  }

  public void testGetResource_FileOutsideOfClasspath_NotFound() throws Exception {
    URL url = loader.getResource("file:" + System.currentTimeMillis());
    assertNotNull("URL should not be null", url);

    try {
      url.openStream();
      fail("should have thrown a file not found exception");
    } catch (IOException e) {
      // expected
    }
  }

  public void testGetResource_FileOutsideOfClasspath_InvalidFormat() throws Exception {
    // when using a file outside of classpath the file name must be prepended with "file:"
    URL url = loader.getResource(String.valueOf(System.currentTimeMillis()));
    assertNull("URL should be null", url);
  }
}
