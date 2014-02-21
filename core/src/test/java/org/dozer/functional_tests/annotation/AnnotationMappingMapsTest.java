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
package org.dozer.functional_tests.annotation;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.dozer.Mapping;
import org.dozer.functional_tests.AbstractFunctionalTest;
import org.junit.Test;

/**
 * @author mwyraz
 */
public class AnnotationMappingMapsTest extends AbstractFunctionalTest
{
    public static class AnnotatedObject
    {
        @Mapping(key="prop1")
        public String prop1;
        
        @Mapping(key="otherNameForProp2")
        public String prop2;
    }
    
    @Test
    public void testMapForwardMapping()
    {
        AnnotatedObject source=new AnnotatedObject();
        source.prop1="value1";
        source.prop2="value2";
        
        @SuppressWarnings("unchecked")
        Map<String,String> result=mapper.map(source, HashMap.class);
        
        assertThat(result.get("prop1"), equalTo("value1"));
        assertThat(result.get("otherNameForProp2"), equalTo("value2"));
        assertThat(result.size(), equalTo(2));
    }

    @Test
    public void testMapReverseMapping()
    {
        Map<String,String> source=new HashMap<String, String>();
        source.put("prop1", "value1");
        source.put("otherNameForProp2", "value2");
        source.put("prop2", "value3");
        
        AnnotatedObject result=mapper.map(source, AnnotatedObject.class);
        assertThat(result.prop1, equalTo("value1"));
        assertThat(result.prop2, equalTo("value2"));
    }
    
}
