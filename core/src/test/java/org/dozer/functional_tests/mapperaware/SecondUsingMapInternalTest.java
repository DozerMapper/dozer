package org.dozer.functional_tests.mapperaware;

import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.MapperAware;
import org.dozer.vo.mapperaware.MapperAwareSimpleDest;
import org.dozer.vo.mapperaware.MapperAwareSimpleInternal;
import org.dozer.vo.mapperaware.MapperAwareSimpleSrc;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;


/**
 * @author Dmitry Spikhalskiy
 * @see <a href="https://github.com/DozerMapper/dozer/issues/45">issue</a>
 */
public class SecondUsingMapInternalTest {
  DozerBeanMapper mapper;

  @Before
  public void setup() {
    mapper = new DozerBeanMapper();
    List<String> mappingFileUrls = new ArrayList<String>();
    mappingFileUrls.add("mapper-aware.xml");

    Map<String, CustomConverter> customConvertersWithId = new HashMap<String, CustomConverter>();
    customConvertersWithId.put("issue45Converter", new Issue45Converter());

    mapper.setCustomConvertersWithId(customConvertersWithId);
    mapper.setMappingFiles(mappingFileUrls);
  }

  @Test
  public void issue45Test() {
    MapperAwareSimpleSrc src = new MapperAwareSimpleSrc();
    src.setOne(new MapperAwareSimpleInternal());
    MapperAwareSimpleDest dst = new MapperAwareSimpleDest();
    mapper.map(src, dst);
    assertNotNull(dst.getOne());
  }

  private static class Issue45Converter implements CustomConverter, MapperAware {

    private Mapper mapper;

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
      MapperAwareSimpleInternal a = (MapperAwareSimpleInternal)sourceFieldValue;

      MapperAwareSimpleInternal b = new MapperAwareSimpleInternal();
      mapper.map(a, b);
      MapperAwareSimpleInternal b2 = new MapperAwareSimpleInternal();
      mapper.map(a, b2); //throws NPE
      return b2;
    }

    @Override
    public void setMapper(Mapper mapper) {
      this.mapper = mapper;
    }
  }
}
