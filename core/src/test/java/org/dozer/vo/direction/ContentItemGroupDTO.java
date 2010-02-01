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
