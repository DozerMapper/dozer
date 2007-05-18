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
package net.sf.dozer.util.mapping.recursive;


import net.sf.dozer.util.mapping.AbstractDozerTest;
import net.sf.dozer.util.mapping.MapperIF;

/**
 * Test the dozer behaviour when confronted with structures similar to ours. As of now (dozer 3.0) the behaviour is not
 * optimal and still require special treatments in special cases.
 * 
 * @author ADE
 */
public class RecursiveTest extends AbstractDozerTest {

    private MapperIF mapper;

    private TestClassAA createTestClassAA() {

        // Create sample assureSocialDTO
        TestClassAA classA = new TestClassAA();

        classA.setNom("gbs");
        classA.setPrenom("prn");

        TestClassB classB = new TestClassB();
        classA.addSubs(classB);
        classB.setRue("rue");
        classB.setVille("ville");
        return classA;
    }

    /**
     * this test should validate dozerXX correct behaviour in front of recursive class references in a subclass. With
     * dozer3.0 the first reference is not used but the recursion is correct on the next levels.
     * 
     */
    public void testConvertWithSubClass() {
        mapper = getNewMapper(new String[] { "recursivemappings.xml", "recursivemappings2.xml" });    	
        TestClassAA testAA = createTestClassAA();
        //the == is on purpose, we test that the referenced parent of the first item of the subs is the parent instance itself
        TestClassB testClassB = (TestClassB) testAA.getSubs().iterator().next();
        assertTrue(testClassB.getParent() == testAA);
        TestClassAAPrime testAAPrime = (TestClassAAPrime) mapper.map(testAA, TestClassAAPrime.class, null);
        //testing the new dozer3.0 bi-directionnal reference through a set
        assertEquals(testAA.getSubs().size(), testAAPrime.getSubs().size());
        //the equality is true at the data level
        TestClassBPrime testClassBPrime = (TestClassBPrime) testAAPrime.getSubs().iterator().next();
        assertTrue(testClassBPrime.getParent().equals(testAAPrime));
        //we want the referenced parent of the first item of the subs to be the parent instance itself
        TestClassBPrime testClassBPrime2 = (TestClassBPrime) testAAPrime.getSubs().iterator().next();
        assertTrue(testClassBPrime2.getParent() == testAAPrime);
    }


}
