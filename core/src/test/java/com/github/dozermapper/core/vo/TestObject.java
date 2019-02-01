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
package com.github.dozermapper.core.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestObject extends BaseTestObject {

    private String one;
    private Integer two;
    private InsideTestObject three;
    private InsideTestObject insideTestObject;
    private java.util.List equalNamedList;
    private java.util.List unequalNamedList;
    private int thePrimitive;
    private int theMappedPrimitive;
    private int[] anArray;
    private Integer[] arrayForLists;
    private BigDecimal bigDecimalToInt;
    private int intToBigDecimal;
    private Date date;
    private GregorianCalendar calendar;
    private NoCustomMappingsObject noMappingsObj;
    private List hintList;
    private Timestamp timeStamp;
    private String dateStr;
    private DoubleObject doubleObject;
    private long anotherLongValue;
    private int[] primArray;
    private Van van;
    private String excludeMe;
    private MetalThingyIF carMetalThingy;
    private String blankDate;
    private String blankStringToLong;
    private String createdByFactoryName;
    private NoExtendBaseObject copyByReference;
    private NoExtendBaseObject copyByReferenceDeep;
    private NoExtendBaseObjectGlobalCopyByReference globalCopyByReference;
    private Set setToArray;
    private Set setToObjectArray;
    private Set setToArrayWithValues;
    private Set setToList;
    private Collection collectionToList;
    private Set setToListWithValues;
    private String fieldAccessible;
    private int fieldAccessiblePrimInt;
    private InsideTestObject fieldAccessibleComplexType;
    private String[] stringArrayWithNullValue;
    private String[] fieldAccessibleArrayToList;
    private Date overloadGetField;
    private InsideTestObject createMethodType;
    private String excludeMeOneWay;
    private String throwAllowedExceptionOnMap;
    private String throwNonAllowedExceptionOnMap;
    private Set setToArrayWithIterate = new HashSet();

    public int[] getPrimArray() {
        return primArray;
    }

    public void setPrimArray(int[] primArray) {
        this.primArray = primArray;
    }

    public long getAnotherLongValue() {
        return anotherLongValue;
    }

    public void setAnotherLongValue(long longValue) {
        anotherLongValue = longValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public InsideTestObject getInsideTestObject() {
        return insideTestObject;
    }

    public void setInsideTestObject(InsideTestObject insideTestObject) {
        this.insideTestObject = insideTestObject;
    }

    public int getThePrimitive() {
        return thePrimitive;
    }

    public void setThePrimitive(int primitive) {
        thePrimitive = primitive;
    }

    public java.util.List getEqualNamedList() {
        return equalNamedList;
    }

    public void setEqualNamedList(java.util.List equalNamedList) {
        this.equalNamedList = equalNamedList;
    }

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public InsideTestObject getThree() {
        return three;
    }

    public void setThree(InsideTestObject three) {
        this.three = three;
    }

    public Integer getTwo() {
        return two;
    }

    public void setTwo(Integer two) {
        this.two = two;
    }

    public java.util.List getUnequalNamedList() {
        return unequalNamedList;
    }

    public void setUnequalNamedList(java.util.List unequalNamedList) {
        this.unequalNamedList = unequalNamedList;
    }

    public int getTheMappedPrimitive() {
        return theMappedPrimitive;
    }

    public void setTheMappedPrimitive(int mappedPrimitive) {
        theMappedPrimitive = mappedPrimitive;
    }

    public int[] getAnArray() {
        return anArray;
    }

    public void setAnArray(int[] anArray) {
        this.anArray = anArray;
    }

    public Integer[] getArrayForLists() {
        return arrayForLists;
    }

    public void setArrayForLists(Integer[] arrayForLists) {
        this.arrayForLists = arrayForLists;
    }

    public BigDecimal getBigDecimalToInt() {
        return bigDecimalToInt;
    }

    public void setBigDecimalToInt(BigDecimal bigDecimalToInt) {
        this.bigDecimalToInt = bigDecimalToInt;
    }

    public int getIntToBigDecimal() {
        return intToBigDecimal;
    }

    public void setIntToBigDecimal(int intToBigDecimal) {
        this.intToBigDecimal = intToBigDecimal;
    }

    public GregorianCalendar getCalendar() {
        return calendar;
    }

    public void setCalendar(GregorianCalendar calendar) {
        this.calendar = calendar;
    }

    public NoCustomMappingsObject getNoMappingsObj() {
        return noMappingsObj;
    }

    public void setNoMappingsObj(NoCustomMappingsObject noMappingsObj) {
        this.noMappingsObj = noMappingsObj;
    }

    public List getHintList() {
        return hintList;
    }

    public void setHintList(List hintList) {
        this.hintList = hintList;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public DoubleObject getDoubleObject() {
        return doubleObject;
    }

    public void setDoubleObject(DoubleObject doubleObject) {
        this.doubleObject = doubleObject;
    }

    public Van getVan() {
        return van;
    }

    public void setVan(Van van) {
        this.van = van;
    }

    public String getExcludeMe() {
        return excludeMe;
    }

    public void setExcludeMe(String excludeMe) {
        this.excludeMe = excludeMe;
    }

    public MetalThingyIF getCarMetalThingy() {
        return carMetalThingy;
    }

    public void setCarMetalThingy(MetalThingyIF car) {
        this.carMetalThingy = car;
    }

    public String getBlankDate() {
        return blankDate;
    }

    public void setBlankDate(String blankDate) {
        this.blankDate = blankDate;
    }

    public String getBlankStringToLong() {
        return blankStringToLong;
    }

    public void setBlankStringToLong(String blankStringToLong) {
        this.blankStringToLong = blankStringToLong;
    }

    public String getCreatedByFactoryName() {
        return createdByFactoryName;
    }

    public void setCreatedByFactoryName(String createdByFactoryName) {
        this.createdByFactoryName = createdByFactoryName;
    }

    public NoExtendBaseObject getCopyByReference() {
        return copyByReference;
    }

    public void setCopyByReference(NoExtendBaseObject copyByReference) {
        this.copyByReference = copyByReference;
    }

    public NoExtendBaseObject getCopyByReferenceDeep() {
        return copyByReferenceDeep;
    }

    public void setCopyByReferenceDeep(NoExtendBaseObject copyByReferenceDeep) {
        this.copyByReferenceDeep = copyByReferenceDeep;
    }

    public NoExtendBaseObjectGlobalCopyByReference getGlobalCopyByReference() {
        return globalCopyByReference;
    }

    public void setGlobalCopyByReference(NoExtendBaseObjectGlobalCopyByReference globalCopyByReference) {
        this.globalCopyByReference = globalCopyByReference;
    }

    public Set getSetToArray() {
        return setToArray;
    }

    public void setSetToArray(Set setToArray) {
        this.setToArray = setToArray;
    }

    public Set getSetToObjectArray() {
        return setToObjectArray;
    }

    public void setSetToObjectArray(Set setToObjectArray) {
        this.setToObjectArray = setToObjectArray;
    }

    public Set getSetToArrayWithValues() {
        return setToArrayWithValues;
    }

    public void setSetToArrayWithValues(Set setToArrayWithValues) {
        this.setToArrayWithValues = setToArrayWithValues;
    }

    public Set getSetToList() {
        return setToList;
    }

    public void setSetToList(Set setToList) {
        this.setToList = setToList;
    }

    public Set getSetToListWithValues() {
        return setToListWithValues;
    }

    public void setSetToListWithValues(Set setToListWithValues) {
        this.setToListWithValues = setToListWithValues;
    }

    public String getFieldAccessible() {
        return fieldAccessible;
    }

    public void setFieldAccessible(String fieldAccessible) {
        this.fieldAccessible = fieldAccessible;
    }

    public int getFieldAccessiblePrimInt() {
        return fieldAccessiblePrimInt;
    }

    public void setFieldAccessiblePrimInt(int fieldAccessiblePrimInt) {
        this.fieldAccessiblePrimInt = fieldAccessiblePrimInt;
    }

    public InsideTestObject getFieldAccessibleComplexType() {
        return fieldAccessibleComplexType;
    }

    public void setFieldAccessibleComplexType(InsideTestObject fieldAccessibleComplexType) {
        this.fieldAccessibleComplexType = fieldAccessibleComplexType;
    }

    public String[] getStringArrayWithNullValue() {
        return stringArrayWithNullValue;
    }

    public void setStringArrayWithNullValue(String[] stringArrayWithNullValue) {
        this.stringArrayWithNullValue = stringArrayWithNullValue;
    }

    public String[] getFieldAccessibleArrayToList() {
        return fieldAccessibleArrayToList;
    }

    public void setFieldAccessibleArrayToList(String[] fieldAccessibleArrayToList) {
        this.fieldAccessibleArrayToList = fieldAccessibleArrayToList;
    }

    public Date getOverloadGetField() {
        return overloadGetField;
    }

    public void setOverloadGetField(Date overloadGetField) {
        this.overloadGetField = overloadGetField;
    }

    public InsideTestObject getCreateMethodType() {
        return createMethodType;
    }

    public void setCreateMethodType(InsideTestObject createMethodType) {
        this.createMethodType = createMethodType;
    }

    public String getExcludeMeOneWay() {
        return excludeMeOneWay;
    }

    public void setExcludeMeOneWay(String excludeMeOneWay) {
        this.excludeMeOneWay = excludeMeOneWay;
    }

    public String getThrowAllowedExceptionOnMap() {
        return throwAllowedExceptionOnMap;
    }

    public void setThrowAllowedExceptionOnMap(String throwAllowedExceptionOnMap) {
        this.throwAllowedExceptionOnMap = throwAllowedExceptionOnMap;
    }

    public String getThrowNonAllowedExceptionOnMap() {
        return throwNonAllowedExceptionOnMap;
    }

    public void setThrowNonAllowedExceptionOnMap(String throwNonAllowedExceptionOnMap) {
        this.throwNonAllowedExceptionOnMap = throwNonAllowedExceptionOnMap;
    }

    public Set getSetToArrayWithIterate() {
        return setToArrayWithIterate;
    }

    public void setSetToArrayWithIterate(Set setToArrayWithIterate) {
        this.setToArrayWithIterate = setToArrayWithIterate;
    }

    public void addAnotherTestObject(AnotherTestObject ato) {
        getSetToArrayWithIterate().add(ato);
    }

    public Collection getCollectionToList() {
        return collectionToList;
    }

    public void setCollectionToList(Collection collectionToList) {
        this.collectionToList = collectionToList;
    }
}
