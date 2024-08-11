package org.project.githubrepositories.http;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.project.githubrepositories.http.error.ApiRateLimitException;
import org.project.githubrepositories.http.error.GitHubUserNotFoundException;
import org.project.githubrepositories.repository.RepositoryInfo;
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
import static org.project.githubrepositories.http.GitHubHttpClientConfigTest.httpClient;
import static org.project.githubrepositories.http.GitHubHttpClientConfigTest.mockedResponse200;
import static org.project.githubrepositories.http.GitHubHttpClientConfigTest.mockedResponseBranches200;


class GitHubHttpClientTest {
    public static MockWebServer mockWebServer;
    public static GitHubHttpClient clientUnderTest;

    @BeforeAll
    static void setUp() {
        mockWebServer = new MockWebServer();
        WebClient mockedWebClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(mockWebServer.url("/").toString())
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .build();
        clientUnderTest = new GitHubHttpClient(mockedWebClient);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.close();
    }

    @Test
    void should_return_correct_response_when_existing_username_is_given() {
        // given
        String username = "some_user";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.OK.value())
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(mockedResponse200())
        );

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(HttpStatus.OK.value())
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(mockedResponseBranches200())
        );

        // when
        List<RepositoryInfo> actual = clientUnderTest.getRepositoriesInformationForUser(username);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual.getFirst().repositoryName()).isEqualTo("Hello-World"),
                () -> assertThat(actual.getFirst().ownerLogin()).isEqualTo("octocat"),
                () -> assertThat(actual.get(0).branches().get(0).name()).isEqualTo("master")
        );
    }

    @Test
    void should_throw_exception_when_non_existing_username_is_given() {
        // given
        String username = "non_existing_user";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(404)
        );

        // when
        // then
        Exception exception = assertThrows(GitHubUserNotFoundException.class, () ->
                clientUnderTest.getRepositoriesInformationForUser(username)
        );
        assertThat(exception.getMessage()).isEqualTo("User %s not exists on GitHub", username);
    }

    @Test
    void should_throw_exception_when_api_rate_limit_reached() {
        // given
        String username = "non_existing_user";

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(403)
        );

        // when
        // then
        Exception exception = assertThrows(ApiRateLimitException.class, () ->
                clientUnderTest.getRepositoriesInformationForUser(username)
        );
        assertThat(exception.getMessage()).isEqualTo("GitHub API rate limit exceeded, try again later.");
    }
}