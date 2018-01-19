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
package org.dozer;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dmitry.buzdin
 */
public class MappedFieldsTrackerTest extends AbstractDozerTest{

  private MappedFieldsTracker tracker;

  @Before
  public void setUp() {
    tracker = new MappedFieldsTracker();
  }

  @Test
  public void testPut_OK() {
    tracker.put("", "1");
    assertEquals("1", tracker.getMappedValue("", String.class));
  }

  @Test
  public void testPut_Interface() {
    tracker.put("", "1");
    tracker.put("", new HashSet());
    assertEquals(new HashSet(), tracker.getMappedValue("", Set.class));
  }

  @Test
  public void testGetMappedValue() {
    assertNull(tracker.getMappedValue("", String.class));
  }

  @Test
  public void testGetMappedValue_NoSuchType() {
    tracker.put("", new HashSet());
    assertNull(tracker.getMappedValue("", String.class));
  }

  @Test
  public void doesNotCallEqualsOrHashCode() {
    Boom src = new Boom();
    Boom dest = new Boom();

    tracker.put(src, dest);

    Object result = tracker.getMappedValue(src, Boom.class);
    assertSame(dest, result);
  }

  @Test
  public void testGetMappedValue_honorsMapId() {
    tracker.put("", "42", "someId");
    assertNull(tracker.getMappedValue("", String.class));
    assertEquals("42", tracker.getMappedValue("", String.class, "someId"));
    assertNull(tracker.getMappedValue("", String.class, "brandNewMapId"));
  }

  public static class Boom {
    @Override
    public int hashCode() {
      throw new RuntimeException();
    }

    @Override
    public boolean equals(Object obj) {
      throw new RuntimeException();
    }
  }

}
