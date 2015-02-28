/*
 * Copyright 2005-2010 the original author or authors.
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

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.dozer.DozerConverter;
import org.dozer.MappingException;
import org.dozer.converters.ConversionException;
import org.dozer.converters.XMLGregorianCalendarConverter;
import org.dozer.loader.api.FieldsMappingOption;
import org.dozer.loader.api.FieldsMappingOptions;

/**
 * A Dozer Converter to convert a Date-like object to an {@link XMLGregorianCalendar} object.
 * Provides a helper method to create a {@link FieldsMappingOption}. When possible uses 
 * {@link XMLGregorianCalendarConverter} to convert Date, Calendar, XMLGregorianCalendar, 
 * 'date literal' or millisecond values; otherwise, the source is interpreted as a arbitrary
 * date-time literal and matched to a best possible SimpleDateFormat pattern.
 * 
 * For example, to create a {@link FieldsMappingOption} to map an ISO8601 formatted date-time 
 * literal to an {@link XMLGregorianCalendarConverter}, use this:
 * 
 * <code>XmlDateConverter.convertXmlDate(XmlDateConverter.ISO8601)</code>
 * 
 * IMPORTANT: Initialize with the longest pattern that works for your date time values 
 *            to achieve the best probable conversion. The recommended pattern is ISO8601.
 * 
 * The configured pattern is used to parse literal date-time strings. If the parse fails,
 * the default exception handler looks for a pattern that matche the literal value. This
 * behavior can be disabled using the 'strict' property.  
 * 
 * @author Rick O'Sullivan
 */
public class XmlDateConverter extends DozerConverter<Object, XMLGregorianCalendar>
{
	// @see java.text.SimpleDateFormat
	//   OneLetterISO8601TimeZone   is   'X' for 'Sign TwoDigitHours' (ex: -05) or Z.
	//   TwoLetterISO8601TimeZone   is  'XX' for 'Sign TwoDigitHours Minutes' (ex: -0500) or Z.
	//   ThreeLetterISO8601TimeZone is 'XXX' for 'Sign TwoDigitHours : Minutes' (ex: -05:00) or Z.
	public static final String ISO8601       = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	public static final String ISO8601SSSXXX = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	public static final String ISO8601SSSXX  = "yyyy-MM-dd'T'HH:mm:ss.SSSXX";
	public static final String ISO8601SSSX   = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
	public static final String ISO8601XXX    = "yyyy-MM-dd'T'HH:mm:ssXXX";
	public static final String ISO8601XX     = "yyyy-MM-dd'T'HH:mm:ssXX";
	public static final String ISO8601X      = "yyyy-MM-dd'T'HH:mm:ssX";
	public static final String ISO8601SSSZ   = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String ISO8601Z      = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	/**
	 * Get parameter containing a {@link SimpleDateFormat} pattern.
	 * @return a date time pattern.
	 */
	@Override
	public String getParameter()
	{
		try
		{
			return super.getParameter();
		}
		catch ( IllegalStateException ex)
		{
			setParameter(ISO8601);
			return super.getParameter();
		}
	}
	
	private boolean strict;
	/**
	 * Is strict pattern lookup active.
	 * @return True when pattern lookup is off; otherwise false.
	 */
	public boolean isStrict() {
		return strict;
	}
	/**
	 * Set strict pattern lookup behavior.
	 * @param strict True to prevent pattern lookups.
	 */
	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	private XMLGregorianCalendarConverter converter;
	protected XMLGregorianCalendarConverter getConverter()
	{
		if ( converter == null )
			converter = new XMLGregorianCalendarConverter(new SimpleDateFormat(getParameter()));
		return converter;
	}
	protected void setConverter(XMLGregorianCalendarConverter converter)
	{
		this.converter = converter;
	}

	private static DatatypeFactory datatypeFactory;
	protected static DatatypeFactory getDatatypeFactory() 
	{
		try
		{
			if ( datatypeFactory == null )
				datatypeFactory = DatatypeFactory.newInstance();
			return datatypeFactory;
		}
		catch (DatatypeConfigurationException dce)
		{
			throw new MappingException(dce);
		}
	}
	
	/**
	 * Create a XMLGregorianCalendar instance for the current time
	 * in the default time zone with the default locale.
	 * 
	 * @return a current MLGregorianCalendar instance.
	 */
	public static XMLGregorianCalendar now()
	{
		return now(null);
	}
	
	/**
	 * Create a XMLGregorianCalendar instance for the current time
	 * in the specified time zone with the default locale.
	 * 
	 * @param tz the specified time zone.
	 * 
	 * @return a current MLGregorianCalendar instance.
	 */
	public static XMLGregorianCalendar now(TimeZone tz)
	{
		GregorianCalendar gc = (tz == null ) ? new GregorianCalendar() : new GregorianCalendar(tz);
		return getDatatypeFactory().newXMLGregorianCalendar(gc);
	}
	
	/**
	 * Default constructor with Object and XMLGregorianCalendar prototypes.
	 */
	public XmlDateConverter()
	{
		super(Object.class, XMLGregorianCalendar.class);
	}

	/**
	 * Defines two types, which will take part in the transformation. As Dozer supports
	 * bi-directional mapping it is not known which of the classes is source and
	 * which is destination. It will be decided in runtime.
	 *
	 * @param prototypeA First transformation type.
	 * @param prototypeB Second transformation type.
	 */
	public XmlDateConverter(Class<Object> prototypeA, Class<XMLGregorianCalendar> prototypeB)
	{
		super(prototypeA, prototypeB);
	}

	/**
	 * Converts the a date-like source field into a XMLGregorianCalendar field.
	 *
	 * @param source the field containing a date-like value.
	 * 
	 * @return an instance of XMLGregorianCalendar.
	 */
	@Override
	public XMLGregorianCalendar convertTo(Object source)
	{
		return convertTo(source, null);
	}
	
	/**
	 * Converts the a date-like source field into a XMLGregorianCalendar field.
	 *
	 * @param source      the field containing a date-like value.
	 * @param destination the current value of the XMLGregorianCalendar field (or null)
	 * 
	 * @return an instance of XMLGregorianCalendar.
	 * 
	 * @see org.dozer.DozerConverter#convertTo(Object, Object)
	 */
	@Override
	public XMLGregorianCalendar convertTo(Object source, XMLGregorianCalendar destination)
	{
		try
		{
			// First, attempt to convert the source using XMLGregorianCalendarConverter. The 
			// XMLGregorianCalendarConverter attempts to converts Date, Calendar, XMLGregorianCalendar,
			// 'date literal' or milliseconds to an instance of XMLGregorianCalendar.
			//
			// Notes:
			//
			// The format for the date literal conversion is provided by this instances Parameter property 
			// which is a SimpleDateFormat pattern describing the date and time.
			//
			// The number of milliseconds since the standard base time (January 1, 1970, 00:00:00 GMT)
			//
			// XMLGregorianCalendarConverter is a org.apache.commons.beanutils.Converter while this class 
			// implements a org.dozer.CustomConverter which can be used to create a FieldsMappingOption
			// with FieldsMappingOptions.customConverter(...). 
			return (source == null) ? null :(XMLGregorianCalendar) getConverter().convert(XMLGregorianCalendar.class, source);
		}
		catch ( ConversionException cex)
		{
			if ( !isStrict() )
			{
				// The first attempt may have been unable to parse the source object using specified date 
				// format or unable to determine time in milliseconds of source object. Let's try to find
				// a better SimpleDateFormat pattern.
				//
				// Attempt to lookup the SimpleDateFormat pattern from the source value.
				String dateValue = dateTimeLiteral(source);
				String dateFormat = XmlDateConverter.rawPattern(dateValue);
				if ( dateFormat != null )
				{
					// Found a possible SimpleDateFormat pattern, reset the Parameter and Converter
					// properties and try again.
					String originalDateFormat = getParameter();
					try
					{
						setParameter(dateFormat);
						setConverter(null);
						return (XMLGregorianCalendar) getConverter().convert(XMLGregorianCalendar.class, dateValue);
					}
					finally
					{
						setParameter(originalDateFormat);
						setConverter(null);
					}
				}
			}
			throw cex;
		}
	}

	@Override
	public String convertFrom(XMLGregorianCalendar source) 
	{
		return convertFrom(source, null);
	}
	
	/**
	 * Converts a XMLGregorianCalendar source field to the destination field.
	 *
	 * @param source      the XMLGregorianCalendar value of the source field
	 * @param destination the current value of the destination field (or null)
	 * 
	 * @return the resulting XML formatted value for the destination field.
	 */
	@Override
	public String convertFrom(XMLGregorianCalendar source, Object destination) 
	{
		return (source != null) ? source.toXMLFormat() : null;
	}
	
	/**
	 * Helper method to create a field option for the default XML date format.
	 * 
	 * @return a field mapping option for ISO8601 date literal.
	 */
	public static FieldsMappingOption convertXmlDate()
	{
		return convertXmlDate(ISO8601);
	}
	
	/**
	 * Helper method to create a field option for the specified date format.
	 * 
	 * @param the SimpleDateFormat pattern describing the date and time format.
	 * 
	 * @return a field mapping option for a custom date literal.
	 */
	public static FieldsMappingOption convertXmlDate(String dateFormat)
	{
		return FieldsMappingOptions.customConverter(XmlDateConverter.class, dateFormat);
	}

	/**
	 * Lookup a {@link SimpleDateFormat} pattern that best matches the given date value.
	 * 
	 * @param dateValue a literal representation of a date value.
	 * 
	 * @return A pattern matching the date value or null for no match.
	 */
	public static String pattern(String dateValue)
	{
		return rawPattern(dateTimeLiteral(dateValue));
	}
	
	private static Pattern YYYY_MM = Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}.*");
	
	// Convert source object into possible date time literal.
	private static String dateTimeLiteral(Object source)
	{
		String dateTimeLiteral = "";
		
		// Unwrap JAXBElement values, etc.
		if ( source instanceof JAXBElement)
		{
			@SuppressWarnings("rawtypes")
			JAXBElement je = (JAXBElement) source;
			dateTimeLiteral = (je.getValue() != null ) ? je.getValue().toString() : "";
		}
		else if ( source != null )
			dateTimeLiteral = source.toString();

		// Prefer 'T' syntax over ' ' syntax.
		if ( YYYY_MM.matcher(dateTimeLiteral).matches() )
			dateTimeLiteral = dateTimeLiteral.replaceFirst(" ", "T");
		
		// Replace any Zulu suffix with ISO8601 zero hour and minutes.
		return dateTimeLiteral.replaceAll("[zZ]$", "+00:00");
	}
	
	// Lookup a {@link SimpleDateFormat} pattern that best matches the given raw date value.
	private static String rawPattern(String dateValue)
	{
		String timePattern = null;
		for ( Pattern pattern : PATTERNS.keySet() )
		{
			if ( pattern.matcher(dateValue).matches() )
			{
				timePattern = (String) PATTERNS.get(pattern);
				break;
			}
		}
		return timePattern;
	}
	
	// Represents a map of compiled regex patterns in decreasing order of complexity.
	private static final Map<Pattern, String> PATTERNS = new LinkedHashMap<Pattern, String>();
	static
	{
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]+[-+][0-9]{2}:[0-9]{2}$"), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}[-+][0-9]{2}:[0-9]{2}$"), "yyyy-MM-dd'T'HH:mm:ssXXX");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}[-+][0-9]{2}:[0-9]{2}$"), "yyyy-MM-dd'T'HH:mmXXX");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}[-+][0-9]{2}:[0-9]{2}$"), "yyyy-MM-ddXXX");
		
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]+[-+][0-9]{4}$"), "yyyy-MM-dd'T'HH:mm:ss.SSSXX");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}[-+][0-9]{4}$"), "yyyy-MM-dd'T'HH:mm:ssXX");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}[-+][0-9]{4}$"), "yyyy-MM-dd'T'HH:mmXX");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}[-+][0-9]{4}$"), "yyyy-MM-ddXX");
		
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]+[-+][0-9]{2}$"), "yyyy-MM-dd'T'HH:mm:ss.SSSX");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}[-+][0-9]{2}$"), "yyyy-MM-dd'T'HH:mm:ssX");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}[-+][0-9]{2}$"), "yyyy-MM-dd'T'HH:mmX");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}[-+][0-9]{2}$"), "yyyy-MM-ddX");
		
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]+[-+][0-9]{4}$"), "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}[-+][0-9]{4}$"), "yyyy-MM-dd'T'HH:mm:ssZ");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}[-+][0-9]{4}$"), "yyyy-MM-dd'T'HH:mmZ");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}[-+][0-9]{4}$"), "yyyy-MM-ddZ");
		
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]+$"), "yyyy-MM-dd'T'HH:mm:ss.SSS");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}$"), "yyyy-MM-dd'T'HH:mm:ss");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}$"), "yyyy-MM-dd'T'HH:mm");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"), "yyyy-MM-dd");
		
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]+ .*"), "yyyy-MM-dd'T'HH:mm:ss.SSS z");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2} .*"), "yyyy-MM-dd'T'HH:mm:ss z");
		PATTERNS.put(Pattern.compile("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2} .*"), "yyyy-MM-dd'T'HH:mm z");
	}
}

