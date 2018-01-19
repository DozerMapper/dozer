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

import java.util.HashMap;
import java.util.Map;

import org.dozer.vo.BaseTestObject;

/**
 * @author garsombke.franz
 * @author sullins.ben
 * @author tierney.matt
 */
public class MapTestObjectPrime extends BaseTestObject {

    private PropertyToMap propertyToMapReverse;
    private Map propertyToMapMap = new HashMap();
    private Map nullPropertyToMapMap;
    private CustomMapIF propertyToCustomMapMap = new CustomMap();
    private PropertyToMap propertyToCustomMapWithInterface;

    public Map getPropertyToMapMap() {
        return propertyToMapMap;
    }

    public void setPropertyToMapMap(Map propertyToMapMap) {
        this.propertyToMapMap = propertyToMapMap;
    }

    public PropertyToMap getPropertyToMapReverse() {
        return propertyToMapReverse;
    }

    public void setPropertyToMapReverse(PropertyToMap propertyToMap) {
        this.propertyToMapReverse = propertyToMap;
    }

    public Map getNullPropertyToMapMap() {
        return nullPropertyToMapMap;
    }

    public void setNullPropertyToMapMap(Map nullPropertyToMapMap) {
        this.nullPropertyToMapMap = nullPropertyToMapMap;
    }

    public CustomMapIF getPropertyToCustomMapMap() {
        return propertyToCustomMapMap;
    }

    public void setPropertyToCustomMapMap(CustomMapIF propertyToCustomMapMap) {
        this.propertyToCustomMapMap = propertyToCustomMapMap;
    }

    public PropertyToMap getPropertyToCustomMapWithInterface() {
        return propertyToCustomMapWithInterface;
    }

    public void setPropertyToCustomMapWithInterface(PropertyToMap propertyToCustomMapWithInterface) {
        this.propertyToCustomMapWithInterface = propertyToCustomMapWithInterface;
    }
}
