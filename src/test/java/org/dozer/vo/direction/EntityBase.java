package org.dozer.vo.direction;

/**
 * @author dmitry.buzdin
 */
public abstract class EntityBase implements Entity {

  private String id;

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }

  @Override
  public int hashCode() {
    if (this.id == null) {
      throw new IllegalStateException("Id not mapped yet: BOEM.");
    }
    return this.id.hashCode();
  }

}
