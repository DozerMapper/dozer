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
package com.github.dozermapper.core.classmap;

import com.github.dozermapper.core.AbstractDozerTest;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CopyByReferenceTest extends AbstractDozerTest {

    @Test
    public void testMatches_OK() {
        assertTrue(new CopyByReference("java.lang.String").matches("java.lang.String"));
        assertFalse(new CopyByReference("java.lang.String").matches("java.lang.Integer"));
    }

    @Test
    public void testMatches_Mask() {
        assertTrue(new CopyByReference("*").matches("java.lang.String"));
        assertTrue(new CopyByReference("java.*").matches("java.lang.String"));
        assertTrue(new CopyByReference("java.*.String").matches("java.lang.String"));
        assertTrue(new CopyByReference("java.lang.String*").matches("java.lang.String"));
        assertTrue(new CopyByReference("java.lang.Stri*").matches("java.lang.String"));
        assertTrue(new CopyByReference("java.lang.*tring").matches("java.lang.String"));
        assertTrue(new CopyByReference("java.lang.*tri*").matches("java.lang.String"));
        assertTrue(new CopyByReference("java.*tring").matches("java.lang.String"));

        assertFalse(new CopyByReference("com*").matches("java.lang.String"));
        assertFalse(new CopyByReference("java.lang.*f*").matches("java.lang.String"));
    }

    @Test
    public void testMatches_SpecialCases() {
        assertFalse(new CopyByReference("java.*.tring").matches("java.lang.String"));
    }

}
