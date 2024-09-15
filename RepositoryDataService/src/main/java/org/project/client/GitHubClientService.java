package org.project.client;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.project.dto.BranchResponse;
import org.project.dto.GitHubResponse;
import org.project.error.GitHubServerException;
import org.project.error.GitHubUserNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class GitHubClientService {
    private final WebClient webClient;

    public List<GitHubResponse> fetchRepositoriesForUser(String username) {
        log.info("Fetching repositories from GitHubService for user {}", username);

        return webClient.get()
                .uri("/repositories/{username}", username)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        log.warn("GitHub user {} not found", username);
                        return Mono.error(new GitHubUserNotFoundException("GitHub user not found: " + username));
                    }
                    log.warn("Client error occurred for user {}: Status code {}", username, response.statusCode());
                    return Mono.error(new RuntimeException("Client error: " + response.statusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Server error occurred for user {}: Status code {}", username, response.statusCode());
                    return Mono.error(new GitHubServerException(username, response.statusCode()));
                })
                .bodyToMono(new ParameterizedTypeReference<List<GitHubResponse>>() {
                })
                .block();
    }

    public List<BranchResponse> fetchBranchesForUserAndRepository(String username, String repositoryName) {
        log.info("Fetching branches from GitHubService for user {} and repository {}", username, repositoryName);

        return webClient.get()
                .uri("/repositories/{username}/{repositoryName}/branches", username, repositoryName)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        log.warn("Repository {} not found for user {}", repositoryName, username);
                        return Mono.error(new GitHubUserNotFoundException("Repository not found: " + repositoryName));
                    }
                    log.warn("Client error occurred for user {} and repository {}: Status code {}", username, repositoryName, response.statusCode());
                    return Mono.error(new RuntimeException("Client error: " + response.statusCode()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Server error occurred for user {} and repository {}: Status code {}", username, repositoryName, response.statusCode());
                    return Mono.error(new RuntimeException("Server error: " + response.statusCode()));
                })
                .bodyToMono(new ParameterizedTypeReference<List<BranchResponse>>() {
                })
                .block();
    }
}
