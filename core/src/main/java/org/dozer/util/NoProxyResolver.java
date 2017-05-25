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
package org.dozer.util;

/**
 *
 * No Proxy behavior strategy. In cases when there are no proxied objects (XmlBeans for example) this mode
 * should be configured in dozer.properties for maximum performance. Configured per Dozer Mapper instance.
 *
 * @author Dmitry Buzdin
 */
public class NoProxyResolver implements DozerProxyResolver {

  @Override
  public boolean isProxy(Class<?> clazz) {
    return false;
  }

  @Override
  public <T> T unenhanceObject(T object) {
    return object;
  }

  @Override
  public Class<?> getRealClass(Class<?> clazz) {
    return clazz;
  }

}
