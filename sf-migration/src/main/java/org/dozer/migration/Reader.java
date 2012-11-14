package org.dozer.migration;

import org.dozer.migration.jaxb.Project;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.net.URL;

/**
 * @author Dmitry Buzdin
 */
public class Reader {

  public static void main(String[] args) throws Exception {
    URL resource = Reader.class.getClassLoader().getResource("sfexport.xml");
    File file = new File(resource.toURI() );
    JAXBContext jaxbContext = JAXBContext.newInstance(Project.class);

    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    Project project = (Project) jaxbUnmarshaller.unmarshal(file);

    System.out.println(project.getArtifacts().size());
  }

}
