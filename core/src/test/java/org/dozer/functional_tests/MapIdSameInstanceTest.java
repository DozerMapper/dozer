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
package org.dozer.functional_tests;

import org.dozer.vo.mapidsameinstance.One;
import org.dozer.vo.mapidsameinstance.Two;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Regression-tests to ensure that mapid is respected even when
 * the same instance is mapped.
 *
 * @see <a href="https://github.com/DozerMapper/dozer/issues/238">MappedFieldsTracker does not consider map-id 238</a>
 */
public class MapIdSameInstanceTest extends AbstractFunctionalTest {

    private One one;
    private Two two;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mapper = getMapper("mappings/mapidsameinstance.xml");

        one = new One();
        two = createTwo();
    }

    /**
     * Different instances of Two in the fields of One.
     * Worked correctly in Dozer 5.5.1.
     */
    @Test
    public void testMappingDifferentInstances() {
        testMapping(createTwo());
    }

    /**
     * Same instance of Two in the fields of One. Failed
     * in Dozer 5.5.1, fixed by pull request #257.
     */
    @Test
    public void testMappingSameInstance() {
        testMapping(two);
    }

    private void testMapping(Two anotherTwo) {
        one.setTwo(two);
        one.setAnotherTwo(anotherTwo);

        One mapped = mapper.map(one, One.class, "one");
        assertRespectsMappingIds(mapped);
    }

    private void assertRespectsMappingIds(One mapped) {
        assertNotNull(mapped);

        // Instance of Two in one.two should be mapped according to the "mapsFieldA" mapping id
        assertNotNull(mapped.getTwo());
        assertThat(mapped.getTwo().getA(), is(one.getTwo().getA()));
        assertNull(mapped.getTwo().getB());

        // Instance of Two in one.anotherTwo should be mapped according to the "mapsFieldB" mapping id
        assertNotNull(mapped.getAnotherTwo());
        assertNull(mapped.getAnotherTwo().getA());
        assertThat(mapped.getAnotherTwo().getB(), is(one.getAnotherTwo().getB()));
    }

    private Two createTwo() {
        Two classTwo = new Two();
        classTwo.setA("A");
        classTwo.setB("B");
        return classTwo;
    }
}
