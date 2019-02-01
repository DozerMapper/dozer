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
package com.github.dozermapper.core.el;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.github.dozermapper.core.AbstractDozerTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DefaultELEngineTest extends AbstractDozerTest {

    protected ELEngine elEngine;
    protected Method method;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        elEngine = new DefaultELEngine(ELExpressionFactory.newInstance());
        method = DefaultELEngineTest.class.getMethod("concat", String.class, String.class);

        assertNotNull("Failed to getMethod 'concat' from " + DefaultELEngineTest.class.getCanonicalName(), method);
    }

    public static String concat(String a, String b) {
        return a + b;
    }

    @Test
    public void testSimple() {
        elEngine.setVariable("A", "B");

        assertEquals("*B*", elEngine.resolve("*${A}*"));
    }

    @Test
    public void testMap() {
        HashMap<String, Number> hashMap = new HashMap<>();
        hashMap.put("a", 1);

        elEngine.setVariable("A", hashMap, Map.class);

        assertEquals("*1*", elEngine.resolve("*${A['a']}*"));
    }

    @Test
    public void testList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");

        elEngine.setVariable("a", list);

        assertEquals("*1*", elEngine.resolve("*${a[0]}*"));
    }

    @Test
    public void testTwoExpressions() {
        elEngine.setVariable("A1", "B");
        elEngine.setVariable("A2", "C");

        assertEquals("*B*C*", elEngine.resolve("*${A1}*${A2}*"));
    }

    @Test
    public void testFunction() {
        elEngine.setFunction("dozer", "conc", method);
        elEngine.setVariable("a", "aa");
        elEngine.setVariable("b", "bb");

        String result = elEngine.resolve("${dozer:conc(a,b)}");

        assertEquals("aabb", result);
    }

    @Test
    public void testFunctionDefaultName() {
        elEngine.setFunction("dozer", method);

        String result = elEngine.resolve("${dozer:concat(1,2)}");

        assertEquals("12", result);
    }
}
