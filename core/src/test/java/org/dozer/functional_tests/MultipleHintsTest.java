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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author dmitry buzdin
 * @since 09.10.2011
 */
public class MultipleHintsTest extends AbstractFunctionalTest {

  @Before
  public void setUp() throws Exception {
    super.setUp();
    mapper = getMapper("mappings/multipleHintsMapping.xml");
  }

  @Test
  public void testMultipleHints() {
    SourceClass sc = new SourceClass();
    List<SrcA> listOfSrcA = sc.getListOfSrcA();
    listOfSrcA.add(new SrcA.SrcB());
    listOfSrcA.add(new SrcA.SrcC());
    sc.setListOfSrcA(listOfSrcA);

    DestinationClass dc = mapper.map(sc, DestinationClass.class);

    assertThat(dc.getListOfA().size(), equalTo(2));
  }

  @Test
  public void testMultipleHintsWithAlreadyMappedObject() {
    SourceClass sc = new SourceClass();
    List<SrcA> listOfSrcA = sc.getListOfSrcA();
    SrcA.SrcB testObj = new SrcA.SrcB();
    listOfSrcA.add(testObj);
    listOfSrcA.add(new SrcA.SrcC());
    listOfSrcA.add(testObj);
    sc.setListOfSrcA(listOfSrcA);

    DestinationClass dc = mapper.map(sc, DestinationClass.class);

    assertThat(dc.getListOfA().size(), equalTo(3));
    assertThat(dc.getListOfA().get(0), equalTo(dc.getListOfA().get(2)));
  }

  public static class SourceClass {

    List<SrcA> listOfSrcA = new ArrayList<SrcA>();


    public void setListOfSrcA(List<SrcA> listOfSrcA) {
      this.listOfSrcA = listOfSrcA;
    }

    public List<SrcA> getListOfSrcA() {
      return listOfSrcA;
    }

  }

  public static class DestinationClass {

    private List<A> listOfA = new ArrayList<A>();


    public List<A> getListOfA() {
      return Collections.unmodifiableList(listOfA);
    }

    public void addA(A a) {
      listOfA.add(a);
    }

  }

  public abstract static class A {

    public static class B extends A {
    }

    public static class C extends A {
    }

  }

  public abstract static class SrcA {

    public static class SrcB extends SrcA {
    }

    public static class SrcC extends SrcA {
    }
  }

}
