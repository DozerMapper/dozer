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
package com.github.dozermapper.core.cache;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Factory which produces cache keys.
 */
public final class CacheKeyFactory {

    private CacheKeyFactory() {
    }

    /**
     * Create a key based on destination and source class types
     * @param destClass destination class being mapped
     * @param srcClass source class being mapped
     * @return created key
     */
    public static Object createKey(Class<?> destClass, Class<?> srcClass) {
        return new CacheKey(srcClass, destClass);
    }

    /**
     * Create a key based on destination and source class types
     * @param destClass destination class being mapped
     * @param srcClass source class being mapped
     * @param mapId map id being used by mapping
     * @return created key
     */
    public static Object createKey(Class<?> destClass, Class<?> srcClass, String mapId) {
        return new CacheKey(srcClass, destClass, mapId);
    }

    private static final class CacheKey {

        private final Class<?> srcClass;
        private final Class<?> destClass;
        private String mapId;

        private CacheKey(Class<?> srcClass, Class<?> destClass, String mapId) {
            this.srcClass = srcClass;
            this.destClass = destClass;
            this.mapId = mapId;
        }

        private CacheKey(Class<?> srcClass, Class<?> destClass) {
            this.srcClass = srcClass;
            this.destClass = destClass;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final CacheKey cacheKey = (CacheKey)o;
            if (destClass != null ? !destClass.equals(cacheKey.destClass) : cacheKey.destClass != null) {
                return false;
            }

            if (srcClass != null ? !srcClass.equals(cacheKey.srcClass) : cacheKey.srcClass != null) {
                return false;
            }

            return mapId != null ? mapId.equals(cacheKey.mapId) : cacheKey.mapId == null;
        }

        @Override
        public int hashCode() {
            int result;
            result = srcClass != null ? srcClass.hashCode() : 0;
            result = 31 * result + (destClass != null ? destClass.hashCode() : 0);
            result = 31 * result + (mapId != null ? mapId.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
