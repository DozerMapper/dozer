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
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.github.dozermapper.core.events.EventListener;
import com.github.dozermapper.core.vo.TestObject;
import com.github.dozermapper.core.vo.generics.deepindex.TestObjectPrime;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class DozerBeanMapperTest {

    private Mapper mapper;
    private static final int THREAD_COUNT = 10;
    private List<Throwable> exceptions;

    @Before
    public void setUp() {
        // todo the test should be redesigned once DozerBeanMapper is immutable #434
        mapper = DozerBeanMapperBuilder.buildDefault();
        exceptions = new ArrayList<>();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                exceptions.add(e);
            }
        });
    }

    @After
    public void tearDown() {
        for (Throwable t : exceptions) {
            t.printStackTrace();
        }
    }

    @Test
    public void shouldBeThreadSafe() throws Exception {
        Mapper mapper = DozerBeanMapperBuilder.create()
                .withMappingFiles("mappings/testDozerBeanMapping.xml")
                .build();

        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        mapper.map(new TestObject(), TestObjectPrime.class);
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();

        }
        latch.await();
        assertTrue(exceptions.isEmpty());
    }

    @Test
    public void shouldReturnImmutableResources() throws Exception {
        mapper.map("Hello", String.class);

        assertImmutable("mappingFiles", mapper);
        assertImmutable("customConverters", mapper);
        assertImmutable("customConvertersWithId", mapper);
        assertImmutable("eventListeners", mapper);
    }

    private void assertImmutable(String name, Mapper mapper) throws Exception {
        MapperModelContext mapperModelContext = mapper.getMapperModelContext();
        Object property = PropertyUtils.getProperty(mapperModelContext, name);
        assertNotNull(property);
        try {
            if (property instanceof List) {
                ((List)property).add("");
            } else if (property instanceof Map) {
                ((Map)property).put("", "");
            }
            fail();
        } catch (UnsupportedOperationException e) {
        }
    }

    @Test
    public void shouldSetEventListeners() {
        EventListener listener = mock(EventListener.class);

        Mapper beanMapper = DozerBeanMapperBuilder.create()
                .withEventListener(listener)
                .build();
        beanMapper.map(new Object(), new Object());

        verify(listener).onMappingStarted(any());
        verify(listener).onMappingFinished(any());
        verifyNoMoreInteractions(listener);
    }
}
