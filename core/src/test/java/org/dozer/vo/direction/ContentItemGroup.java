package org.dozer.vo.direction;

import java.util.Set;

/**
 * @author dmitry.buzdin
 */
public interface ContentItemGroup extends Entity {

  void setParentGroup(ContentItemGroup parent);

  ContentItemGroup getParentGroup();

  Set getChildGroups();

  void setChildGroups(Set childs);

  void addChildGroup(ContentItemGroup child);

}