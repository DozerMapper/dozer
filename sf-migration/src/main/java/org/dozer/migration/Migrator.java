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
