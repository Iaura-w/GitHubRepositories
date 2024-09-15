package org.project;


class GitHubHttpClientTest {
//    public static MockWebServer mockWebServer;
//    public static RemoteHttpClient clientUnderTest;
//
//    @BeforeAll
//    static void setUp() {
//        mockWebServer = new MockWebServer();
//        WebClient mockedWebClient = WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(httpClient()))
//                .baseUrl(mockWebServer.url("/").toString())
//                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
//                .build();
//        clientUnderTest = new GitHubHttpClient(mockedWebClient);
//    }
//
//    @AfterAll
//    static void tearDown() throws IOException {
//        mockWebServer.close();
//    }
//
//    @Test
//    void should_return_correct_response_when_existing_username_is_given() {
//        // given
//        String username = "some_user";
//
//        mockWebServer.enqueue(
//                new MockResponse()
//                        .setResponseCode(HttpStatus.OK.value())
//                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .setBody(mockedResponse200())
//        );
//
//        mockWebServer.enqueue(
//                new MockResponse()
//                        .setResponseCode(HttpStatus.OK.value())
//                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .setBody(mockedResponseBranches200())
//        );
//
//        // when
//        List<RepositoryInfoDto> actual = clientUnderTest.getRepositoriesInformationForUser(username);
//
//        // then
//        assertAll(
//                () -> assertThat(actual).isNotNull(),
//                () -> assertThat(actual).hasSize(1),
//                () -> assertThat(actual.getFirst().repositoryName()).isEqualTo("Hello-World"),
//                () -> assertThat(actual.getFirst().ownerLogin()).isEqualTo("octocat"),
//                () -> assertThat(actual.get(0).branches().get(0).name()).isEqualTo("master")
//        );
//    }
//
//    @Test
//    void should_throw_exception_when_non_existing_username_is_given() {
//        // given
//        String username = "non_existing_user";
//
//        mockWebServer.enqueue(
//                new MockResponse()
//                        .setResponseCode(404)
//        );
//
//        // when
//        // then
//        Exception exception = assertThrows(GitHubUserNotFoundException.class, () ->
//                clientUnderTest.getRepositoriesInformationForUser(username)
//        );
//        assertThat(exception.getMessage()).isEqualTo("User %s not exists on GitHub", username);
//    }
//
//    @Test
//    void should_throw_exception_when_api_rate_limit_reached() {
//        // given
//        String username = "non_existing_user";
//
//        mockWebServer.enqueue(
//                new MockResponse()
//                        .setResponseCode(403)
//        );
//
//        // when
//        // then
//        Exception exception = assertThrows(ApiRateLimitException.class, () ->
//                clientUnderTest.getRepositoriesInformationForUser(username)
//        );
//        assertThat(exception.getMessage()).isEqualTo("GitHub API rate limit exceeded, try again later.");
//    }
}