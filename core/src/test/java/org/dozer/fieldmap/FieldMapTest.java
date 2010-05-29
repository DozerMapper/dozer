package org.dozer.fieldmap;

import org.dozer.AbstractDozerTest;
import org.dozer.classmap.ClassMap;
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

  @Before
  public void setUp() {
    classMap = mock(ClassMap.class);
    fieldMap = new FieldMap(classMap) {};
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

}
