package org.project.client;

import org.project.dto.BranchResponse;
import org.project.dto.GitHubResponse;

import java.util.List;

public interface RemoteHttpClient {

    List<GitHubResponse> fetchRepositoriesForUser(String username);

    List<BranchResponse> fetchBranchesForUserAndRepository(String username, String repositoryName);
}
