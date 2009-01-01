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
 * Internal class that is responsible for producing cache keys. Only intended for internal use.
 * 
 * @author tierney.matt
 * @author dmitry.buzdin
 */
public final class CacheKeyFactory {

  private CacheKeyFactory() {
  }

  public static Object createKey(Class<?> destClass, Class<?> srcClass) {
    return new CacheKey(srcClass, destClass);
  }

  private static class CacheKey {

    private Class<?> srcClass;
    private Class<?> destClass;

    private CacheKey(Class<?> srcClass, Class<?> destClass) {
      this.srcClass = srcClass;
      this.destClass = destClass;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;

      CacheKey cacheKey = (CacheKey) o;

      if (destClass != null ? !destClass.equals(cacheKey.destClass) : cacheKey.destClass != null)
        return false;
      if (srcClass != null ? !srcClass.equals(cacheKey.srcClass) : cacheKey.srcClass != null)
        return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result;
      result = (srcClass != null ? srcClass.hashCode() : 0);
      result = 31 * result + (destClass != null ? destClass.hashCode() : 0);
      return result;
    }

  }

}
