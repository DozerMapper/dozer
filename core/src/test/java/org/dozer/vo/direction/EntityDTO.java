package org.dozer.vo.direction;

/**
 * @author dmitry.buzdin
 */
public class EntityDTO {

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public int hashCode() {
    if (this.id == null) {
      throw new IllegalStateException("Id not mapped yet: BOEM.");
    }
    return this.id.hashCode();
  }

}
