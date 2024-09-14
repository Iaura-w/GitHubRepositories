package org.project.githubrepositories.http;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.project.githubrepositories.http.dto.BranchResponse;
import org.project.githubrepositories.http.dto.GitHubResponse;
import org.project.githubrepositories.http.error.ApiRateLimitException;
import org.project.githubrepositories.http.error.GitHubUserNotFoundException;
import org.project.githubrepositories.repository.BranchInfo;
import org.project.githubrepositories.repository.dto.RepositoryInfoDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Log4j2
public class GitHubHttpClient implements RemoteHttpClient {
    private final WebClient webClient;

    public List<RepositoryInfoDto> getRepositoriesInformationForUser(String username) {
        List<GitHubResponse> gitHubResponseList = fetchRepositories(username);
        List<GitHubResponse> repositoriesNotFork = gitHubResponseList.stream()
                .filter(gitHubResponse -> !gitHubResponse.fork())
                .toList();

        return repositoriesNotFork.stream()
                .map(repository -> {
                    String repositoryName = repository.name();
                    List<BranchResponse> branchResponseList = fetchBranches(username, repositoryName);
                    List<BranchInfo> branchInfoList = branchResponseList.stream()
                            .map(Mapper::branchResponseToBranchInfoMapper)
                            .toList();
                    return new RepositoryInfoDto(repository.name(), repository.owner().login(), branchInfoList, LocalDateTime.now());
                })
                .collect(Collectors.toList());
    }

    private List<GitHubResponse> fetchRepositories(String username) {
        String pathForUserRepository = String.format("/users/%s/repos", username);
        Mono<List<GitHubResponse>> responseMono = webClient.get()
                .uri(pathForUserRepository)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        Mono<List<GitHubResponse>> responseBody = response.bodyToMono(new ParameterizedTypeReference<>() {
                        });
                        log.info("Success, response body returned [{}]", responseBody);
                        return responseBody;
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


    private List<BranchResponse> fetchBranches(String username, String repositoryName) {
        String pathForRepositoryBranches = String.format("/repos/%s/%s/branches", username, repositoryName);
        Mono<List<BranchResponse>> listMono = webClient.get()
                .uri(pathForRepositoryBranches)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        log.info("Success, response body returned (branches)");
                        return response.bodyToMono(new ParameterizedTypeReference<>() {
                        });
                    } else {
                        log.error(String.format("Error while fetching branches for user %s repository %s ", username, repositoryName));
                        return response.createException()
                                .flatMap(Mono::error);
                    }
                });
        return listMono.block();
    }
}
