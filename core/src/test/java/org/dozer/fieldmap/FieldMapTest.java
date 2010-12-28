package org.dozer.fieldmap;

import org.dozer.AbstractDozerTest;
import org.dozer.classmap.ClassMap;
import org.dozer.classmap.DozerClass;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Dmitry Buzdin
 */
public class FieldMapTest extends AbstractDozerTest {

  private ClassMap classMap;
  private FieldMap fieldMap;
  private DozerField field;

  @Before
  public void setUp() {
    classMap = mock(ClassMap.class);
    fieldMap = new FieldMap(classMap) {};
    DozerClass dozerClass = mock(DozerClass.class);
    when(classMap.getDestClass()).thenReturn(dozerClass);
    field = mock(DozerField.class);
    fieldMap.setDestField(field);
  }

  @Test
  public void shouldSurviveConcurrentAccess() throws InterruptedException {
    DozerField dozerField = mock(DozerField.class);
    when(dozerField.getName()).thenReturn("");
    fieldMap.setSrcField(dozerField);
    fieldMap.setDestField(dozerField);

    ExecutorService executorService = Executors.newFixedThreadPool(10);
    List<Callable<Object>> callables = new ArrayList<Callable<Object>>();
    for (int i=0; i < 30; i++) {
      callables.add(new Callable<Object>() {
        public Object call() throws Exception {
          return fieldMap.getSrcPropertyDescriptor(String.class);
        }
      });
    }

    executorService.invokeAll(callables);
    Thread.sleep(1000);
    executorService.shutdown();
  }

  @Test
  public void shouldBeAccessible_ClassLevel() {
    when(classMap.getDestClass().isAccesible()).thenReturn(Boolean.TRUE);
    when(field.isAccessible()).thenReturn(Boolean.FALSE);

    assertFalse(fieldMap.isDestFieldAccessible());
  }

  @Test
  public void shouldBeAccessible_Both() {
    when(classMap.getDestClass().isAccesible()).thenReturn(Boolean.TRUE);
    when(field.isAccessible()).thenReturn(Boolean.TRUE);

    assertTrue(fieldMap.isDestFieldAccessible());
  }

  @Test
  public void shouldBeAccessible_FieldLevel() {
    when(classMap.getDestClass().isAccesible()).thenReturn(Boolean.FALSE);
    when(field.isAccessible()).thenReturn(Boolean.TRUE);

    assertTrue(fieldMap.isDestFieldAccessible());
  }

  @Test
  public void shouldBeAccessible_Override() {
    when(classMap.getDestClass().isAccesible()).thenReturn(Boolean.TRUE);
    when(field.isAccessible()).thenReturn(Boolean.FALSE);

    assertFalse(fieldMap.isDestFieldAccessible());
  }

  @Test
  public void shouldBeAccessible_Null() {
    when(classMap.getDestClass().isAccesible()).thenReturn(null);
    when(field.isAccessible()).thenReturn(Boolean.TRUE);

    assertTrue(fieldMap.isDestFieldAccessible());
  }

  @Test
  public void shouldBeAccessible_FieldLevelNull() {
    when(classMap.getDestClass().isAccesible()).thenReturn(Boolean.TRUE);
    when(field.isAccessible()).thenReturn(null);

    assertTrue(fieldMap.isDestFieldAccessible());
  }

  @Test
  public void shouldBeAccessible_Default() {
    when(classMap.getDestClass().isAccesible()).thenReturn(null);
    when(field.isAccessible()).thenReturn(null);

    assertFalse(fieldMap.isDestFieldAccessible());
  }


}
