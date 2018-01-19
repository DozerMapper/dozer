/*
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
package org.dozer.util;

import org.dozer.AbstractDozerTest;
import org.dozer.MappingException;
import org.dozer.config.BeanContainer;
import org.junit.Test;

/**
 * @author tierney.matt
 */
public class MappingValidatorTest extends AbstractDozerTest {

  private BeanContainer beanContainer = new BeanContainer();

  @Test(expected = MappingException.class)
  public void testValidateMappingRequest_NullSrcObj() throws Exception {
    MappingValidator.validateMappingRequest(null);
  }

  @Test(expected = MappingException.class)
  public void testValidateMappingRequest_NullDestObj() throws Exception {
    MappingValidator.validateMappingRequest(new Object(), null);
  }

  @Test(expected = MappingException.class)
  public void testValidateMappingRequest_BothNullObj() throws Exception {
    MappingValidator.validateMappingRequest(null, null);
  }

  @Test
  public void testValidateMappingRequest_OK() throws Exception {
    try {
      MappingValidator.validateMappingRequest(new Object(), new Object());
      MappingValidator.validateMappingRequest(new Object(), String.class);
    } catch (MappingException e) {
      fail("Not expected");
    }
  }

  @Test(expected = MappingException.class)
  public void testValidtateMappingURL_InvalidFileName() throws Exception{
    MappingValidator.validateURL("hello", beanContainer);
  }
  
  @Test(expected = MappingException.class)
  public void testValidtateMappingURL_NullFileName() throws Exception{
    MappingValidator.validateURL(null, beanContainer);
  }

}
