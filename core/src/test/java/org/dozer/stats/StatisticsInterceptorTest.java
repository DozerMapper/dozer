package org.dozer.stats;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Proxy;

import org.dozer.AbstractDozerTest;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dmitry.buzdin
 */
public class StatisticsInterceptorTest extends AbstractDozerTest {

  private StatisticsInterceptor interceptor;

  private Service service = mock(Service.class);
  private StatisticsManager manager = mock(StatisticsManager.class);

  @Before
  public void setUp() throws Exception {
    interceptor = new StatisticsInterceptor(service, manager);
  }

  @Test
  public void testInvoke() {
    Service proxy = (Service) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Service.class}, interceptor);
    proxy.method("a", "b");

    verify(service).method("a", "b");
    verify(manager).increment(StatisticType.MAPPING_SUCCESS_COUNT);
  }

  @Test
  public void testException() {
    Service proxy = (Service) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{Service.class}, interceptor);
    doThrow(new NullPointerException()).when(service).method("a", "b");

    try {
      proxy.method("a", "b");
      fail();
    } catch (NullPointerException e) {
    }

    verify(service).method("a", "b");
    verify(manager).increment(StatisticType.MAPPING_FAILURE_COUNT);
  }

  private interface Service {
    void method(String src, String dest);
  }

}
