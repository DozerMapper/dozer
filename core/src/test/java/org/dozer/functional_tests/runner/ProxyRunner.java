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
package org.dozer.functional_tests.runner;

import org.dozer.functional_tests.DataObjectInstantiator;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * @author Dmitry Buzdin
 */
public class ProxyRunner extends BlockJUnit4ClassRunner {

  private final DataObjectInstantiator instantiator;

  public ProxyRunner(Class<?> klass, DataObjectInstantiator instantiator) throws InitializationError {
    super(klass);
    this.instantiator = instantiator;
  }

  @Override
  protected Statement methodBlock(final FrameworkMethod method) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        DataObjectInstantiator oldValue = InstantiatorHolder.get();
        InstantiatorHolder.set(instantiator);
        try {
          ProxyRunner.super.methodBlock(method).evaluate();
        } finally {
          InstantiatorHolder.set(oldValue);
        }
      }
    };
  }

  @Override
  protected String getName() {
    return String.format("%s [%s]", super.getName(), instantiator.getName());
  }

  @Override
  protected String testName(FrameworkMethod method) {
    return String.format("%s [%s]", super.testName(method), instantiator.getName());
  }
}
