/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.util;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;

import org.junit.Test;

import static org.junit.Assert.fail;

public class MappingValidatorTest extends AbstractDozerTest {

    private BeanContainer beanContainer = new BeanContainer();

    @Test(expected = MappingException.class)
    public void testValidateMappingRequest_NullSrcObj() {
        MappingValidator.validateMappingRequest(null);
    }

    @Test(expected = MappingException.class)
    public void testValidateMappingRequest_NullDestObj() {
        MappingValidator.validateMappingRequest(new Object(), null);
    }

    @Test(expected = MappingException.class)
    public void testValidateMappingRequest_BothNullObj() {
        MappingValidator.validateMappingRequest(null, null);
    }

    @Test
    public void testValidateMappingRequest_OK() {
        try {
            MappingValidator.validateMappingRequest(new Object(), new Object());
            MappingValidator.validateMappingRequest(new Object(), String.class);
        } catch (MappingException e) {
            fail("Not expected");
        }
    }

    @Test(expected = MappingException.class)
    public void testValidtateMappingURL_InvalidFileName() {
        MappingValidator.validateURL("hello", beanContainer);
    }

    @Test(expected = MappingException.class)
    public void testValidtateMappingURL_NullFileName() {
        MappingValidator.validateURL(null, beanContainer);
    }

}
