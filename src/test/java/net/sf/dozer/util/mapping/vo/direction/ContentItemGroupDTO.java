package net.sf.dozer.util.mapping.vo.direction;

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
    this.parentGroup = parentGroup;
  }

  public Set getChildGroups() {
    return childGroups;
  }

  public void setChildGroups(Set childGroups) {
    this.childGroups = childGroups;
  }

}
