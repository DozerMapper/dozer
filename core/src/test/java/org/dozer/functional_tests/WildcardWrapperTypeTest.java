/*
 * Copyright 2005-2017 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dozer.functional_tests;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 *  Tests to confirm that Dozer can map between primitive and wrapper types. E.g.
 *  a {@code long} field with a getter returning {@code long} and a setter taking
 *  {@code Long} should be automatically mapped by Dozer.
 */
public class WildcardWrapperTypeTest extends AbstractFunctionalTest {

    /**
     *  Mapping should work without configuration when field is primitive and
     *  setter takes a wrapper class. */
    @Test
    public void testCanMapPrimitiveFieldAndWrapperSetter() {
        PrimitiveFieldWrapperSetter source = new PrimitiveFieldWrapperSetter();
        source.setPrimitive(Long.MAX_VALUE);

        PrimitiveFieldWrapperSetter target = mapper.map(source, PrimitiveFieldWrapperSetter.class);

        assertThat(target.getPrimitive(), equalTo(Long.MAX_VALUE));
    }

    /**
     *  Mapping should work without configuration when field is primitive and
     *  getter returns a wrapper class. */
    @Test
    public void testCanMapPrimitiveFieldAndWrapperGetter() {
        PrimitiveFieldWrapperGetter source = new PrimitiveFieldWrapperGetter();
        source.setPrimitive(true);

        PrimitiveFieldWrapperGetter target = mapper.map(source, PrimitiveFieldWrapperGetter.class);

        assertThat(target.getPrimitive(), equalTo(true));
    }

    /**
     *  Mapping should work without configuration when field is primitive and
     *  both getter and setters are of wrapper type. */
    @Test
    public void testCanMapPrimitiveFieldAndWrapperSetterAndGetter() {
        PrimitiveFieldWrapperGetterAndSetter source = new PrimitiveFieldWrapperGetterAndSetter();
        source.setPrimitive(Integer.MIN_VALUE);

        PrimitiveFieldWrapperGetterAndSetter target = mapper.map(source, PrimitiveFieldWrapperGetterAndSetter.class);

        assertThat(target.getPrimitive(), equalTo(Integer.MIN_VALUE));
    }

    /**
     * Sanity check: Wrapper setters that do not autobox to the primitive field type
     * should be ignored. E.g. a setter taking {@code Character} should not be considered
     * a setter for a {@code float} field.
     */
    @Test
    public void testDoesNotMapPrimitiveFieldAndUnrelatedWrapperSetter() {
        PrimitiveFieldUnrelatedSetter source = new PrimitiveFieldUnrelatedSetter();
        source.setPrimitive(Boolean.FALSE);

        PrimitiveFieldUnrelatedSetter target = mapper.map(source, PrimitiveFieldUnrelatedSetter.class);

        assertThat(target.getPrimitive(), equalTo(0D));
    }

    /**
     *  Mapping should work without configuration when field is of wrapper type and
     *  setter takes a primitive type. */
    @Test
    public void testCanMapWrapperFieldAndPrimitiveSetter() {
        WrapperFieldPrimitiveSetter source = new WrapperFieldPrimitiveSetter();
        source.setPrimitive(Float.MAX_VALUE);

        WrapperFieldPrimitiveSetter target = mapper.map(source, WrapperFieldPrimitiveSetter.class);

        assertThat(target.getPrimitive(), equalTo(Float.MAX_VALUE));
    }

    /**
     *  Mappings should work even if the setter is non-void and takes a primitive while
     *  the field is of wrapper type. */
    @Test
    public void testCanMapWrapperFieldAndNonVoidPrimitiveSetter() {
        WrapperFieldAndNonVoidPrimitiveSetter source = new WrapperFieldAndNonVoidPrimitiveSetter();
        source.setPrimitive(Short.MIN_VALUE);

        WrapperFieldAndNonVoidPrimitiveSetter target =
                mapper.map(source, WrapperFieldAndNonVoidPrimitiveSetter.class);

        assertThat(target.getPrimitive(), equalTo(Short.MIN_VALUE));
    }

    @Test
    public void testCanMapBetweenClassUsingWrapperTypeAndClassUsingPrimitiveType() {
        PrimitiveFieldWrapperGetterAndSetter source = new PrimitiveFieldWrapperGetterAndSetter();
        source.setPrimitive(Integer.MIN_VALUE);

        PrimitiveFieldStandardGetterAndSetter target =
                mapper.map(source, PrimitiveFieldStandardGetterAndSetter.class);

        assertThat(target.getPrimitive(), equalTo(Integer.MIN_VALUE));
    }

    public static class PrimitiveFieldWrapperSetter {
        private long primitive;

        public long getPrimitive() {
            return primitive;
        }

        public void setPrimitive(Long primitive) {
            this.primitive = primitive;
        }
    }

    public static class PrimitiveFieldWrapperGetter {
        private boolean primitive;

        public Boolean getPrimitive() {
            return primitive;
        }

        public void setPrimitive(boolean primitive) {
            this.primitive = primitive;
        }
    }

    public static class PrimitiveFieldWrapperGetterAndSetter {
        private int primitive;

        public Integer getPrimitive() {
            return primitive;
        }

        public void setPrimitive(Integer primitive) {
            this.primitive = primitive;
        }
    }

    public static class PrimitiveFieldStandardGetterAndSetter {
        private int primitive;

        public int getPrimitive() {
            return primitive;
        }

        @SuppressWarnings("unused")
        public void setPrimitive(int primitive) {
            this.primitive = primitive;
        }
    }

    public static class PrimitiveFieldUnrelatedSetter {
        private double primitive;

        public double getPrimitive() {
            return primitive;
        }

        @SuppressWarnings("unused")
        public void setPrimitive(Boolean primitive) {
            this.primitive = Double.MIN_VALUE;
        }
    }

    public static class WrapperFieldPrimitiveSetter {
        private Float primitive;

        public Float getPrimitive() {
            return primitive;
        }

        public void setPrimitive(float primitive) {
            this.primitive = primitive;
        }
    }

    public static class WrapperFieldAndNonVoidPrimitiveSetter {
        private Short primitive;

        public Short getPrimitive() {
            return primitive;
        }

        public Short setPrimitive(short primitive) {
            return this.primitive = primitive;
        }
    }
}

