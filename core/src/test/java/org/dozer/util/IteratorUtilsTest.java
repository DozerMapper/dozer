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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.dozer.AbstractDozerTest;
import org.junit.Test;

/**
 * @author Dmitry Buzdin
 */
public class IteratorUtilsTest extends AbstractDozerTest {

  @Test
  public void testToList() {
    Set<String> set = new LinkedHashSet<String>();
    set.add("A");
    set.add("B");

    List<String> result = IteratorUtils.toList(set.iterator());
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("A", result.get(0));
  }
}
