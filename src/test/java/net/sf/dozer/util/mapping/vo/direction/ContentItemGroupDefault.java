package net.sf.dozer.util.mapping.vo.direction;

import java.util.Set;
import java.util.HashSet;

/**
 * @author dmitry.buzdin
 */
public class ContentItemGroupDefault extends EntityBase implements ContentItemGroup {

	private ContentItemGroup parentGroup;
	private Set childGroups;

  public ContentItemGroup getParentGroup() {
		return this.parentGroup;
	}

	public void setParentGroup(final ContentItemGroup parent) {
		this.parentGroup = parent;
	}

	public Set getChildGroups() {
		return this.childGroups;
	}

  public void setChildGroups(final Set childs) {
		this.childGroups = childs;
	}

  public void addChildGroup(final ContentItemGroup child) {
		if (this.childGroups == null) {
			this.childGroups = new HashSet();
		}
		this.childGroups.add(child);
	}

}