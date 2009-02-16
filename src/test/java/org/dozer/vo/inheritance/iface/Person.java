package org.dozer.vo.inheritance.iface;

public interface Person extends PersonProfile {
  Long getId();

  void setName(String name);

  void setAddress(String address);
}
