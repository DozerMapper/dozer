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
package net.sf.dozer.util.mapping.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.sf.dozer.util.mapping.AbstractDozerTest;

/**
 * @author tierney.matt
 */
public class CacheKeyFactoryTest extends AbstractDozerTest {

  public void testCreateKey() throws Exception {
    List args = new ArrayList();
    args.add(String.class);
    args.add(Long.class);
    args.add(new String("hello"));
    
    Object cacheKey = CacheKeyFactory.createKey(args.toArray());
    Object cacheKey2 = CacheKeyFactory.createKey(new ArrayList(args).toArray());    
     
    assertEquals("cache keys should have been equal", cacheKey, cacheKey2);
    assertEquals("cache key hash codes should have been equal", cacheKey.hashCode(), cacheKey2.hashCode());    
  }
  
  public void testCreateKey2() throws Exception {
    String arg1 = "test string";
    Long arg2 = new Long(55);
    List arg3 = new ArrayList();
    arg3.add("list entry");
    Class arg4 = Random.class;
    
    Object cacheKey = CacheKeyFactory.createKey(new Object[] {arg1, arg2, arg3, arg4});
    Object cacheKey2 = CacheKeyFactory.createKey(new Object[] {arg1, arg2, arg3, arg4});    
    
    assertEquals("cache keys should have been equal", cacheKey, cacheKey2);     
    assertEquals("cache key hash codes should have been equal", cacheKey.hashCode(), cacheKey2.hashCode());
  }

}
