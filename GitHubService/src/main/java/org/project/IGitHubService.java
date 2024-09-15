package org.project;

import org.project.dto.BranchResponse;
import org.project.dto.GitHubResponse;

import java.util.List;

public interface IGitHubService {
    List<GitHubResponse> getRepositoriesForUser(String username);

    List<BranchResponse> getBranchesForRepository(String username, String repositoryName);
}
