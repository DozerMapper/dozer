package net.sf.dozer.vo.generics;

public abstract class Entity<I> {
  private I id;
  public I getId() {
    return id;
  }
  public void setId(final I id) {
    this.id = id;
  }
}