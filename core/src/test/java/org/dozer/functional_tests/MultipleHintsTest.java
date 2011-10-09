package org.dozer.functional_tests;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    mapper = getMapper("multipleHintsMapping.xml");
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

  public static abstract class A {

    public static class B extends A {
    }

    public static class C extends A {
    }

  }

  public static abstract class SrcA {

    public static class SrcB extends SrcA {
    }

    public static class SrcC extends SrcA {
    }
  }

}
