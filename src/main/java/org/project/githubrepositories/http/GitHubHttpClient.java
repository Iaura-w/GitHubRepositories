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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Log4j2
public class GitHubHttpClient {
    private final RestTemplate restTemplate;
    private static final String URL_GITHUB = "https://api.github.com/users/%s/repos";

    public List<RepositoryInfo> getRepositoriesInformationForUser(String username) {
        List<GitHubResponse> gitHubResponseList = fetchRepositories(username);
        List<GitHubResponse> repositoriesNotFork = gitHubResponseList.stream()
                .filter(gitHubResponse -> !gitHubResponse.fork())
                .toList();

        return repositoriesNotFork.stream()
                .map(r -> {
                    String branchesUrl = r.branchesUrl().substring(0, r.branchesUrl().indexOf("{"));
                    List<BranchResponse> branchResponseList = fetchBranches(branchesUrl);
                    List<BranchInfo> branchInfoList = branchResponseList.stream()
                            .map(Mapper::branchResponseToBranchInfoMapper)
                            .toList();
                    return new RepositoryInfo(r.name(), r.owner().login(), branchInfoList);
                })
                .collect(Collectors.toList());
    }

    private List<GitHubResponse> fetchRepositories(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github+json");

        final HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        try {
            String urlForService = String.format(URL_GITHUB, username);
            final String url = UriComponentsBuilder.fromHttpUrl(urlForService).toUriString();
            ResponseEntity<List<GitHubResponse>> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
            });
            final List<GitHubResponse> body = response.getBody();
            if (body == null) {
                log.error("Response body null");
                throw new ResponseStatusException(HttpStatus.NO_CONTENT);
            }
            log.info("Success, response body returned: " + body);
            return body;
        } catch (ResourceAccessException e) {
            log.error(String.format("Error while fetching repositories for user %s using http client: %s ", username, e.getMessage()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ResponseStatusException e) {
            log.error(String.format("Error, user %s not found: %s", username, e.getMessage()));
            throw new GitHubUserNotFoundException(username);
        } catch (RestClientException e) {
            log.error("Error, API rate limit exceeded");
            throw new ApiRateLimitException();
        }
    }

    private List<BranchResponse> fetchBranches(String urlForService) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github+json");

        final HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        try {
            final String url = UriComponentsBuilder.fromHttpUrl(urlForService).toUriString();
            ResponseEntity<List<BranchResponse>> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {
            });
            final List<BranchResponse> body = response.getBody();
            if (body == null) {
                log.error("Response body null");
                throw new ResponseStatusException(HttpStatus.NO_CONTENT);
            }
            log.info("Success, response body returned: " + body);
            return body;
        } catch (ResourceAccessException e) {
            log.error(String.format("Error while fetching branches for url %s using http client: %s ", urlForService, e.getMessage()));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
