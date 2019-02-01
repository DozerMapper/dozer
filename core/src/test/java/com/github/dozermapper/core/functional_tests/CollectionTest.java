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

import java.util.ArrayList;
import java.util.List;

import com.github.dozermapper.core.vo.ArrayDest;
import com.github.dozermapper.core.vo.ArraySource;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Collections and arrays test
 */
public class CollectionTest extends AbstractFunctionalTest {

    @Override
    public void setUp() throws Exception {
        mapper = getMapper("mappings/arrayMapping.xml");
    }

    /**
     * Test shows how simple array grows, when dest array is not null
     */
    @Test
    public void testArrayGrowConversion() {
        ArraySource sourceBean = new ArraySource();
        String[] sourceArray = sourceBean.getPreInitializedArray();
        sourceArray[0] = "1";
        sourceArray[1] = "2";

        ArrayDest destinationBean = new ArrayDest();

        mapper.map(sourceBean, destinationBean, "array");

        String[] destinationArray = destinationBean.getPreInitializedArray();
        Assert.assertEquals(sourceArray.length + 2, destinationArray.length);

    }

    @Test
    public void testSetValueToNullArray() {
        ArraySource sourceBean = new ArraySource();
        ArrayDest arrayDest = mapper.map(sourceBean, ArrayDest.class, "single");
        Assert.assertEquals(1, arrayDest.getArray().length);
        Assert.assertNull("Element must contain null", arrayDest.getArray()[0]);
    }

    /**
     * Test collection to primitive array mapping
     */
    @Test
    public void testCollectionToPrimitiveArray() {
        ArraySource sourceBean = new ArraySource();
        List<Integer> srcList = new ArrayList<>();
        srcList.add(new Integer(2));
        srcList.add(new Integer(3));
        srcList.add(new Integer(8));
        sourceBean.setListOfIntegers(srcList);
        ArrayDest destBean = mapper.map(sourceBean, ArrayDest.class);

        int[] resultPrimitiveIntArray = destBean.getPrimitiveIntArray();
        for (int i = 0; i < srcList.size(); i++) {
            Integer srcValue = new Integer(srcList.get(i));
            int resultValue = resultPrimitiveIntArray[i];
            assertEquals(srcValue, new Integer(resultValue));
        }
    }

    /**
     * Test primitive array to collection mapping and also test for bidirectionality
     * in the custom mappings XML file
     */
    @Test
    public void testPrimitiveArrayToCollection() {
        ArrayDest sourceBean = new ArrayDest();
        int[] primitiveIntArray = {2, 3, 8};
        sourceBean.setPrimitiveIntArray(primitiveIntArray);
        ArraySource destBean = mapper.map(sourceBean, ArraySource.class);

        List<Integer> resultList = destBean.getListOfIntegers();
        for (int i = 0; i < primitiveIntArray.length; i++) {
            int srcValue = primitiveIntArray[i];
            int resultValue = resultList.get(i);
            assertEquals(srcValue, resultValue);
        }
    }

}
