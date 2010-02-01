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
