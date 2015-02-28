/**
 * Copyright 2005-2013 Dozer Project
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
package org.dozer.converters.custom;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.datatype.XMLGregorianCalendar;

import org.dozer.AbstractDozerTest;
import org.junit.Before;
import org.junit.Test;

public class XmlDateConverterTest extends AbstractDozerTest
{
	private static final String DATE_XML = "2015-02-22T01:59:28.766+00:00";
	private static final long DATE_MS = 1424570368766L;
	private static final int DATE_YEAR = 2015;
	private static final int DATE_MONTH = 2;
	private static final int DATE_DAY = 22;
	private static final int DATE_HOUR = 1;
	private static final int DATE_MINUTE = 59;
	private static final int DATE_SECOND = 28;
	private static final int DATE_FRACTION = 766;
	private static final TimeZone DATE_TZ = TimeZone.getTimeZone("UTC");
	  
	private XmlDateConverter converter;

	@Before
	public void setUp() throws Exception
	{
		converter = new XmlDateConverter();
	}

	@Test
	public void testXmlDateConverterDefaults() throws Exception 
	{
		assertEquals(XmlDateConverter.ISO8601, converter.getParameter());
		assertNotNull(converter.getConverter());
		assertNull(converter.convertFrom(null));
		assertNull(converter.convertTo(null));
	}
	
	@Test
	public void testConvertFrom() throws Exception 
	{
		XMLGregorianCalendar xgcNow = XmlDateConverter.now();
		String xmlNow = converter.convertFrom(xgcNow);
		assertEquals(xgcNow.toXMLFormat(), xmlNow);
	}
	
	@Test
	public void testXMLConvertTo() throws Exception 
	{
		XMLGregorianCalendar xgc = converter.convertTo(DATE_XML);
		assertMillis(DATE_MS, xgc);
		assertYear(DATE_YEAR, xgc);
		assertMonth(DATE_MONTH, xgc);
		assertDay(DATE_DAY, xgc);
		assertHour(DATE_HOUR, xgc);
		assertMinute(DATE_MINUTE, xgc);
		assertSecond(DATE_SECOND, xgc);
		assertFraction(DATE_FRACTION, xgc);
	}

	@Test
	public void testUtilDateConvertTo() throws Exception 
	{
		assertMillis(DATE_MS, converter.convertTo(new java.util.Date(DATE_MS)));
	}

	@Test
	public void testSqlDateConvertTo() throws Exception 
	{
		assertMillis(DATE_MS, converter.convertTo(new java.sql.Date(DATE_MS)));
	}

	@Test
	public void testCalendarConvertTo() throws Exception 
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(DATE_MS);
		assertMillis(DATE_MS, converter.convertTo(cal));
	}

	@Test
	public void testXGCConvertTo() throws Exception 
	{
		XMLGregorianCalendar xgcNow = XmlDateConverter.now();
		assertMillis(xgcNow, converter.convertTo(xgcNow));
	}

	@Test
	public void testMillisConvertTo() throws Exception 
	{
		assertMillis(DATE_MS, converter.convertTo(DATE_MS));
	}

	@Test
	public void testPattern() throws Exception 
	{
		// 'T' delimited
		assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", XmlDateConverter.pattern("2015-02-22T01:59:28.766+00:00"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ssXXX", XmlDateConverter.pattern("2015-02-22T01:59:28+00:00"));
		assertEquals("yyyy-MM-dd'T'HH:mmXXX", XmlDateConverter.pattern("2015-02-22T01:59+00:00"));
		assertEquals("yyyy-MM-ddXXX", XmlDateConverter.pattern("2015-02-22+00:00"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSXX", XmlDateConverter.pattern("2015-02-22T01:59:28.766+0000"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ssXX", XmlDateConverter.pattern("2015-02-22T01:59:28+0000"));
		assertEquals("yyyy-MM-dd'T'HH:mmXX", XmlDateConverter.pattern("2015-02-22T01:59+0000"));
		assertEquals("yyyy-MM-ddXX", XmlDateConverter.pattern("2015-02-22+0000"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSX", XmlDateConverter.pattern("2015-02-22T01:59:28.766+00"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ssX", XmlDateConverter.pattern("2015-02-22T01:59:28+00"));
		assertEquals("yyyy-MM-dd'T'HH:mmX", XmlDateConverter.pattern("2015-02-22T01:59+00"));
		assertEquals("yyyy-MM-ddX", XmlDateConverter.pattern("2015-02-22+00"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", XmlDateConverter.pattern("2015-02-22T01:59:28.766Z"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ssXXX", XmlDateConverter.pattern("2015-02-22T01:59:28Z"));
		assertEquals("yyyy-MM-dd'T'HH:mmXXX", XmlDateConverter.pattern("2015-02-22T01:59Z"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSS z", XmlDateConverter.pattern("2015-02-22T01:59:28.766 GMT"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss z", XmlDateConverter.pattern("2015-02-22T01:59:28 GMT"));
		assertEquals("yyyy-MM-dd'T'HH:mm z", XmlDateConverter.pattern("2015-02-22T01:59 GMT"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSS", XmlDateConverter.pattern("2015-02-22T01:59:28.766"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss", XmlDateConverter.pattern("2015-02-22T01:59:28"));
		assertEquals("yyyy-MM-dd'T'HH:mm", XmlDateConverter.pattern("2015-02-22T01:59"));

		// ' ' delimited maps to 'T' patterns
		assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", XmlDateConverter.pattern("2015-02-22 01:59:28.766+00:00"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ssXXX", XmlDateConverter.pattern("2015-02-22 01:59:28+00:00"));
		assertEquals("yyyy-MM-dd'T'HH:mmXXX", XmlDateConverter.pattern("2015-02-22 01:59+00:00"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSXX", XmlDateConverter.pattern("2015-02-22 01:59:28.766+0000"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ssXX", XmlDateConverter.pattern("2015-02-22 01:59:28+0000"));
		assertEquals("yyyy-MM-dd'T'HH:mmXX", XmlDateConverter.pattern("2015-02-22 01:59+0000"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSX", XmlDateConverter.pattern("2015-02-22 01:59:28.766+00"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ssX", XmlDateConverter.pattern("2015-02-22 01:59:28+00"));
		assertEquals("yyyy-MM-dd'T'HH:mmX", XmlDateConverter.pattern("2015-02-22 01:59+00"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", XmlDateConverter.pattern("2015-02-22 01:59:28.766Z"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ssXXX", XmlDateConverter.pattern("2015-02-22 01:59:28Z"));
		assertEquals("yyyy-MM-dd'T'HH:mmXXX", XmlDateConverter.pattern("2015-02-22 01:59Z"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSS z", XmlDateConverter.pattern("2015-02-22 01:59:28.766 GMT"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss z", XmlDateConverter.pattern("2015-02-22 01:59:28 GMT"));
		assertEquals("yyyy-MM-dd'T'HH:mm z", XmlDateConverter.pattern("2015-02-22 01:59 GMT"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss.SSS", XmlDateConverter.pattern("2015-02-22 01:59:28.766"));
		assertEquals("yyyy-MM-dd'T'HH:mm:ss", XmlDateConverter.pattern("2015-02-22 01:59:28"));
		assertEquals("yyyy-MM-dd'T'HH:mm", XmlDateConverter.pattern("2015-02-22 01:59"));
	}
	
	@Test
	public void testPatternConvertTo() throws Exception 
	{
		assertMillis(DATE_MS       , converter.convertTo("2015-02-22T01:59:28.766+00:00"));
		assertMillis(1424570368000L, converter.convertTo("2015-02-22T01:59:28+00:00"));
		assertMillis(1424570340000L, converter.convertTo("2015-02-22T01:59+00:00"));
		assertMillis(1424563200000L, converter.convertTo("2015-02-22+00:00"));

		assertMillis(DATE_MS       , converter.convertTo("2015-02-22T01:59:28.766+0000"));
		assertMillis(1424570368000L, converter.convertTo("2015-02-22T01:59:28+0000"));
		assertMillis(1424570340000L, converter.convertTo("2015-02-22T01:59+0000"));
		assertMillis(1424563200000L, converter.convertTo("2015-02-22+0000"));

		assertMillis(DATE_MS       , converter.convertTo("2015-02-22T01:59:28.766+00"));
		assertMillis(1424570368000L, converter.convertTo("2015-02-22T01:59:28+00"));
		assertMillis(1424570340000L, converter.convertTo("2015-02-22T01:59+00"));
		assertMillis(1424563200000L, converter.convertTo("2015-02-22+00"));

		assertMillis(DATE_MS       , converter.convertTo("2015-02-22T01:59:28.766Z"));
		assertMillis(1424570368000L, converter.convertTo("2015-02-22T01:59:28Z"));
		assertMillis(1424570340000L, converter.convertTo("2015-02-22T01:59Z"));

		assertMillis(DATE_MS       , converter.convertTo("2015-02-22T01:59:28.766 GMT"));
		assertMillis(1424570368000L, converter.convertTo("2015-02-22T01:59:28 GMT"));
		assertMillis(1424570340000L, converter.convertTo("2015-02-22T01:59 GMT"));
		
		assertMillis(DATE_MS       , converter.convertTo("2015-02-22 01:59:28.766+00:00"));
		assertMillis(1424570368000L, converter.convertTo("2015-02-22 01:59:28+00:00"));
		assertMillis(1424570340000L, converter.convertTo("2015-02-22 01:59+00:00"));

		assertMillis(DATE_MS       , converter.convertTo("2015-02-22 01:59:28.766+0000"));
		assertMillis(1424570368000L, converter.convertTo("2015-02-22 01:59:28+0000"));
		assertMillis(1424570340000L, converter.convertTo("2015-02-22 01:59+0000"));

		assertMillis(DATE_MS       , converter.convertTo("2015-02-22 01:59:28.766+00"));
		assertMillis(1424570368000L, converter.convertTo("2015-02-22 01:59:28+00"));
		assertMillis(1424570340000L, converter.convertTo("2015-02-22 01:59+00"));

		assertMillis(DATE_MS       , converter.convertTo("2015-02-22 01:59:28.766Z"));
		assertMillis(1424570368000L, converter.convertTo("2015-02-22 01:59:28Z"));
		assertMillis(1424570340000L, converter.convertTo("2015-02-22 01:59Z"));
	
		assertMillis(DATE_MS       , converter.convertTo("2015-02-22 01:59:28.766 GMT"));
		assertMillis(1424570368000L, converter.convertTo("2015-02-22 01:59:28 GMT"));
		assertMillis(1424570340000L, converter.convertTo("2015-02-22 01:59 GMT"));
		
		// Calculate time zone offset in milliseconds. 
		Calendar zulu  = converter.convertTo("2015-02-22T01:59:28.766Z").toGregorianCalendar();
		Calendar local = converter.convertTo("2015-02-22T01:59:28.766").toGregorianCalendar();
		long TZMS = local.getTimeInMillis() - zulu.getTimeInMillis();

		assertMillis(DATE_MS+TZMS       , converter.convertTo("2015-02-22T01:59:28.766"));
		assertMillis(1424570368000L+TZMS, converter.convertTo("2015-02-22T01:59:28"));
		assertMillis(1424570340000L+TZMS, converter.convertTo("2015-02-22T01:59"));
		             
		assertMillis(DATE_MS+TZMS       , converter.convertTo("2015-02-22 01:59:28.766"));
		assertMillis(1424570368000L+TZMS, converter.convertTo("2015-02-22 01:59:28"));
		assertMillis(1424570340000L+TZMS, converter.convertTo("2015-02-22 01:59"));
		assertMillis(1424563200000L+TZMS, converter.convertTo("2015-02-22"));

		Calendar edt = converter.convertTo("2015-02-22T01:59:28.766 EDT").toGregorianCalendar();
		TZMS = edt.getTimeInMillis() - zulu.getTimeInMillis();

		assertMillis(DATE_MS+TZMS       , converter.convertTo("2015-02-22T01:59:28.766 EDT"));
		assertMillis(1424570368000L+TZMS, converter.convertTo("2015-02-22T01:59:28 EDT"));
		assertMillis(1424570340000L+TZMS, converter.convertTo("2015-02-22T01:59 EDT"));
		
		// Pacific Daylight Time
		Calendar pdt = converter.convertTo("2015-02-22T01:59:28.766 Pacific Daylight Time").toGregorianCalendar();
		TZMS = pdt.getTimeInMillis() - zulu.getTimeInMillis();

		assertMillis(DATE_MS+TZMS       , converter.convertTo("2015-02-22T01:59:28.766 Pacific Daylight Time"));
		assertMillis(1424570368000L+TZMS, converter.convertTo("2015-02-22T01:59:28 Pacific Daylight Time"));
		assertMillis(1424570340000L+TZMS, converter.convertTo("2015-02-22T01:59 Pacific Daylight Time"));
	}
	
	private void assertMillis(XMLGregorianCalendar xgc1, XMLGregorianCalendar xgc2)
	{
		assertMillis(xgc1.toGregorianCalendar().getTimeInMillis(), xgc2);
	}
	
	private void assertMillis(long millis, XMLGregorianCalendar xgc)
	{
		assertEquals(millis, xgc.toGregorianCalendar().getTimeInMillis());
	}
	
	private void assertYear(int year, XMLGregorianCalendar xgc)
	{
		assertEquals(year, xgc.getYear());
	}

	private void assertMonth(int month, XMLGregorianCalendar xgc)
	{
		assertEquals(month, xgc.getMonth());
	}

	private void assertDay(int day, XMLGregorianCalendar xgc)
	{
		assertEquals(day, xgc.getDay() + 1);
	}

	private void assertHour(int hour, XMLGregorianCalendar xgc)
	{
		assertEquals(60 * hour, (60 * xgc.getHour() - xgc.getTimezone()) % 1440);
	}

	private void assertMinute(int minute, XMLGregorianCalendar xgc)
	{
		assertEquals(minute, xgc.getMinute());
	}

	private void assertSecond(int second, XMLGregorianCalendar xgc)
	{
		assertEquals(second, xgc.getSecond());
	}

	private void assertFraction(int fraction, XMLGregorianCalendar xgc)
	{
		assertEquals(fraction, xgc.getFractionalSecond().movePointRight(3).intValue());
	}
}
