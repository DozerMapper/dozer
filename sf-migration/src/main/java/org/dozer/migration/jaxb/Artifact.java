package org.dozer.migration.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Buzdin
 */
@XmlRootElement(name = "artifact")
public class Artifact {

  List<Field> fields = new ArrayList<Field>();

  public List<Field> getFields() {
    return fields;
  }

  public void setFields(List<Field> fields) {
    this.fields = fields;
  }

}
