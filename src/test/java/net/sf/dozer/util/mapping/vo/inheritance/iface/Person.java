package net.sf.dozer.util.mapping.vo.inheritance.iface;

public interface Person extends PersonProfile {
  Long getId();

  void setName(String name);
  
  void setAddress(String address);
}
