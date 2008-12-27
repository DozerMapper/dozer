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

import net.sf.dozer.AbstractDozerTest;
import net.sf.dozer.MappingException;

/**
 * @author tierney.matt
 */
public class MappingValidatorTest extends AbstractDozerTest {

  public void testValidateMappingRequest_NullSrcObj() throws Exception {
    try {
      MappingValidator.validateMappingRequest(null);
      fail("Should have thrown exception");
    } catch (MappingException e) {}
  }

  public void testValidateMappingRequest_NullDestObj() throws Exception {
    try {
      MappingValidator.validateMappingRequest(new Object(), null);
      fail("Should have thrown exception");
    } catch (MappingException e) {}
  }

  public void testValidateMappingRequest_BothNullObj() throws Exception {
    try {
      MappingValidator.validateMappingRequest(null, null);
      fail("Should have thrown exception");
    } catch (MappingException e) {}
  }

  public void testValidateMappingRequest_OK() throws Exception {
    try {
      MappingValidator.validateMappingRequest(new Object(), new Object());
      MappingValidator.validateMappingRequest(new Object(), String.class);
    } catch (MappingException e) {
        fail("Not expected");
    }
  }

}
