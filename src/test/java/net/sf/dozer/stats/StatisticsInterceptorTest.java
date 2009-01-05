package net.sf.dozer.stats;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.lang.reflect.Proxy;

/**
 * @author dmitry.buzdin
 */
public class StatisticsInterceptorTest extends TestCase {

  private StatisticsInterceptor interceptor;

  private Service service = mock(Service.class);
  private StatisticsManager manager = mock(StatisticsManager.class);

  @Before
  protected void setUp() throws Exception {
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
