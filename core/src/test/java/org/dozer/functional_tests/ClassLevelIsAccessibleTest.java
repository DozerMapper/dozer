package org.dozer.functional_tests;

import org.dozer.Mapping;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Tests for situations when the entire class has been declared as accessible
 * (is-accessible=true).
 */
public class ClassLevelIsAccessibleTest extends AbstractFunctionalTest {
    private final String FIELD_VALUE = "someValue";

    @Override
    @Before
    public void setUp() throws Exception {
        mapper = getMapper("classLevelIsAccessible.xml");
    }

    @Test
    public void testClassLevelMappingOfField_FirstToSecond() {
        First source = new First();
        source.someField = FIELD_VALUE;
        source.another = 2;

        Second target = mapper.map(source, Second.class);

        assertThat(target.someField, equalTo(FIELD_VALUE));
    }

    @Test
    public void testClassLevelMappingOfField_SecondToFirst() {
        Second source = new Second();
        source.someField = FIELD_VALUE;

        First target = mapper.map(source, First.class);

        assertThat(target.someField, equalTo(FIELD_VALUE));
        assertThat(target.another, equalTo(source.another));
    }

    @Test
    public void testExplicitXMLFieldMappingOverridesDefaultFieldMapping() {
        First source = new First();
        source.someField = FIELD_VALUE;

        CustomFieldMapped target = mapper.map(source, CustomFieldMapped.class);

        assertThat(target.someField, nullValue());
        assertThat(target.otherField, equalTo(FIELD_VALUE));
    }

    @Test
    public void testExplicitAnnotationMappingOverridesDefaultFieldMapping() {
        AnnotationMapped source = new AnnotationMapped();
        source.aField = FIELD_VALUE;
        source.bField = 42;

        First target = mapper.map(source, First.class);

        assertThat(target.someField, equalTo(source.aField));
        assertThat(target.another, equalTo(source.bField));
    }

    @Test
    public void testSetterUsedWhenMappingToNonAccessibleClass() {
        First source = new First();
        source.someField = FIELD_VALUE;

        BeanClass target = mapper.map(source, BeanClass.class);

        assertThat(target.setterInvoked, is(true));
    }

    @Test
    public void testGetterUsedWhenMappingFromNonAccessibleClass() {
        BeanClass source = new BeanClass();
        source.someField = FIELD_VALUE;

        mapper.map(source, First.class);

        assertThat(source.getterInvoked, is(true));
    }

    public static class First {
        private String someField;
        private int another;
    }

    public static class Second {
        private String someField;
        private final int another = 8;
    }

    public static class CustomFieldMapped {
        @SuppressWarnings("unused")
        private String someField;
        @SuppressWarnings("unused")
        private String otherField;
    }

    public static class AnnotationMapped {
        @Mapping("someField")
        private String aField;
        @Mapping("another")
        private int bField;
    }

    public static class BeanClass {
        private String someField;
        private boolean setterInvoked;
        private boolean getterInvoked;

        @SuppressWarnings("unused")
        public String getSomeField() {
            getterInvoked = true;
            return someField;
        }

        @SuppressWarnings("unused")
        public void setSomeField(String someField) {
            setterInvoked = true;
            this.someField = someField;
        }
    }
}
