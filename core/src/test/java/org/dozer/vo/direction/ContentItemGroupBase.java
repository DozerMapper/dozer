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
package org.dozer.vo.direction;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmitry Buzdin
 */
public class ContentItemGroupBase extends EntityBase {

  private ContentItemGroup parentGroup;
  private Set childGroups;

  public ContentItemGroup getParentGroup() {
    return this.parentGroup;
  }

  public void setParentGroup(final ContentItemGroup parent) {
    checkId();
    this.parentGroup = parent;
  }

  public Set getChildGroups() {
    return this.childGroups;
  }

  public void setChildGroups(final Set childs) {
    checkId();
    this.childGroups = childs;
  }

  public void addChildGroup(final ContentItemGroup child) {
    if (this.childGroups == null) {
      this.childGroups = new HashSet();
    }
    this.childGroups.add(child);
  }

  private void checkId() {
    String id = getId();
    if (id == null) {
      throw new IllegalStateException("No id!");
    }
  }

}
