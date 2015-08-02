package org.dozer.converters;

import java.lang.reflect.Method;
import java.text.DateFormat;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;
import org.dozer.util.MappingUtils;
import org.dozer.util.ReflectionUtils;

/**
 * @author Jose Barragan
 */
public class JAXBElementConverter implements Converter {

	private String     destObjClass  = null;
	private String     destFieldName = null;
	private DateFormat dateFormat    = null;

	public JAXBElementConverter(String destObjClass, String destFieldName, DateFormat dateFormat) {
		this.destObjClass = destObjClass;
		this.destFieldName = destFieldName;
		this.dateFormat = dateFormat;
	}

	/**
	 * Cache the ObjectFactory because newInstance is very expensive.
	 */
	private static Object   objectFactory      = null;
	private static Class<?> objectFactoryClass = null;

	/**
	 * Returns a new instance of ObjectFactory, or the cached one if previously created.
	 *
	 * @return instance of ObjectFactory
	 */
	private static Object objectFactory(String destObjClass) {
		String objectFactoryClassName = destObjClass.substring(0, destObjClass.lastIndexOf(".")) + ".ObjectFactory";
		if (objectFactory == null || objectFactoryClass == null || !objectFactoryClass.getCanonicalName().equals(objectFactoryClassName)) {
			objectFactoryClass = MappingUtils.loadClass(objectFactoryClassName);
			objectFactory = ReflectionUtils.newInstance(objectFactoryClass);
		}
		return objectFactory;
	}

	/**
	 * Convert the specified input object into an output object of the
	 * specified type.
	 *
	 * @param type  Data type to which this value should be converted
	 * @param value The input value to be converted
	 * @return The converted value
	 * @throws org.apache.commons.beanutils.ConversionException
	 *          if conversion cannot be performed
	 *          successfully
	 */
	@Override
	public Object convert(Class type, Object value) {

		Object result;
		Object factory = objectFactory(destObjClass);
		Class<?> factoryClass = factory.getClass();
		Class<?> destClass = value.getClass();
		Class<?> valueClass = value.getClass();

		String methodName = "create" + destObjClass.substring(destObjClass.lastIndexOf(".") + 1) + StringUtils.capitalize(destFieldName);
		Method method = null;
		try {
			method = ReflectionUtils.findAMethod(factoryClass, methodName);
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (Class<?> parameterClass : parameterTypes) {
				if (!valueClass.equals(parameterClass)) {
					destClass = parameterClass;
					break;
				}
			}

			Class<?>[] paramTypes = {destClass};
			method = ReflectionUtils.getMethod(factoryClass, methodName, paramTypes);
		} catch (NoSuchMethodException e) {
			MappingUtils.throwMappingException(e);
		}

		Object param = value;
		Converter converter;
		if (java.util.Date.class.isAssignableFrom(valueClass) && !destClass.equals(XMLGregorianCalendar.class)) {
			converter = new DateConverter(dateFormat);
			param = converter.convert(destClass, param);
		} else if (java.util.Calendar.class.isAssignableFrom(valueClass) && !destClass.equals(XMLGregorianCalendar.class)) {
			converter = new CalendarConverter(dateFormat);
			param = converter.convert(destClass, param);
		} else if (XMLGregorianCalendar.class.isAssignableFrom(valueClass) || XMLGregorianCalendar.class.isAssignableFrom(destClass)) {
			converter = new XMLGregorianCalendarConverter(dateFormat);
			param = converter.convert(destClass, param);
		}

		Object[] paramValues = {param};
		result = ReflectionUtils.invoke(method, factory, paramValues);
		return result;
	}

	/**
	 * Resolve the beanId associated to destination field name
	 *
	 * @return
	 */
	public String getBeanId() {
		Class<?> factoryClass = objectFactory(destObjClass).getClass();
		Class<?> destClass = null;
		String methodName = "create" + destObjClass.substring(destObjClass.lastIndexOf(".") + 1) + StringUtils.capitalize(destFieldName);

		try {
			Method method = ReflectionUtils.findAMethod(factoryClass, methodName);
			Class<?>[] parameterTypes = method.getParameterTypes();
			for (Class<?> parameterClass : parameterTypes) {
				destClass = parameterClass;
				break;
			}
		} catch (NoSuchMethodException e) {
			MappingUtils.throwMappingException(e);
		}

		return (destClass != null) ? destClass.getCanonicalName() : null;
	}
}
