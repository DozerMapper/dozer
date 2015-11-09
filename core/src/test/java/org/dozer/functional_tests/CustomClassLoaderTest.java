package org.dozer.functional_tests;

import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeMappingOptions;
import org.dozer.util.DozerClassLoader;
import org.dozer.vo.customclassloader.NumericClass;
import org.dozer.vo.customclassloader.TextualClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Test for custom class loader per DozerBeanMapper instance support.
 * It should be possible to set custom class loader per instance, so singleton class loader is overridden.
 *
 * Created by mmatosevic on 6.7.2015.
 */
public class CustomClassLoaderTest extends AbstractFunctionalTest {

    @Mock
    DozerClassLoader classLoader;

    @Override
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCustomClassLoader() throws Exception {
        final String NUMERIC_CLASS_NAME = NumericClass.class.getName();
        final String TEXTUAL_CLASS_NAME = TextualClass.class.getName();

        when(classLoader.loadClass(NUMERIC_CLASS_NAME)).thenAnswer(new Answer<Class>() {
            @Override
            public Class answer(InvocationOnMock invocationOnMock) throws Throwable {
                return NumericClass.class;
            }
        });

        when(classLoader.loadClass(TEXTUAL_CLASS_NAME)).thenAnswer(new Answer<Class>() {
            @Override
            public Class answer(InvocationOnMock invocationOnMock) throws Throwable {
                return TextualClass.class;
            }
        });

        DozerBeanMapper mapper = new DozerBeanMapper();
        mapper.setClassLoader(classLoader);

        mapper.addMapping(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(NumericClass.class, TextualClass.class, TypeMappingOptions.oneWay(), TypeMappingOptions.customClassLoader(classLoader))
                        .fields("number", "text");
            }
        });

        NumericClass numericClass = new NumericClass();
        numericClass.setNumber(78);
        TextualClass textualClass = mapper.map(numericClass, TextualClass.class);
        assertNotNull(textualClass);
        assertEquals(Integer.toString(numericClass.getNumber()), textualClass.getText());

        verify(classLoader, times(2)).loadClass(eq(NUMERIC_CLASS_NAME));
        verify(classLoader, times(2)).loadClass(eq(TEXTUAL_CLASS_NAME));
    }

}
