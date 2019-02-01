/*
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.core.converters;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.github.dozermapper.core.AbstractDozerTest;
import com.github.dozermapper.core.config.BeanContainer;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PrimitiveOrWrapperConverterTest extends AbstractDozerTest {

    private PrimitiveOrWrapperConverter converter = new PrimitiveOrWrapperConverter(new BeanContainer());

    @Test
    public void testConvertPrimitiveOrWrapperEmptyString() {
        // Test scenarios where emptry string should not result in a null output object
        String input = "";
        Object output = converter.convert(input, String.class, null);

        assertNotNull("output object should not be null", output);
        assertEquals("invalid output value", input, output);
    }

    @Test
    public void testConvertPrimitiveOrWrapperEmptyString2() {
        // For non strings, the output object should be null when an empty string is used as input
        String input = "";
        Integer output = (Integer)converter.convert(input, Integer.class, null);

        assertNull("output object should be null", output);
    }

    @Test
    public void testConvertInteger() {
        Object[] input = {String.valueOf(Integer.MIN_VALUE), "-17", "-1", "0", "1", "17", String.valueOf(Integer.MAX_VALUE),
                          new Byte((byte)7), new Short((short)8), new Integer(9), new Long(10), new Float(11.1), new Double(12.2),
                          new Boolean(true), new Boolean(false)};
        Integer[] expected = {new Integer(Integer.MIN_VALUE), new Integer(-17), new Integer(-1), new Integer(0), new Integer(1),
                              new Integer(17), new Integer(Integer.MAX_VALUE), new Integer(7), new Integer(8), new Integer(9), new Integer(10),
                              new Integer(11), new Integer(12), new Integer(1), new Integer(0)};

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i].getClass().getName() + " to Integer", expected[i], converter.convert(input[i], Integer.class, null));
            assertEquals(input[i].getClass().getName() + " to int", expected[i], converter.convert(input[i], Integer.TYPE, null));
        }
    }

    @Test(expected = ConversionException.class)
    public void testConvertIntegerWithFailure() {
        Object input = "three";
        converter.convert(input, Integer.class, null);
    }

    @Test
    public void testConvertDouble() {
        Object[] input = {String.valueOf(Double.MIN_VALUE), "-17.2", "-1.1", "0.0", "1.1", "17.2", String.valueOf(Double.MAX_VALUE),
                          new Byte((byte)7), new Short((short)8), new Integer(9), new Long(10), new Float(11.1), new Double(12.2)};
        Double[] expected = {new Double(Double.MIN_VALUE), new Double(-17.2), new Double(-1.1), new Double(0.0), new Double(1.1),
                             new Double(17.2), new Double(Double.MAX_VALUE), new Double(7), new Double(8), new Double(9), new Double(10),
                             new Double(11.1), new Double(12.2)};

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i].getClass().getName() + " to Double", expected[i].doubleValue(), ((Double)(converter.convert(input[i],
                                                                                                                              Double.class, null))).doubleValue(), 0.00001D);
            assertEquals(input[i].getClass().getName() + " to double", expected[i].doubleValue(), ((Double)(converter.convert(input[i],
                                                                                                                              Double.TYPE, null))).doubleValue(), 0.00001D);
        }
    }

    @Test
    public void testConvertFloat() {
        Object[] input = {String.valueOf(Float.MIN_VALUE), "-17.2", "-1.1", "0.0", "1.1", "17.2", String.valueOf(Float.MAX_VALUE),
                          new Byte((byte)7), new Short((short)8), new Integer(9), new Long(10), new Float(11.1), new Double(12.2)};
        Float[] expected = {new Float(Float.MIN_VALUE), new Float(-17.2), new Float(-1.1), new Float(0.0), new Float(1.1),
                            new Float(17.2), new Float(Float.MAX_VALUE), new Float(7), new Float(8), new Float(9), new Float(10), new Float(11.1),
                            new Float(12.2)};

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i].getClass().getName() + " to Float", expected[i].floatValue(), ((Float)(converter.convert(input[i],
                                                                                                                           Float.class, null))).floatValue(), 0.00001);
            assertEquals(input[i].getClass().getName() + " to float", expected[i].floatValue(), ((Float)(converter.convert(input[i],
                                                                                                                           Float.TYPE, null))).floatValue(), 0.00001);
        }
    }

    @Test
    public void testConvertLong() {
        Object[] input = {String.valueOf(Long.MIN_VALUE), "-17", "-1", "0", "1", "17", String.valueOf(Long.MAX_VALUE),
                          new Byte((byte)7), new Short((short)8), new Integer(9), new Long(10), new Float(11.1), new Double(12.2), Boolean.TRUE,
                          Boolean.FALSE};
        Long[] expected = {new Long(Long.MIN_VALUE), new Long(-17), new Long(-1), new Long(0), new Long(1), new Long(17),
                           new Long(Long.MAX_VALUE), new Long(7), new Long(8), new Long(9), new Long(10), new Long(11), new Long(12), new Long(1),
                           new Long(0)};

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i].getClass().getName() + " to Long", expected[i], converter.convert(input[i], Long.class, null));
            assertEquals(input[i].getClass().getName() + " to long", expected[i], converter.convert(input[i], Long.TYPE, null));
        }
    }

    @Test
    public void testConvertBigDecimal() {
        Object[] input = {String.valueOf(Integer.MIN_VALUE), "-17", "-1", "0", "1", "17", String.valueOf(Integer.MAX_VALUE),
                          new Byte((byte)7), new Short((short)8), new Integer(9), new Long(10),};
        BigDecimal[] expected = {new BigDecimal(Integer.MIN_VALUE), new BigDecimal(-17), new BigDecimal(-1), new BigDecimal(0),
                                 new BigDecimal(1), new BigDecimal(17), new BigDecimal(Integer.MAX_VALUE), new BigDecimal(7), new BigDecimal(8),
                                 new BigDecimal(9), new BigDecimal(10)};

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i].getClass().getName() + " to BigDecimal", expected[i], converter.convert(input[i], BigDecimal.class,
                                                                                                          null));
        }
    }

    @Test
    public void testConvertBigInteger() {
        Object[] input = {String.valueOf(Integer.MIN_VALUE), "-17", "-1", "0", "1", "17", String.valueOf(Integer.MAX_VALUE),
                          new Byte((byte)7), new Short((short)8), new Integer(9), new Long(10)};
        BigInteger[] expected = {new BigInteger(String.valueOf(Integer.MIN_VALUE)), new BigInteger("-17"), new BigInteger("-1"),
                                 new BigInteger("0"), new BigInteger("1"), new BigInteger("17"), new BigInteger(String.valueOf(Integer.MAX_VALUE)),
                                 new BigInteger("7"), new BigInteger("8"), new BigInteger("9"), new BigInteger("10")};

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i].getClass().getName() + " to BigDecimal", expected[i], converter.convert(input[i], BigInteger.class,
                                                                                                          null));
        }
    }

    @Test
    public void testConvertShort() {
        Object[] input = {String.valueOf(Short.MIN_VALUE), "-17", "-1", "0", "1", "17", String.valueOf(Short.MAX_VALUE),
                          new Byte((byte)7), new Short((short)8), new Integer(9), new Long(10), new Float(11.1), new Double(12.2)};
        Short[] expected = {new Short(Short.MIN_VALUE), new Short((short)-17), new Short((short)-1), new Short((short)0),
                            new Short((short)1), new Short((short)17), new Short(Short.MAX_VALUE), new Short((short)7), new Short((short)8),
                            new Short((short)9), new Short((short)10), new Short((short)11), new Short((short)12)};

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i].getClass().getName() + " to Short", expected[i], converter.convert(input[i], Short.class, null));
            assertEquals(input[i].getClass().getName() + " to short", expected[i], converter.convert(input[i], Short.TYPE, null));
        }
    }

    @Test
    public void testConvertDate() throws Exception {
        long time = Calendar.getInstance().getTimeInMillis();

        java.util.Date date = new java.util.Date(time);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        GregorianCalendar gregCal = new GregorianCalendar();
        gregCal.setTimeInMillis(time);

        XMLGregorianCalendar xmlGregCal = getXMLGregorianCalendar(time);

        DateFormat[] dateFormats = new DateFormat[] {DateFormat.getDateInstance(DateFormat.FULL),
                                                     DateFormat.getDateInstance(DateFormat.LONG), DateFormat.getDateInstance(DateFormat.MEDIUM),
                                                     new SimpleDateFormat("MM/dd/yyyy"), new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS")};

        // java.util.Date
        Object[] input = {new java.sql.Time(time), new java.sql.Timestamp(time), new java.sql.Date(time), cal, gregCal, xmlGregCal,
                          String.valueOf(time)};
        Object expected = new java.util.Date(time);
        Object result = null;

        for (Object anInput : input) {
            DateFormatContainer dfc = new DateFormatContainer(null);
            result = converter.convert(anInput, Date.class, dfc);
            assertTrue("result should be instance of java.util.Date", result instanceof Date);
            assertEquals(anInput.getClass().getName() + " to java.util.Date", expected, result);
        }

        for (DateFormat dateFormat : dateFormats) {
            String dateStr = dateFormat.format(date);
            DateFormatContainer dfc = new DateFormatContainer(null);
            dfc.setDateFormat(dateFormat);
            result = converter.convert(dateStr, Date.class, dfc);
            assertEquals("String to java.util.Date for input: " + dateStr, dateFormat.parse(dateStr), result);
            assertEquals("String to java.util.Date for input: " + dateStr, dateStr, dateFormat.format(result));
        }

        // java.sql.Date
        input = new Object[] {new java.util.Date(time), new java.sql.Time(time), new java.sql.Timestamp(time), cal, gregCal,
                              xmlGregCal, String.valueOf(time)};
        expected = new java.sql.Date(time);

        for (Object anInput : input) {
            DateFormatContainer dfc = new DateFormatContainer(null);
            result = converter.convert(anInput, java.sql.Date.class, dfc);
            assertTrue("result should be instance of java.sql.Date", result instanceof java.sql.Date);
            assertEquals(anInput.getClass().getName() + " to java.sql.Date", expected, result);
        }

        for (DateFormat dateFormat : dateFormats) {
            String dateStr = dateFormat.format(date);
            DateFormatContainer dfc = new DateFormatContainer(null);
            dfc.setDateFormat(dateFormat);
            result = converter.convert(dateStr, java.sql.Date.class, dfc);
            assertEquals("String to java.sql.Date for input: " + dateStr, dateFormat.parse(dateStr), result);
            assertEquals("String to java.sql.Date for input: " + dateStr, dateStr, dateFormat.format(result));
        }

        // java.sql.Time
        input = new Object[] {new java.util.Date(time), new java.sql.Date(time), new java.sql.Timestamp(time), cal, gregCal,
                              xmlGregCal, String.valueOf(time)};
        expected = new java.sql.Time(time);

        for (Object anInput : input) {
            DateFormatContainer dfc = new DateFormatContainer(null);
            result = converter.convert(anInput, Time.class, dfc);
            assertTrue("result should be instance of java.sql.Time", result instanceof Time);
            assertEquals(anInput.getClass().getName() + " to java.sql.Time", expected, result);
        }

        for (DateFormat dateFormat : dateFormats) {
            String dateStr = dateFormat.format(date);
            DateFormatContainer dfc = new DateFormatContainer(null);
            dfc.setDateFormat(dateFormat);
            result = converter.convert(dateStr, Time.class, dfc);
            assertEquals("String to java.sql.Time for input: " + dateStr, dateFormat.parse(dateStr), result);
            assertEquals("String to java.sql.Time for input: " + dateStr, dateStr, dateFormat.format(result));
        }

        // java.sql.Timestamp
        input = new Object[] {new java.util.Date(time), new java.sql.Date(time), new java.sql.Time(time), cal, gregCal, xmlGregCal,
                              String.valueOf(time)};

        for (Object anInput : input) {
            DateFormatContainer dfc = new DateFormatContainer(null);
            result = converter.convert(anInput, Timestamp.class, dfc);
            assertTrue("result should be instance of java.sql.Timestamp", result instanceof Timestamp);
            assertEquals(anInput.getClass().getName() + " to java.sql.Timestamp", time, ((Timestamp)result).getTime());
        }

        for (int i = 0; i < dateFormats.length; i++) {
            String dateStr = dateFormats[i].format(date);
            DateFormatContainer dfc = new DateFormatContainer(null);
            dfc.setDateFormat(dateFormats[i]);
            result = converter.convert(dateStr, java.sql.Timestamp.class, dfc);
            assertEquals("String to java.sql.Timestamp for input: " + dateStr, dateFormats[i].parse(dateStr), result);
            assertEquals("String to java.sql.Timestamp for input: " + dateStr, dateStr, dateFormats[i].format(result));
        }

        // java.util.Calendar
        input = new Object[] {new java.util.Date(time), new java.sql.Date(time), new java.sql.Time(time),
                              new java.sql.Timestamp(time), gregCal, xmlGregCal, String.valueOf(time)};

        for (int i = 0; i < input.length; i++) {
            DateFormatContainer dfc = new DateFormatContainer(null);
            result = converter.convert(input[i], java.util.Calendar.class, dfc);
            assertTrue("result should be instance of java.util.Calendar", result instanceof java.util.Calendar);
            assertEquals(input[i].getClass().getName() + " to java.util.Calendar", time, ((java.util.Calendar)result).getTimeInMillis());
        }

        for (int i = 0; i < dateFormats.length; i++) {
            String dateStr = dateFormats[i].format(date);
            DateFormatContainer dfc = new DateFormatContainer(null);
            dfc.setDateFormat(dateFormats[i]);
            result = converter.convert(dateStr, java.util.Calendar.class, dfc);
            assertEquals("String to java.util.Calendar for input: " + dateStr, dateFormats[i].parse(dateStr), ((Calendar)result)
                    .getTime());
        }

        // java.util.GregorianCalendar
        input = new Object[] {new java.util.Date(time), new java.sql.Date(time), new java.sql.Time(time),
                              new java.sql.Timestamp(time), cal, xmlGregCal, String.valueOf(time)};

        for (int i = 0; i < input.length; i++) {
            DateFormatContainer dfc = new DateFormatContainer(null);
            result = converter.convert(input[i], java.util.GregorianCalendar.class, dfc);
            assertTrue("result should be instance of java.util.GregorianCalendar", result instanceof java.util.GregorianCalendar);
            assertEquals(input[i].getClass().getName() + " to java.util.GregorianCalendar", time, ((java.util.GregorianCalendar)result)
                    .getTimeInMillis());
        }

        for (int i = 0; i < dateFormats.length; i++) {
            String dateStr = dateFormats[i].format(date);
            DateFormatContainer dfc = new DateFormatContainer(null);
            dfc.setDateFormat(dateFormats[i]);
            result = converter.convert(dateStr, java.util.GregorianCalendar.class, dfc);
            assertEquals("String to java.util.GregorianCalendar for input: " + dateStr, dateFormats[i].parse(dateStr),
                         ((GregorianCalendar)result).getTime());
        }

        // javax.xml.datatype.XMLGregorianCalendar
        input = new Object[] {new java.util.Date(time), new java.sql.Date(time), new java.sql.Time(time),
                              new java.sql.Timestamp(time), cal, gregCal, String.valueOf(time)};

        for (int i = 0; i < input.length; i++) {
            DateFormatContainer dfc = new DateFormatContainer(null);
            result = converter.convert(input[i], XMLGregorianCalendar.class, dfc);
            assertTrue("result should be instance of javax.xml.datatype.XMLGregorianCalendar", result instanceof XMLGregorianCalendar);
            assertEquals(input[i].getClass().getName() + " to javax.xml.datatype.XMLGregorianCalendar", time,
                         ((XMLGregorianCalendar)result).toGregorianCalendar().getTimeInMillis());
        }

        for (int i = 0; i < dateFormats.length; i++) {
            String dateStr = dateFormats[i].format(date);
            DateFormatContainer dfc = new DateFormatContainer(null);
            dfc.setDateFormat(dateFormats[i]);
            result = converter.convert(dateStr, XMLGregorianCalendar.class, dfc);
            assertEquals("String to javax.xml.datatype.XMLGregorianCalendar for input: " + dateStr, dateFormats[i].parse(dateStr),
                         ((XMLGregorianCalendar)result).toGregorianCalendar().getTime());
        }

        // invalid mappings
        Class[] classes = new Class[] {java.util.Date.class, java.sql.Date.class, java.sql.Time.class, java.sql.Timestamp.class,
                                       java.util.Calendar.class, java.util.GregorianCalendar.class, XMLGregorianCalendar.class};
        String invalidInputStr = "dflksjf";
        for (int i = 0; i < classes.length; i++) {
            try {
                converter.convert(invalidInputStr, classes[i], null);
                Assert.fail("mapping value " + invalidInputStr + "to class " + classes[i].getName() + " should have thrown a mapping ex");
            } catch (Throwable e) {
                assertTrue(true);
            }

            assertNull("mapping null value to class " + classes[i].getName() + " should result in null", converter.convert(null,
                                                                                                                           classes[i], null));
        }
    }

    private XMLGregorianCalendar getXMLGregorianCalendar(long millis) {
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(millis);
        XMLGregorianCalendar cal = null;

        try {
            cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (Exception ignore) {
        }

        return cal;
    }

    @Test
    public void testConvertBoolean() {
        Object[] input = {Integer.valueOf("0"), Integer.valueOf("1")};
        Boolean[] expected = {Boolean.valueOf(false), Boolean.valueOf(true)};

        for (int i = 0; i < expected.length; i++) {
            assertEquals(input[i].getClass().getName() + " to Boolean", expected[i], converter.convert(input[i], Boolean.class, null));
            assertEquals(input[i].getClass().getName() + " to boolean", expected[i], converter.convert(input[i], Boolean.TYPE, null));
        }
    }

    @Test
    public void testConvertStringPositiveScalar() {
        Object value = converter.convert("true", Boolean.TYPE, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), true);

        value = converter.convert("true", Boolean.class, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), true);

        value = converter.convert("yes", Boolean.TYPE, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), true);

        value = converter.convert("yes", Boolean.class, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), true);

        value = converter.convert("y", Boolean.TYPE, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), true);

        value = converter.convert("y", Boolean.class, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), true);

        value = converter.convert("on", Boolean.TYPE, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), true);

        value = converter.convert("on", Boolean.class, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), true);

        value = converter.convert("1", Boolean.class, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), true);

        value = converter.convert("0", Boolean.class, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), false);

        value = converter.convert("false", Boolean.TYPE, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), false);

        value = converter.convert("false", Boolean.class, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), false);

        value = converter.convert("no", Boolean.TYPE, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), false);

        value = converter.convert("no", Boolean.class, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), false);

        value = converter.convert("n", Boolean.TYPE, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), false);

        value = converter.convert("n", Boolean.class, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), false);

        value = converter.convert("off", Boolean.TYPE, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), false);

        value = converter.convert("off", Boolean.class, null);

        assertTrue(value instanceof Boolean);
        assertEquals(((Boolean)value).booleanValue(), false);

        value = converter.convert("123", Byte.TYPE, null);

        assertTrue(value instanceof Byte);
        assertEquals(((Byte)value).byteValue(), (byte)123);

        value = converter.convert("123", Byte.class, null);

        assertTrue(value instanceof Byte);
        assertEquals(((Byte)value).byteValue(), (byte)123);

        value = converter.convert("a", Character.TYPE, null);

        assertTrue(value instanceof Character);
        assertEquals(((Character)value).charValue(), 'a');

        value = converter.convert("a", Character.class, null);

        assertTrue(value instanceof Character);
        assertEquals(((Character)value).charValue(), 'a');

        value = converter.convert("123.456", Double.TYPE, null);

        assertTrue(value instanceof Double);
        assertEquals(((Double)value).doubleValue(), 123.456, 0.005);

        value = converter.convert("123.456", Double.class, null);

        assertTrue(value instanceof Double);
        assertEquals(((Double)value).doubleValue(), 123.456, 0.005);

        value = converter.convert("123.456", Float.TYPE, null);

        assertTrue(value instanceof Float);
        assertEquals(((Float)value).floatValue(), (float)123.456, (float)0.005);

        value = converter.convert("123.456", Float.class, null);

        assertTrue(value instanceof Float);
        assertEquals(((Float)value).floatValue(), (float)123.456, (float)0.005);

        value = converter.convert("123", Integer.TYPE, null);

        assertTrue(value instanceof Integer);
        assertEquals(((Integer)value).intValue(), 123);

        value = converter.convert("123", Integer.class, null);

        assertTrue(value instanceof Integer);
        assertEquals(((Integer)value).intValue(), 123);

        value = converter.convert("123", Long.TYPE, null);

        assertTrue(value instanceof Long);
        assertEquals(((Long)value).longValue(), (long)123);

        value = converter.convert("123", Long.class, null);

        assertTrue(value instanceof Long);
        assertEquals(((Long)value).longValue(), (long)123);

        value = converter.convert("123", Short.TYPE, null);

        assertTrue(value instanceof Short);
        assertEquals(((Short)value).shortValue(), (short)123);

        value = converter.convert("123", Short.class, null);

        assertTrue(value instanceof Short);
        assertEquals(((Short)value).shortValue(), (short)123);
    }

    @Test(expected = ConversionException.class)
    public void testConvertStringNegativeScalar() {
        converter.convert("foo", Boolean.TYPE, null);
    }

    @Test(expected = ConversionException.class)
    public void testConvertStringNegativeScalar2() {
        converter.convert("foo", Boolean.class, null);
    }

    @Test
    public void shouldConvertUrl() throws Exception {
        URL result = (URL)converter.convert("http://hello", URL.class, null);
        assertThat(result, equalTo(new URL("http://hello")));
    }

    @Test
    public void shouldConvertFile() {
        File result = (File)converter.convert("hello", File.class, null);
        assertThat(result, equalTo(new File("hello")));
    }

    @Test
    public void shouldConvertClass() {
        Class<Date> result = (Class<Date>)converter.convert("java.util.Date", Class.class, null);
        assertThat(result, equalTo(Date.class));
    }

}
