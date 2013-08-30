/**
 * Copyright 2005-2013 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
