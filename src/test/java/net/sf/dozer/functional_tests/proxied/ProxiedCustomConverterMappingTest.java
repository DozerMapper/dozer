/*
 * Copyright 2005-2007 the original author or authors.
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
package net.sf.dozer.functional_tests.proxied;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.dozer.functional_tests.AbstractCustomConverterMappingTest;
import net.sf.dozer.functional_tests.ObjectInstantiator;
import net.sf.dozer.util.mapping.AbstractDozerTest;
import net.sf.dozer.util.mapping.DozerBeanMapper;
import net.sf.dozer.util.mapping.converters.StringAppendCustomConverter;
import net.sf.dozer.util.mapping.vo.AnotherTestObject;
import net.sf.dozer.util.mapping.vo.AnotherTestObjectPrime;
import net.sf.dozer.util.mapping.vo.ArrayCustConverterObj;
import net.sf.dozer.util.mapping.vo.ArrayCustConverterObjPrime;
import net.sf.dozer.util.mapping.vo.Bus;
import net.sf.dozer.util.mapping.vo.Car;
import net.sf.dozer.util.mapping.vo.CustomDoubleObject;
import net.sf.dozer.util.mapping.vo.CustomDoubleObjectIF;
import net.sf.dozer.util.mapping.vo.Moped;
import net.sf.dozer.util.mapping.vo.SimpleObj;
import net.sf.dozer.util.mapping.vo.SimpleObjPrime2;
import net.sf.dozer.util.mapping.vo.TestCustomConverterHashMapObject;
import net.sf.dozer.util.mapping.vo.TestCustomConverterHashMapPrimeObject;
import net.sf.dozer.util.mapping.vo.TestCustomConverterObject;
import net.sf.dozer.util.mapping.vo.TestCustomConverterObjectPrime;
import net.sf.dozer.util.mapping.vo.TestObject;
import net.sf.dozer.util.mapping.vo.TestObjectPrime;
import net.sf.dozer.util.mapping.vo.Van;
import net.sf.dozer.util.mapping.vo.map.CustomMap;
import net.sf.dozer.util.mapping.vo.map.MapToProperty;

/**
 * @author tierney.matt
 * @author garsombke.franz
 */
public class ProxiedCustomConverterMappingTest extends AbstractCustomConverterMappingTest {

  protected ObjectInstantiator getDataObjectInstantiator() {
    return ObjectInstantiator.PROXY_INSTANTIATOR;
  }

}