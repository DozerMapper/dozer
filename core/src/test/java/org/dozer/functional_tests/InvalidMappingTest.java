/**
 * Copyright 2005-2017 Dozer Project
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import org.dozer.MappingException;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author dmitry.buzdin
 */
public class InvalidMappingTest extends AbstractFunctionalTest {

  @Test(expected=MappingException.class)
  public void testWrongClassName() {
    mapper = getMapper("invalidmapping1.xml");
    mapper.map("1", Integer.class);
    fail();
  }

  @Test(expected=MappingException.class)
  public void testNoFieldB() {
    mapper = getMapper("invalidmapping2.xml");
    mapper.map("1", Integer.class);
    fail();
  }

  @Test(expected=MappingException.class)
  public void testEmptyFieldB() {
    mapper = getMapper("invalidmapping3.xml");
    mapper.map("1", Integer.class);
    fail();
  }

  @Test(expected=MappingException.class)
  public void testFieldDoesNotExist() {
    mapper = getMapper("invalidmapping4.xml");
    mapper.map("1", Integer.class);
    fail();
  }

  @Test(expected=MappingException.class)
  public void testNoClassA() {
    mapper = getMapper("invalidmapping5.xml");
    mapper.map("1", Integer.class);
    fail();
  }

}
