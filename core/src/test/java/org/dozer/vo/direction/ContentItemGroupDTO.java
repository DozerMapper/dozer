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

import java.util.Set;

/**
 * @author dmitry.buzdin
 */
public class ContentItemGroupDTO extends EntityDTO {

  private ContentItemGroupDTO parentGroup;
  private Set childGroups;

  public ContentItemGroupDTO getParentGroup() {
    return parentGroup;
  }

  public void setParentGroup(ContentItemGroupDTO parentGroup) {
    checkId();
    this.parentGroup = parentGroup;
  }

  public Set getChildGroups() {
    return childGroups;
  }

  public void setChildGroups(Set childGroups) {
    checkId();
    this.childGroups = childGroups;
  }

  private void checkId() {
    String id = getId();
    if (id == null) {
      throw new IllegalStateException("No id!");
    }
  }

}
