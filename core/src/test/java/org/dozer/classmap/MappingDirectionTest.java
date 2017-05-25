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
package org.dozer.classmap;

import org.dozer.AbstractDozerTest;
import org.junit.Test;

/**
 * @author dmitry.buzdin
 */
public class MappingDirectionTest extends AbstractDozerTest{

  @Test
  public void testValueOf() {
    assertNull(MappingDirection.valueOf(null));
    assertSame(MappingDirection.ONE_WAY, MappingDirection.valueOf("one-way"));
    assertSame(MappingDirection.BI_DIRECTIONAL, MappingDirection.valueOf("bi-directional"));
  }

}
