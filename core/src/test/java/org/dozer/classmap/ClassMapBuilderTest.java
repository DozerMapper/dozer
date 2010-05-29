package org.dozer.classmap;

import org.dozer.AbstractDozerTest;
import org.dozer.fieldmap.FieldMap;
import org.dozer.functional_tests.proxied.ProxyDataObjectInstantiator;
import org.dozer.util.DozerConstants;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author Dmitry Buzdin
 */
public class ClassMapBuilderTest extends AbstractDozerTest {

  private ClassMapBuilder.CollectionMappingGenerator collectionMappingGenerator;
  private ClassMapBuilder.MapMappingGenerator mapMappingGenerator;
  private ClassMapBuilder.BeanMappingGenerator beanMappingGenerator;
  private Configuration configuration;

  @Before
  public void setUp() throws Exception {
    collectionMappingGenerator = new ClassMapBuilder.CollectionMappingGenerator();
    mapMappingGenerator = new ClassMapBuilder.MapMappingGenerator();
    beanMappingGenerator = new ClassMapBuilder.BeanMappingGenerator();

    configuration = new Configuration();
  }

  @Test
  public void shouldPrepareMappingsForCollection() throws Exception {
    ClassMap classMap = new ClassMap(null);

    collectionMappingGenerator.apply(classMap, configuration);

    List<FieldMap> fieldMaps = classMap.getFieldMaps();
    assertEquals(1, fieldMaps.size());
    assertEquals(DozerConstants.SELF_KEYWORD, fieldMaps.get(0).getSrcFieldName());
    assertEquals(DozerConstants.SELF_KEYWORD, fieldMaps.get(0).getDestFieldName());
  }

  @Test
  public void ignoreTechnicalFields() throws Exception {
    Object proxy = ProxyDataObjectInstantiator.INSTANCE.newInstance(Object.class);

    assertTrue(ClassMapBuilder.shouldIgnoreField("callback", proxy.getClass(), Object.class));
    assertFalse(ClassMapBuilder.shouldIgnoreField("callback", Object.class, Object.class));
    assertTrue(ClassMapBuilder.shouldIgnoreField("callbacks", proxy.getClass(), Object.class));
    assertFalse(ClassMapBuilder.shouldIgnoreField("callbacks", Object.class, Object.class));
    assertTrue(ClassMapBuilder.shouldIgnoreField("class", Object.class, Object.class));
    assertFalse(ClassMapBuilder.shouldIgnoreField("a", proxy.getClass(), Object.class));
  }


}
