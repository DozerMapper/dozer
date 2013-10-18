package org.dozer;

import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

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
    DozerBeanMapper dozer = new DozerBeanMapper();
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
    DozerBeanMapper dozer = new DozerBeanMapper(Arrays.asList("mappingProcessorArrayTest.xml"));
    FinalCopyByReferenceDest result = dozer.map(test, FinalCopyByReferenceDest.class);

    long start = System.currentTimeMillis();
    result = dozer.map(test, FinalCopyByReferenceDest.class);
    System.out.println(System.currentTimeMillis() - start);

    assertNotSame(test.getData(), result.getData());
    assertTrue(Arrays.equals(test.getData(), result.getData()));
  }

}
