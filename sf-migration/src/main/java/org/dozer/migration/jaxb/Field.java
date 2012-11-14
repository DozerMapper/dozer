package org.dozer.migration.jaxb;

import javax.xml.bind.annotation.*;

/**
 * @author Dmitry Buzdin
 */
@XmlRootElement(name = "field")
@XmlAccessorType(XmlAccessType.FIELD)
public class Field {

  @XmlAttribute(name = "name")
  String name;

//  @XmlValue
  String value;

}
