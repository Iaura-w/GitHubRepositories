package org.project;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RepositoryInfoServiceTest {
//
//    @Mock
//    private RemoteHttpClient gitHubHttpClient;
//    @Mock
//    private RepositoryInfoRepository repositoryInfoRepository;
//    private Clock clock;
//    private RepositoryInfoService serviceUnderTest;
//
//    @BeforeEach
//    public void setUp() {
//        clock = Clock.fixed(Instant.parse("2024-08-22T10:15:30.00Z"), ZoneId.of("UTC"));
//        serviceUnderTest = new RepositoryInfoService(gitHubHttpClient, repositoryInfoRepository, clock);
//    }
//
//    @Test
//    void should_return_cached_data_when_data_is_up_to_date() {
//        // given
//        String username = "testUser";
//        LocalDateTime dateTime = LocalDateTime.now(clock);
//
//        List<RepositoryInfo> storedRepositories = List.of(
//                new RepositoryInfo("1", "Repo1", username, List.of(), dateTime.minusMinutes(30))
//        );
//
//        when(repositoryInfoRepository.findAllByOwnerLogin(username)).thenReturn(storedRepositories);
//
//        // when
//        List<RepositoryInfoDto> result = serviceUnderTest.getRepositoriesInfoForUser(username);
//
//        // then
//        Assertions.assertThat(result).isNotEmpty();
//        verify(gitHubHttpClient, never()).getRepositoriesInformationForUser(username);
//        verify(repositoryInfoRepository, never()).deleteAllByOwnerLogin(anyString());
//        verify(repositoryInfoRepository, never()).saveAll(anyList());
//    }
//
//    @Test
//    void should_fetch_from_api_when_data_is_expired() {
//        // given
//        String username = "testUser";
//        LocalDateTime dateTime = LocalDateTime.now(clock);
//
//        List<RepositoryInfo> savedRepositories = List.of(
//                new RepositoryInfo("1", "Repo1", username, List.of(), dateTime.minusHours(2))
//        );
//
//        when(repositoryInfoRepository.findAllByOwnerLogin(username)).thenReturn(savedRepositories);
//        List<RepositoryInfoDto> freshRepositories = List.of(
//                new RepositoryInfoDto("Repo1", username, List.of(), dateTime)
//        );
//        when(gitHubHttpClient.getRepositoriesInformationForUser(username)).thenReturn(freshRepositories);
//
//        // when
//        List<RepositoryInfoDto> result = serviceUnderTest.getRepositoriesInfoForUser(username);
//
//        // then
//        Assertions.assertThat(result).isNotEmpty();
//        verify(gitHubHttpClient).getRepositoriesInformationForUser(username);
//        verify(repositoryInfoRepository).deleteAllByOwnerLogin(username);
//        verify(repositoryInfoRepository).saveAll(anyList());
//    }
//
//    @Test
//    void should_fetch_from_api_when_no_cached_data_is_present() {
//        // given
//        String username = "testUser";
//        LocalDateTime dateTime = LocalDateTime.now(clock);
//        when(repositoryInfoRepository.findAllByOwnerLogin(username)).thenReturn(List.of());
//
//        List<RepositoryInfoDto> freshRepositories = List.of(
//                new RepositoryInfoDto("Repo1", username, List.of(), dateTime)
//        );
//        when(gitHubHttpClient.getRepositoriesInformationForUser(username)).thenReturn(freshRepositories);
//
//        // when
//        List<RepositoryInfoDto> result = serviceUnderTest.getRepositoriesInfoForUser(username);
//
//        // then
//        Assertions.assertThat(result).isNotEmpty();
//        verify(gitHubHttpClient).getRepositoriesInformationForUser(username);
//        verify(repositoryInfoRepository).deleteAllByOwnerLogin(username);
//        verify(repositoryInfoRepository).saveAll(anyList());
//    }
}
