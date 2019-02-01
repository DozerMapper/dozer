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

import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class MappingProcessorArrayTest extends AbstractDozerTest {

    public static class PrimitiveArray {
        private int[] data;

        public int[] getData() {
            return data;
        }

        public void setData(int[] data) {
            this.data = data;
        }
    }

    public static final class MyDate extends Date {
        public MyDate(long ms) {
            super(ms);
        }
    }

    public static class FinalCopyByReferenceArray {
        private MyDate[] data;

        public MyDate[] getData() {
            return data;
        }

        public void setData(MyDate[] data) {
            this.data = data;
        }
    }

    public static class FinalCopyByReferenceDest {
        private Date[] data;

        public Date[] getData() {
            return data;
        }

        public void setData(Date[] data) {
            this.data = data;
        }
    }

    @Test
    public void testPrimitiveArrayCopy() {

        PrimitiveArray test = new PrimitiveArray();
        int[] data = new int[1024 * 1024];
        for (int i = 0; i < data.length; i++) {
            data[i] = i;
        }
        test.setData(data);
        Mapper dozer = DozerBeanMapperBuilder.buildDefault();
        PrimitiveArray result = dozer.map(test, PrimitiveArray.class);

        long start = System.currentTimeMillis();
        result = dozer.map(test, PrimitiveArray.class);
        System.out.println(System.currentTimeMillis() - start);

        assertNotSame(test.getData(), result.getData());
        assertTrue(Arrays.equals(test.getData(), result.getData()));
    }

    @Test
    public void testReferenceCopy() {

        FinalCopyByReferenceArray test = new FinalCopyByReferenceArray();
        MyDate[] data = new MyDate[1024 * 1024];
        for (int i = 0; i < data.length; i++) {
            data[i] = new MyDate(i);
        }
        test.setData(data);
        Mapper dozer = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/mappingProcessorArrayTest.xml")
                .build();
        FinalCopyByReferenceDest result = dozer.map(test, FinalCopyByReferenceDest.class);

        long start = System.currentTimeMillis();
        result = dozer.map(test, FinalCopyByReferenceDest.class);
        System.out.println(System.currentTimeMillis() - start);

        assertNotSame(test.getData(), result.getData());
        assertTrue(Arrays.equals(test.getData(), result.getData()));
    }

}
