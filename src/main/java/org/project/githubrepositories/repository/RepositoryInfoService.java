package org.project.githubrepositories.repository;

import lombok.AllArgsConstructor;
import org.project.githubrepositories.http.GitHubHttpClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RepositoryInfoService {
    private final GitHubHttpClient gitHubHttpClient;

    public List<RepositoryInfo> getRepositoriesInfoForUser(String username) {
        return gitHubHttpClient.getRepositoriesInformationForUser(username);
    }
}
