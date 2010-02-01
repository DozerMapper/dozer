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
package org.dozer.functional_tests;

import org.dozer.DozerBeanMapper;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

import java.util.ArrayList;

/**
 * @author Dmitry Buzdin
 */
public class ClassloaderTest extends AbstractFunctionalTest {

  @Test
  public void testClassloader() {
    ArrayList<String> files = new ArrayList<String>();
    files.add("classloader.xml");
    mapper = new DozerBeanMapper(files);
    assertNotNull(mapper);
  }

}
