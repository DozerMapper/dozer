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
package org.dozer.vo;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.dozer.vo.allowedexceptions.TestException;


/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 * 
 */
public class TestObjectPrime extends BaseTestObject {
  private String onePrime;
  private Integer twoPrime;
  private InsideTestObjectPrime threePrime;
  private InsideTestObjectPrime insideTestObject;
  private java.util.List equalNamedList;
  private java.util.List listForArray;
  private java.util.List theMappedUnequallyNamedList;
  private List arrayForLists;
  private int[] theMappedArray;
  private double thePrimitive;
  private int anotherPrimitive;
  private int bigDecimalToInt;
  private BigDecimal intToBigDecimal;
  private Calendar date; // test Calender <-> Date map
  private GregorianCalendar calendar; // Test Calendar <-> Calendar map
  private NoCustomMappingsObjectPrime noMappingsObj;
  private List hintList;
  private Date timeStamp;
  private Date dateFromStr;
  private Double doubleObject;
  private Long theLongValue;
  private long[] primitiveArray;
  private Apple van;
  private String excludeMe = "excludeMe";
  private MetalThingyIF vanMetalThingy;
  private Date blankDate;
  private Long blankStringToLong;
  private String createdByFactoryName;
  private NoExtendBaseObject copyByReferencePrime;
  private NoExtendBaseObjectGlobalCopyByReference globalCopyByReferencePrime;
  private Apple[] arrayToSet;
  private Object[] objectArrayToSet;
  private Apple[] setToArrayWithValues;
  private List listToSet;
	private List listToCollection;
  private List setToListWithValues;
  public static String fieldAccessible;
  public int fieldAccessiblePrimInt;
  public InsideTestObjectPrime fieldAccessibleComplexType;
  private List stringArrayWithNullValue;
  public List fieldAccessibleArrayToList;
  private Date overloadSetField;
  private InsideTestObjectPrime createMethodType;
  private String excludeMeOneWay = "excludeMeOneWay";
  private String throwAllowedExceptionOnMapPrime;
  private String throwNonAllowedExceptionOnMapPrime;

  public long[] getPrimitiveArray() {
    return primitiveArray;
  }

  public void setPrimitiveArray(long[] primitiveArray) {
    this.primitiveArray = primitiveArray;
  }

  public Long getTheLongValue() {
    return theLongValue;
  }

  public void setTheLongValue(Long theLongValue) {
    this.theLongValue = theLongValue;
  }

  public GregorianCalendar getCalendar() {
    return calendar;
  }

  public void setCalendar(GregorianCalendar calendar) {
    this.calendar = calendar;
  }

  public void setAnotherPrimitive(int anotherPrimitive) {
    this.anotherPrimitive = anotherPrimitive;
  }

  public int getAnotherPrimitive() {
    return anotherPrimitive;
  }

  public void setEqualNamedList(java.util.List equalNamedList) {
    this.equalNamedList = equalNamedList;
  }

  public java.util.List getEqualNamedList() {
    return equalNamedList;
  }

  public void setOnePrime(String onePrime) {
    this.onePrime = onePrime;
  }

  public String getOnePrime() {
    return onePrime;
  }

  public void setTheMappedArray(int[] mappedArray) {
    theMappedArray = mappedArray;
  }

  public int[] getTheMappedArray() {
    return theMappedArray;
  }

  public void setTheMappedUnequallyNamedList(java.util.List theMappedUnequallyNamedList) {
    this.theMappedUnequallyNamedList = theMappedUnequallyNamedList;
  }

  public java.util.List getTheMappedUnequallyNamedList() {
    return theMappedUnequallyNamedList;
  }

  public void setThePrimitive(double primitive) {
    thePrimitive = primitive;
  }

  public double getThePrimitive() {
    return thePrimitive;
  }

  public void setThreePrime(InsideTestObjectPrime threePrime) {
    this.threePrime = threePrime;
  }

  public InsideTestObjectPrime getThreePrime() {
    return threePrime;
  }

  public void setTwoPrime(Integer twoPrime) {
    this.twoPrime = twoPrime;
  }

  public Integer getTwoPrime() {
    return twoPrime;
  }

  public void setListForArray(java.util.List listForArray) {
    this.listForArray = listForArray;
  }

  public java.util.List getListForArray() {
    return listForArray;
  }

  public InsideTestObjectPrime getInsideTestObject() {
    return insideTestObject;
  }

  public void setInsideTestObject(InsideTestObjectPrime insideTestObject) {
    this.insideTestObject = insideTestObject;
  }

  public int getBigDecimalToInt() {
    return bigDecimalToInt;
  }

  public void setBigDecimalToInt(int bigDecimalToInt) {
    this.bigDecimalToInt = bigDecimalToInt;
  }

  public BigDecimal getIntToBigDecimal() {
    return intToBigDecimal;
  }

  public void setIntToBigDecimal(BigDecimal intToBigDecimal) {
    this.intToBigDecimal = intToBigDecimal;
  }

  public Calendar getDate() {
    return date;
  }

  public void setDate(Calendar calendar) {
    this.date = calendar;
  }

  public NoCustomMappingsObjectPrime getNoMappingsObj() {
    return noMappingsObj;
  }

  public void setNoMappingsObj(NoCustomMappingsObjectPrime noMappingsObj) {
    this.noMappingsObj = noMappingsObj;
  }

  public List getArrayForLists() {
    return arrayForLists;
  }

  public void setArrayForLists(List arrayForLists) {
    this.arrayForLists = arrayForLists;
  }

  public List getHintList() {
    return hintList;
  }

  public void setHintList(List hintList) {
    this.hintList = hintList;
  }

  public Date getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(Date timeStamp) {
    this.timeStamp = timeStamp;
  }

  public Date getDateFromStr() {
    return dateFromStr;
  }

  public void setDateFromStr(Date dateFromStr) {
    this.dateFromStr = dateFromStr;
  }

  public Double getDoubleObject() {
    return doubleObject;
  }
  public void setDoubleObject(Double doubleObject) {
    this.doubleObject = doubleObject;
  }

  public Apple getVan() {
    return van;
  }

  public void setVan(Apple van) {
    this.van = van;
  }

  public String getExcludeMe() {
    return excludeMe;
  }

  public void setExcludeMe(String excludeMe) {
    this.excludeMe = excludeMe;
  }

  public MetalThingyIF getVanMetalThingy() {
    return vanMetalThingy;
  }

  public void setVanMetalThingy(MetalThingyIF vanMetalThingy) {
    this.vanMetalThingy = vanMetalThingy;
  }

  public Date getBlankDate() {
    return blankDate;
  }

  public void setBlankDate(Date blankDate) {
    this.blankDate = blankDate;
  }

  public Long getBlankStringToLong() {
    return blankStringToLong;
  }

  public void setBlankStringToLong(Long blankStringToLong) {
    this.blankStringToLong = blankStringToLong;
  }

  public String getCreatedByFactoryName() {
    return createdByFactoryName;
  }

  public void setCreatedByFactoryName(String createdByFactoryName) {
    this.createdByFactoryName = createdByFactoryName;
  }

  public NoExtendBaseObject getCopyByReferencePrime() {
    return copyByReferencePrime;
  }

  public void setCopyByReferencePrime(NoExtendBaseObject copyByReferencePrime) {
    this.copyByReferencePrime = copyByReferencePrime;
  }

  public NoExtendBaseObjectGlobalCopyByReference getGlobalCopyByReferencePrime() {
    return globalCopyByReferencePrime;
  }

  public void setGlobalCopyByReferencePrime(NoExtendBaseObjectGlobalCopyByReference globalCopyByReferencePrime) {
    this.globalCopyByReferencePrime = globalCopyByReferencePrime;
  }

  public Apple[] getArrayToSet() {
    return arrayToSet;
  }

  public void setArrayToSet(Apple[] arrayToSet) {
    this.arrayToSet = arrayToSet;
  }

  public Object[] getObjectArrayToSet() {
    return objectArrayToSet;
  }

  public void setObjectArrayToSet(Object[] objectArrayToSet) {
    this.objectArrayToSet = objectArrayToSet;
  }

  public Apple[] getSetToArrayWithValues() {
    return setToArrayWithValues;
  }

  public void setSetToArrayWithValues(Apple[] setToArrayWithValues) {
    this.setToArrayWithValues = setToArrayWithValues;
  }

  public List getListToSet() {
    return listToSet;
  }

  public void setListToSet(List listToSet) {
    this.listToSet = listToSet;
  }

  public List getSetToListWithValues() {
    return setToListWithValues;
  }

  public void setSetToListWithValues(List setToListWithValues) {
    this.setToListWithValues = setToListWithValues;
  }

  public List getStringArrayWithNullValue() {
    return stringArrayWithNullValue;
  }

  public void setStringArrayWithNullValue(List stringArrayWithNullValue) {
    this.stringArrayWithNullValue = stringArrayWithNullValue;
  }

  public Date getOverloadSetField() {
    return overloadSetField;
  }

  public void setOverloadSetField(Date date) {
    this.overloadSetField = date;
  }

  public void setOverloadSetField(InsideTestObject ito) {
  }

  public InsideTestObjectPrime getCreateMethodType() {
    return createMethodType;
  }

  public void setCreateMethodType(InsideTestObjectPrime createMethodType) {
    this.createMethodType = createMethodType;
  }

  public String getExcludeMeOneWay() {
    return excludeMeOneWay;
  }

  public void setExcludeMeOneWay(String excludeMeOneWay) {
    this.excludeMeOneWay = excludeMeOneWay;
  }

  public String getThrowAllowedExceptionOnMapPrime() {
    return throwAllowedExceptionOnMapPrime;
  }

  public void setThrowAllowedExceptionOnMapPrime(String throwAllowedExceptionOnMapPrime) {
    throw new TestException("Checking Allowed Exceptions");
  }

  public String getThrowNonAllowedExceptionOnMapPrime() {
    return throwNonAllowedExceptionOnMapPrime;
  }

  public void setThrowNonAllowedExceptionOnMapPrime(String throwNonAllowedExceptionOnMapPrime) {
    throw new RuntimeException("Checking Allowed Exceptions");
  }

	public List getListToCollection() {
		return listToCollection;
	}

	public void setListToCollection(List listToCollection) {
		this.listToCollection = listToCollection;
	}

}
