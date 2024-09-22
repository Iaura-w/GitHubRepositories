package org.project;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.client.RemoteHttpClient;
import org.project.dto.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepositoryInfoServiceTest {

    @Mock
    private RemoteHttpClient gitHubClientService;
    @Mock
    private RepositoryInfoRepository repositoryInfoRepository;
    private Clock clock;
    private RepositoryInfoService serviceUnderTest;

    @BeforeEach
    public void setUp() {
        clock = Clock.fixed(Instant.parse("2024-08-22T10:15:30.00Z"), ZoneId.of("UTC"));
        serviceUnderTest = new RepositoryInfoService(repositoryInfoRepository, clock, gitHubClientService);
    }

    @Test
    void should_return_cached_data_when_data_is_up_to_date() {
        // given
        String username = "testUser";
        LocalDateTime dateTime = LocalDateTime.now(clock);

        List<RepositoryInfo> storedRepositories = List.of(
                new RepositoryInfo("1", "Repo1", username, List.of(), dateTime.minusMinutes(30))
        );

        when(repositoryInfoRepository.findAllByOwnerLoginIgnoreCase(username)).thenReturn(storedRepositories);

        // when
        List<RepositoryInfoDto> result = serviceUnderTest.getRepositoriesInfoForUser(username);

        // then
        Assertions.assertThat(result).isNotEmpty();
        verify(gitHubClientService, never()).fetchRepositoriesForUser(username);
        verify(gitHubClientService, never()).fetchBranchesForUserAndRepository(eq(username), anyString());
        verify(repositoryInfoRepository, never()).deleteAllByOwnerLoginIgnoreCase(anyString());
        verify(repositoryInfoRepository, never()).saveAll(anyList());
    }

    @Test
    void should_fetch_from_api_when_data_is_expired() {
        // given
        String username = "testUser";
        LocalDateTime dateTime = LocalDateTime.now(clock);
        String repositoryName = "Repo1";

        List<RepositoryInfo> savedRepositories = List.of(
                new RepositoryInfo("1", repositoryName, username, List.of(), dateTime.minusHours(2))
        );
        when(repositoryInfoRepository.findAllByOwnerLoginIgnoreCase(username)).thenReturn(savedRepositories);

        List<GitHubResponse> freshRepositories = List.of(
                new GitHubResponse(repositoryName, new Owner(username), false, "url1")
        );
        List<BranchResponse> freshBranches = List.of(
                new BranchResponse("branch1", new Commit("sha1"))
        );
        when(gitHubClientService.fetchRepositoriesForUser(username)).thenReturn(freshRepositories);
        when(gitHubClientService.fetchBranchesForUserAndRepository(username, repositoryName)).thenReturn(freshBranches);

        // when
        List<RepositoryInfoDto> result = serviceUnderTest.getRepositoriesInfoForUser(username);

        // then
        Assertions.assertThat(result).isNotEmpty();
        verify(gitHubClientService).fetchRepositoriesForUser(username);
        verify(gitHubClientService).fetchBranchesForUserAndRepository(username, repositoryName);
        verify(repositoryInfoRepository).deleteAllByOwnerLoginIgnoreCase(username);
        verify(repositoryInfoRepository).saveAll(anyList());
    }

    @Test
    void should_fetch_from_api_when_no_cached_data_is_present() {
        // given
        String username = "testUser";
        String repositoryName = "Repo1";

        when(repositoryInfoRepository.findAllByOwnerLoginIgnoreCase(username)).thenReturn(List.of());

        List<GitHubResponse> freshRepositories = List.of(
                new GitHubResponse(repositoryName, new Owner(username), false, "url1")
        );
        List<BranchResponse> freshBranches = List.of(
                new BranchResponse("branch1", new Commit("sha1"))
        );
        when(gitHubClientService.fetchRepositoriesForUser(username)).thenReturn(freshRepositories);
        when(gitHubClientService.fetchBranchesForUserAndRepository(username, repositoryName)).thenReturn(freshBranches);

        // when
        List<RepositoryInfoDto> result = serviceUnderTest.getRepositoriesInfoForUser(username);

        // then
        Assertions.assertThat(result).isNotEmpty();
        verify(gitHubClientService).fetchRepositoriesForUser(username);
        verify(gitHubClientService).fetchBranchesForUserAndRepository(username, repositoryName);
        verify(repositoryInfoRepository).deleteAllByOwnerLoginIgnoreCase(username);
        verify(repositoryInfoRepository).saveAll(anyList());
    }
}
