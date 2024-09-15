package org.project.client;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.dto.BranchResponse;
import org.project.dto.GitHubResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class GitHubClientService {
    private WebClient webClient;

    public List<GitHubResponse> fetchRepositoriesForUser(String username) {
        log.info("Fetching repositories from GitHubService for user {}", username);
        return webClient.get()
                .uri("/repositories/{username}", username)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GitHubResponse>>() {
                })
                .block();
    }

    public List<BranchResponse> fetchBranchesForUserAndRepository(String username, String repositoryName) {
        log.info("Fetching branches from GitHubService for user {} and repository {}", username, repositoryName);
        return webClient.get()
                .uri("/repositories/{username}/{repositoryName}/branches", username, repositoryName)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<BranchResponse>>() {
                })
                .block();
    }
}
