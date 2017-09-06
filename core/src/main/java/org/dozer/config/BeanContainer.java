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
package org.dozer.config;

import org.dozer.el.ELEngine;
import org.dozer.el.NoopELEngine;
import org.dozer.loader.xml.ElementReader;
import org.dozer.loader.xml.ExpressionElementReader;
import org.dozer.util.DefaultClassLoader;
import org.dozer.util.DefaultProxyResolver;
import org.dozer.util.DozerClassLoader;
import org.dozer.util.DozerProxyResolver;

/**
 * @author dmitry.buzdin
 */
public class BeanContainer {

  DozerClassLoader classLoader = new DefaultClassLoader(getClass().getClassLoader());
  DozerClassLoader tccl = new DefaultClassLoader(Thread.currentThread().getContextClassLoader());
  DozerProxyResolver proxyResolver = new DefaultProxyResolver();
  ElementReader elementReader = new ExpressionElementReader(new NoopELEngine());
  ELEngine elEngine;

  public DozerClassLoader getClassLoader() {
    return classLoader;
  }

  public DozerClassLoader getTCCL() {
    return tccl;
  }

  public void setClassLoader(DozerClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  public DozerProxyResolver getProxyResolver() {
    return proxyResolver;
  }

  public void setProxyResolver(DozerProxyResolver proxyResolver) {
    this.proxyResolver = proxyResolver;
  }

  public ElementReader getElementReader() {
    return elementReader;
  }

  public void setElementReader(ElementReader elementReader) {
    this.elementReader = elementReader;
  }

  public ELEngine getElEngine() {
    return elEngine;
  }

  public void setElEngine(ELEngine elEngine) {
    this.elEngine = elEngine;
  }
}
