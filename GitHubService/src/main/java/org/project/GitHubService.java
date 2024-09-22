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
public class GitHubService implements IGitHubService {

    private final WebClient webClient;

    @Override
    public List<GitHubResponse> getRepositoriesForUser(String username) {
        log.info("Fetching repositories for user: {}", username);

        String path = String.format("/users/%s/repos", username);

        return webClient.get()
                .uri(path)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        log.info("Repositories successfully fetched for user: {}", username);
                        return response.bodyToMono(new ParameterizedTypeReference<List<GitHubResponse>>() {
                        });
                    } else if (response.statusCode().isSameCodeAs(HttpStatus.NOT_FOUND)) {
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
    }

    @Override
    public List<BranchResponse> getBranchesForRepository(String username, String repositoryName) {
        log.info("Fetching branches for repository: {}/{}", username, repositoryName);

        String path = String.format("/repos/%s/%s/branches", username, repositoryName);

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

    }
}
