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

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;

/**
 * @author Dmitry Buzdin
 */
public class Migrator {

  public static void main(String[] args) throws Exception{
    GitHubClient client = new GitHubClient();
    client.setCredentials("user", "passw0rd");

    RepositoryService repositoryService = new RepositoryService();
    Repository repository = repositoryService.getRepository("DozerMapper", "dozer");

    IssueService issueService = new IssueService();
    Issue issue = new Issue();
    issueService.createIssue("buzdin", "", issue);
  }

}
