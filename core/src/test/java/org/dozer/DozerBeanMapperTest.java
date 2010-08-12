package org.dozer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dmitry Buzdin
 */
public class DozerBeanMapperTest extends Assert {

  private DozerBeanMapper mapper;
  private static final int THREAD_COUNT = 10;

  @Before
  public void setUp() {
    mapper = new DozerBeanMapper();
  }

  @Test
  public void shouldInitializeOnce() throws Exception {
    final CallTrackingMapper mapper = new CallTrackingMapper();
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

    HashSet<Callable<Object>> callables = new HashSet<Callable<Object>>();

    for (int i = 0; i < THREAD_COUNT; i++) {
      callables.add(new Callable<Object>() {
        public Object call() throws Exception {
          latch.countDown();
          latch.await();
          Mapper processor = mapper.getMappingProcessor();
          assertNotNull(processor);
          return null;
        }
      });
    }
    executorService.invokeAll(callables);
    assertEquals(1, mapper.getCalls());
  }

  class CallTrackingMapper extends DozerBeanMapper {
    AtomicInteger calls = new AtomicInteger(0);

    @Override
    void loadCustomMappings() {
      calls.incrementAndGet();
    }

    public int getCalls() {
      return calls.get();
    }
  }

}
