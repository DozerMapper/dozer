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
package com.github.dozermapper.core.functional_tests;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.vo.LoopObjectChild;
import com.github.dozermapper.core.vo.LoopObjectParent;
import com.github.dozermapper.core.vo.LoopObjectParentPrime;
import com.github.dozermapper.core.vo.bidirectional.A;
import com.github.dozermapper.core.vo.bidirectional.B;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BiDirectionalMappingTest extends AbstractFunctionalTest {

    @Test
    public void testSimpleBidirectional() {
        // A contains B and B contains A.
        A src = newInstance(A.class);
        B field1 = newInstance(B.class);
        field1.setField1(src);
        src.setField1(field1);

        B dest = mapper.map(src, B.class);
        assertNotNull("field1 should have been mapped", dest.getField1());
    }

    @Test
    public void testBidirectionalWithCustomMapping() {
        Mapper mapper = getMapper("mappings/infiniteLoopMapping.xml");
        LoopObjectParent loopObjectParent = newInstance(LoopObjectParent.class);
        LoopObjectChild loopObjectChild = newInstance(LoopObjectChild.class);
        loopObjectChild.setParent(loopObjectParent);
        loopObjectParent.setChild(loopObjectChild);

        LoopObjectParentPrime loopObjectParentPrime = mapper.map(loopObjectParent, LoopObjectParentPrime.class);
        assertNotNull(loopObjectParentPrime);
        assertNotNull(loopObjectParentPrime.getChildPrime());
    }

    /**
     * This test checks the fix of bug #1715496 - Lost and Duplicated Objects
     */
    @Test
    public void testManyObjects() {
        com.github.dozermapper.core.vo.B a = newInstance(com.github.dozermapper.core.vo.B.class);
        com.github.dozermapper.core.vo.B b = new com.github.dozermapper.core.vo.B();
        a.setCs(new com.github.dozermapper.core.vo.C[20000]);
        // Fill the list with C objects numbered from 0 to SIZE-1
        for (int i = 0; i < a.getCs().length; i++) {
            com.github.dozermapper.core.vo.C c = newInstance(com.github.dozermapper.core.vo.C.class);
            c.setValue(Integer.toString(i));
            a.getCs()[i] = c;
        }

        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/testDozerBeanMapping.xml")
                .build();
        mapper.map(a, b);

        // Check if C object nr i holds value i after the mapping
        for (int i = 0; i < b.getCs().length; i++) {
            assertEquals("Wrong value ", b.getCs()[i].getValue(), Integer.toString(i));
        }
    }

}
