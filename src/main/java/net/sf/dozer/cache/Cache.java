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
package net.sf.dozer.cache;

/**
 * Internal interface to a single cache. Holds all of the cache entries for the cache. Only
 * intended for internal use.
 *
 * @author tierney.matt
 */
public interface Cache {

  public void clear();

  public void put(Object key, Object value);

  public Object get(Object key);

  public String getName();

  public long getSize();

  public long getMaxSize();

  public long getHitCount();

  public long getMissCount();

}
