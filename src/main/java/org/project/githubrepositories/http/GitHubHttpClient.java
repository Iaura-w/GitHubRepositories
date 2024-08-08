package org.project.githubrepositories.http;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.project.githubrepositories.http.dto.BranchResponse;
import org.project.githubrepositories.http.dto.GitHubResponse;
import org.project.githubrepositories.http.error.ApiRateLimitException;
import org.project.githubrepositories.http.error.GitHubUserNotFoundException;
import org.project.githubrepositories.repository.BranchInfo;
import org.project.githubrepositories.repository.RepositoryInfo;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Log4j2
public class GitHubHttpClient {
    private final WebClient webClient;

    public List<RepositoryInfo> getRepositoriesInformationForUser(String username) {
        List<GitHubResponse> gitHubResponseList = fetchRepositories(username);
        List<GitHubResponse> repositoriesNotFork = gitHubResponseList.stream()
                .filter(gitHubResponse -> !gitHubResponse.fork())
                .toList();

        return repositoriesNotFork.stream()
                .map(repository -> {
                    String branchesUrl = repository.branchesUrl().substring(0, repository.branchesUrl().indexOf("{"));
                    List<BranchResponse> branchResponseList = fetchBranches(branchesUrl);
                    List<BranchInfo> branchInfoList = branchResponseList.stream()
                            .map(Mapper::branchResponseToBranchInfoMapper)
                            .toList();
                    return new RepositoryInfo(repository.name(), repository.owner().login(), branchInfoList);
                })
                .collect(Collectors.toList());
    }

    private List<GitHubResponse> fetchRepositories(String username) {
        String pathForUserRepository = String.format("/users/%s/repos", username);
        Mono<List<GitHubResponse>> responseMono = webClient.get()
                .uri(pathForUserRepository)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        log.info("Success, response body returned");
                        return response.bodyToMono(new ParameterizedTypeReference<List<GitHubResponse>>() {
                        });
                    } else if (response.statusCode().isSameCodeAs(HttpStatusCode.valueOf(403))) {
                        log.error("Error, API rate limit exceeded");
                        return Mono.error(new ApiRateLimitException());
                    } else if (response.statusCode().is4xxClientError()) {
                        log.error(String.format("Error while fetching repositories for user %s using http client ", username));
                        return Mono.error(new GitHubUserNotFoundException(username));
                    } else {
                        log.error("Error");
                        return response.createException()
                                .flatMap(Mono::error);
                    }
                });
        return responseMono.block();
    }


    private List<BranchResponse> fetchBranches(String urlForService) {
        Mono<List<BranchResponse>> listMono = webClient.get()
                .uri(URI.create(urlForService))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        log.info("Success, response body returned");
                        return response.bodyToMono(new ParameterizedTypeReference<List<BranchResponse>>() {
                        });
                    } else {
                        log.error(String.format("Error while fetching branches for url %s using http client", urlForService));
                        return response.createException()
                                .flatMap(Mono::error);
                    }
                });
        return listMono.block();
    }
}
