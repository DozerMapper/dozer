package org.dozer;

import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.event.DozerEvent;
import org.dozer.vo.TestObject;
import org.dozer.vo.generics.deepindex.TestObjectPrime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.mock;

/**
 * @author Dmitry Buzdin
 */
public class DozerBeanMapperTest extends Assert {

  private DozerBeanMapper mapper;
  private static final int THREAD_COUNT = 10;
  private List<Throwable> exceptions;

  @Before
  public void setUp() {
    mapper = new DozerBeanMapper();
    exceptions = new ArrayList<Throwable>();
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
    assertTrue(exceptions.isEmpty());
  }

  @Test
  public void shouldBeThreadSafe() throws Exception {
    mapper.setMappingFiles(Arrays.asList("dozerBeanMapping.xml"));
    final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

    for (int i = 0; i < THREAD_COUNT; i++) {
      new Thread(new Runnable() {
        public void run() {
          try {
            mapper.map(new TestObject(), TestObjectPrime.class);
          }
          finally {
            latch.countDown();
          }
        }
      }).start();

    }
    latch.await();
    assertTrue(exceptions.isEmpty());
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

  @Test
  public void shouldNotBeConfigurableAfterInit() {
    mapper.map("Hello", String.class);
    try {
      mapper.setCustomConverters(null);
      fail();
    } catch (MappingException e) {
    }
    try {
      mapper.setCustomConvertersWithId(null);
      fail();
    } catch (MappingException e) {
    }
    try {
      mapper.setCustomFieldMapper(null);
      fail();
    } catch (MappingException e) {
    }
    try {
      mapper.setEventListeners(null);
      fail();
    } catch (MappingException e) {
    }
    try {
      mapper.setFactories(null);
      fail();
    } catch (MappingException e) {
    }
    try {
      mapper.setMappingFiles(null);
      fail();
    } catch (MappingException e) {
    }
    try {
      mapper.addMapping(null);
      fail();
    } catch (MappingException e) {
    }
  }

  @Test
  public void shouldReturnImmutableResources() throws Exception {
    mapper.map("Hello", String.class);

    assertImmutable("mappingFiles", mapper);
    assertImmutable("customConverters", mapper);
    assertImmutable("customConvertersWithId", mapper);
    assertImmutable("eventListeners", mapper);
  }

  private void assertImmutable(String name, DozerBeanMapper mapper) throws Exception {
    Object property = PropertyUtils.getProperty(mapper, name);
    assertNotNull(property);
    try {
      if (property instanceof List) {
        ((List) property).add("");
      } else if (property instanceof Map) {
        ((Map) property).put("", "");
      }
      fail();
    } catch (UnsupportedOperationException e) {
    }
  }

  @Test
  public void shouldSetEventListeners() {
    DozerEventListener listener = mock(DozerEventListener.class);
    mapper.setEventListeners(Arrays.asList(listener));

    List<? extends DozerEventListener> listeners = mapper.getEventListeners();

    assertEquals(1, listeners.size());
  }

}
