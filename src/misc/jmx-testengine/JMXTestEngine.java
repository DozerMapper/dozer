import java.util.ArrayList;
import java.util.List;

import net.sf.dozer.DozerBeanMapper;
import net.sf.dozer.Mapper;
import net.sf.dozer.util.MapperConstants;
import net.sf.dozer.functional_tests.NoProxyDataObjectInstantiator;
import net.sf.dozer.functional_tests.support.TestDataFactory;
import net.sf.dozer.vo.ArrayCustConverterObj;
import net.sf.dozer.vo.ArrayCustConverterObjPrime;
import net.sf.dozer.vo.SimpleObj;
import net.sf.dozer.vo.SimpleObjPrime2;
import net.sf.dozer.vo.TestObject;
import net.sf.dozer.vo.TestObjectPrime;

public class JMXTestEngine {

  public static void main(String[] args) throws Exception {
    System.setProperty("dozer.debug", "true");
    System.setProperty(MapperConstants.CONFIG_FILE_SYS_PROP, "jmx_test_engine.properties");
    performSomeMappings();

    System.out.println("Waiting forever...");
    Thread.sleep(Long.MAX_VALUE);
  }

  private static void performSomeMappings() {
    List<String> mappingFiles = new ArrayList<String>();
    mappingFiles.add("dozerBeanMapping.xml");
    Mapper mapper = new DozerBeanMapper(mappingFiles);

    try {
      mapper.map(new String("yo"), new String("y"));
    } catch (Throwable t) {
    }

    try {
      mapper.map(null, null);
    } catch (Throwable t) {
    }
    try {
      mapper.map(new String(), null);
    } catch (Throwable t) {
    }

    TestDataFactory testDataFactory = new TestDataFactory(NoProxyDataObjectInstantiator.INSTANCE);
    TestObject to = testDataFactory.getInputGeneralMappingTestObject();
    TestObjectPrime prime = mapper.map(to, TestObjectPrime.class);
    TestObject source = mapper.map(prime, TestObject.class);
    mapper.map(source, TestObjectPrime.class);

    int numIters = 4000;
    for (int i = 0; i < numIters; i++) {
      SimpleObj src = testDataFactory.getSimpleObj();
      mapper.map(src, SimpleObjPrime2.class);
    }

    mappingFiles = new ArrayList<String>();
    mappingFiles.add("arrayToStringCustomConverter.xml");
    mapper = new DozerBeanMapper(mappingFiles);

    for (int i = 0; i < 6000; i++) {
      SimpleObj simple = new SimpleObj();
      simple.setField1(String.valueOf(System.currentTimeMillis()));

      ArrayCustConverterObj src = new ArrayCustConverterObj();
      src.setField1(new SimpleObj[] { simple });

      mapper.map(src, ArrayCustConverterObjPrime.class);
    }
  }
}
