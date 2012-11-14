package org.dozer.migration.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Buzdin
 */
@XmlRootElement(name = "project_export")
public class Project {

  List<Artifact> artifacts = new ArrayList<Artifact>();

  public List<Artifact> getArtifacts() {
    return artifacts;
  }

  public void setArtifacts(List<Artifact> artifacts) {
    this.artifacts = artifacts;
  }

}
