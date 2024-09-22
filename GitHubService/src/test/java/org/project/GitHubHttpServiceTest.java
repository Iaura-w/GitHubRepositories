package org.project;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.project.dto.BranchResponse;
import org.project.dto.Commit;
import org.project.dto.GitHubResponse;
import org.project.dto.Owner;
import org.project.error.ApiRateLimitException;
import org.project.error.GitHubServiceException;
import org.project.error.GitHubUserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.project.GitHubHttpClientConfigTest.*;

class GitHubHttpServiceTest {
    public MockWebServer mockWebServer;
    public IGitHubService clientUnderTest;

    @BeforeEach
    void setUp() {
        mockWebServer = new MockWebServer();
        WebClient mockedWebClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(mockWebServer.url("/").toString())
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .build();
        clientUnderTest = new GitHubService(mockedWebClient);
    }

    @AfterEach
    void resetMockWebServer() throws IOException {
        mockWebServer.close();
    }

    @Test
    void should_return_correct_repository_response_when_existing_username_is_given() {
        // given
        String username = "some_user";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.OK.value())
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(mockedResponse200())
        );

        // when
        List<GitHubResponse> actual = clientUnderTest.getRepositoriesForUser(username);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.getFirst().name()).isEqualTo("Hello-World"),
                () -> assertThat(actual.getFirst().owner()).isEqualTo(new Owner("octocat")),
                () -> assertThat(actual.getFirst().branchesUrl()).isEqualTo("https://api.github.com/repos/octocat/Hello-World/branches{/branch}")
        );
    }

    @Test
    void should_throw_exception_for_get_repositories_when_non_existing_username_is_given() {
        // given
        String username = "non_existing_user";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.NOT_FOUND.value())
        );

        // when
        // then
        Exception exception = assertThrows(GitHubUserNotFoundException.class, () ->
                clientUnderTest.getRepositoriesForUser(username)
        );
        assertThat(exception.getMessage()).isEqualTo("User %s not exists on GitHub", username);
    }

    @Test
    void should_throw_exception_for_get_repositories_when_api_rate_limit_reached() {
        // given
        String username = "username";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.FORBIDDEN.value())
        );

        // when
        // then
        Exception exception = assertThrows(ApiRateLimitException.class, () ->
                clientUnderTest.getRepositoriesForUser(username)
        );
        assertThat(exception.getMessage()).isEqualTo("GitHub API rate limit exceeded, try again later.");
    }

    @Test
    void should_return_correct_branches_response_when_existing_username_is_given() {
        // given
        String username = "some_user";
        String repository = "some_repository";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.OK.value())
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(mockedResponseBranches200())
        );

        // when
        List<BranchResponse> actual = clientUnderTest.getBranchesForRepository(username, repository);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.getFirst().name()).isEqualTo("master"),
                () -> assertThat(actual.getFirst().commit()).isEqualTo(new Commit("c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc"))
        );
    }

    @Test
    void should_throw_exception_for_get_branches_when_bad_data_is_given() {
        // given
        String username = "some_user";
        String repository = "wrong_repository";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.NOT_FOUND.value())
        );

        // when
        // then
        Exception exception = assertThrows(GitHubServiceException.class, () ->
                clientUnderTest.getBranchesForRepository(username, repository)
        );
        assertThat(exception.getMessage()).isEqualTo("Error fetching branches for repository: %s", repository);
    }
}