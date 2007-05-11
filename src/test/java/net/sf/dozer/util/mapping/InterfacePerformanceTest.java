package net.sf.dozer.util.mapping;

import net.sf.dozer.util.mapping.vo.iface.ApplicationUser;
import net.sf.dozer.util.mapping.vo.iface.Subscriber;
import net.sf.dozer.util.mapping.vo.iface.UpdateMember;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InterfacePerformanceTest extends AbstractDozerTest {
  private static Log log = LogFactory.getLog(InterfacePerformanceTest.class);

  public void testInterface() throws Exception {
    log.info("Starting");
    mapper = getNewMapper(new String[] { "interfaceMapping.xml" });
    { // warm up to load the config
      ApplicationUser source = new ApplicationUser();
      UpdateMember target = new UpdateMember();
      mapper.map(source, target);
    }
    for (int j = 1; j <= 16384; j += j) {
      long start = System.currentTimeMillis();
      for (int i = 0; i < j; i++) {
        ApplicationUser source = new ApplicationUser();
        UpdateMember target = new UpdateMember();
        mapper.map(source, target);
      }
      long applicationUserTime = (System.currentTimeMillis() - start);
      start = System.currentTimeMillis();
      for (int i = 0; i < j; i++) {
        Subscriber source = new Subscriber();
        UpdateMember target = new UpdateMember();
        mapper.map(source, target);
      }
      long subscriberTime = (System.currentTimeMillis() - start);
      log.info("Execution of " + j + " iterations times ApplicationUser = " + applicationUserTime + " Subscriber = "
          + subscriberTime);
    }
  }

}
