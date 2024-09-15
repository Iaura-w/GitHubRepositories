package org.project;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.project.dto.BranchResponse;
import org.project.dto.GitHubResponse;
import org.project.error.ApiRateLimitException;
import org.project.error.GitHubServiceException;
import org.project.error.GitHubUserNotFoundException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class GitHubService {

    private final WebClient webClient;

    public List<GitHubResponse> getRepositoriesForUser(String username) {
        log.info("Fetching repositories for user: {}", username);

        String path = String.format("/users/%s/repos", username);

        try {
            return webClient.get()
                    .uri(path)
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            log.info("Repositories successfully fetched for user: {}", username);
                            return response.bodyToMono(new ParameterizedTypeReference<List<GitHubResponse>>() {
                            });
                        } else if (response.statusCode().is4xxClientError()) {
                            log.warn("User {} not found. Status code: {}", username, response.statusCode());
                            return Mono.error(new GitHubUserNotFoundException(username));
                        } else if (response.statusCode().isSameCodeAs(HttpStatus.FORBIDDEN)) {
                            log.error("API rate limit exceeded while fetching repositories for user: {}", username);
                            return Mono.error(new ApiRateLimitException());
                        } else {
                            log.error("Unexpected error occurred while fetching repositories for user: {}", username);
                            return Mono.error(new GitHubServiceException("Error fetching repositories for user: " + username));
                        }
                    })
                    .block();
        } catch (Exception e) {
            log.error("Error while fetching repositories for user: {}. Error: {}", username, e.getMessage());
            throw new GitHubServiceException("Failed to fetch repositories for user: " + username);
        }
    }

    public List<BranchResponse> getBranchesForRepository(String username, String repositoryName) {
        log.info("Fetching branches for repository: {}/{}", username, repositoryName);

        String path = String.format("/repos/%s/%s/branches", username, repositoryName);

        try {
            return webClient.get()
                    .uri(path)
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            log.info("Branches successfully fetched for repository: {}/{}", username, repositoryName);
                            return response.bodyToMono(new ParameterizedTypeReference<List<BranchResponse>>() {
                            });
                        } else {
                            log.error("Error while fetching branches for repository: {}/{}. Status code: {}", username, repositoryName, response.statusCode());
                            return Mono.error(new GitHubServiceException("Error fetching branches for repository: " + repositoryName));
                        }
                    })
                    .block();
        } catch (Exception e) {
            log.error("Error while fetching branches for repository: {}/{}. Error: {}", username, repositoryName, e.getMessage());
            throw new GitHubServiceException("Failed to fetch branches for repository: " + repositoryName);
        }
    }
}
