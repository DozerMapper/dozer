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
package com.github.dozermapper.core;

import java.util.ArrayList;
import java.util.List;

import com.github.dozermapper.core.vo.A;
import com.github.dozermapper.core.vo.B;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MappingProcessorTest extends AbstractDozerTest {

    private ArrayList<Object> sourceList;
    private ArrayList<Object> destinationList;

    @Before
    public void setUp() {
        sourceList = new ArrayList<>();
        destinationList = new ArrayList<>();
    }

    @Test
    public void testTwiceObjectToObjectConvert() {
        // todo mapping processor should be redesigned, see #377
        DozerBeanMapper mapper = (DozerBeanMapper)DozerBeanMapperBuilder.buildDefault();
        Mapper mappingProcessor = mapper.getMappingProcessor();

        A src = new A();
        src.setB(new B());

        A dest1 = new A();
        mappingProcessor.map(src, dest1);
        A dest2 = new A();
        mappingProcessor.map(src, dest2);

        assertSame(dest1.getB(), dest2.getB());
    }

    @Test
    public void testPrepareDetinationList_OK() {
        List<?> result = MappingProcessor.prepareDestinationList(sourceList, destinationList);
        assertEquals(destinationList, result);

        destinationList.add("");
        result = MappingProcessor.prepareDestinationList(sourceList, destinationList);
        assertEquals(destinationList, result);
    }

    @Test
    public void testPrepareDetinationList_Null() {
        List<?> result = MappingProcessor.prepareDestinationList(sourceList, null);
        assertNotNull(result);
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void testPrepareDetinationList_Array() {
        List<?> result = MappingProcessor.prepareDestinationList(sourceList, new Object[] {"A"});
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("A", result.iterator().next());
    }

    @Test
    public void testPrepareDetinationList_StrangeCase() {
        List<?> result = MappingProcessor.prepareDestinationList(sourceList, "Hullo");
        assertNotNull(result);
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    public void testRemoveOrphans_OK() {
        destinationList.add("A");
        MappingProcessor.removeOrphans(sourceList, destinationList);
        assertTrue(destinationList.isEmpty());
    }

    @Test
    public void testRemoveOrphans_Many() {
        destinationList.add("A");
        destinationList.add("B");
        destinationList.add("C");

        sourceList.add("B");
        sourceList.add("D");

        MappingProcessor.removeOrphans(sourceList, destinationList);
        assertEquals(2, destinationList.size());
        assertEquals("B", destinationList.get(0));
        assertEquals("D", destinationList.get(1));
    }

    @Test
    public void testRemoveOrphans_Ordering() {
        destinationList.add(new Ordered(1));
        destinationList.add(new Ordered(2));
        destinationList.add(new Ordered(3));

        sourceList.add(new Ordered(0));
        sourceList.add(new Ordered(3));
        sourceList.add(new Ordered(2));
        sourceList.add(new Ordered(1));

        MappingProcessor.removeOrphans(sourceList, destinationList);
        assertEquals(4, destinationList.size());
        assertEquals(new Ordered(1), destinationList.get(0));
        assertEquals(new Ordered(2), destinationList.get(1));
        assertEquals(new Ordered(3), destinationList.get(2));
        assertEquals(new Ordered(0), destinationList.get(3));
    }

    private static final class Ordered {
        private int id;

        private Ordered(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Ordered ordered = (Ordered)o;
            return id == ordered.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

}
