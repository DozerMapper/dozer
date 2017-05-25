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
package org.dozer.vo.map;

import java.util.Map;

import org.dozer.vo.BaseTestObject;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 *
 */
public class MapTestObject extends BaseTestObject {

    private PropertyToMap propertyToMap;
    private Map propertyToMapMapReverse;
    private PropertyToMap propertyToMapToNullMap;
    private PropertyToMap propertyToCustomMap;
    private CustomMapIF propertyToCustomMapMapWithInterface;

    public PropertyToMap getPropertyToMap() {
        return propertyToMap;
    }

    public void setPropertyToMap(PropertyToMap propertyToMap) {
        this.propertyToMap = propertyToMap;
    }

    public Map getPropertyToMapMapReverse() {
        return propertyToMapMapReverse;
    }

    public void setPropertyToMapMapReverse(Map propertyToMapMapReverse) {
        this.propertyToMapMapReverse = propertyToMapMapReverse;
    }

    public PropertyToMap getPropertyToMapToNullMap() {
        return propertyToMapToNullMap;
    }

    public void setPropertyToMapToNullMap(PropertyToMap propertyToMapToNullMap) {
        this.propertyToMapToNullMap = propertyToMapToNullMap;
    }

    public PropertyToMap getPropertyToCustomMap() {
        return propertyToCustomMap;
    }

    public void setPropertyToCustomMap(PropertyToMap propertyToCustomMap) {
        this.propertyToCustomMap = propertyToCustomMap;
    }

    public CustomMapIF getPropertyToCustomMapMapWithInterface() {
        return propertyToCustomMapMapWithInterface;
    }

    public void setPropertyToCustomMapMapWithInterface(CustomMapIF propertyToCustomMapMapWithInterface) {
        this.propertyToCustomMapMapWithInterface = propertyToCustomMapMapWithInterface;
    }
}
