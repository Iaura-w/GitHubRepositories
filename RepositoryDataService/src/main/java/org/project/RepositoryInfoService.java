package org.project;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.project.client.GitHubClientService;
import org.project.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class RepositoryInfoService {
    private final RestTemplate restTemplate;
    private final RepositoryInfoRepository repositoryInfoRepository;
    private final Clock clock;
    private final GitHubClientService gitHubClientService;

    public List<RepositoryInfoDto> getRepositoriesInfoForUser(String username) {
        List<RepositoryInfo> storedRepositories = repositoryInfoRepository.findAllByOwnerLoginIgnoreCase(username);
        if (!storedRepositories.isEmpty() && isDataUpToDate(storedRepositories)) {
            log.info("Found data in db repository for user {}", username);
            return storedRepositories.stream()
                    .map(RepositoryInfoMapper::mapToRepositoryInfoDto)
                    .toList();
        } else {
            log.info("Fetching new data from API for user {}", username);
            List<RepositoryInfoDto> freshRepositories = getRepositoriesAndBranchesForUser(username);
            repositoryInfoRepository.deleteAllByOwnerLoginIgnoreCase(username);
            repositoryInfoRepository.saveAll(freshRepositories.stream()
                    .map(RepositoryInfoMapper::mapToRepositoryInfo)
                    .toList());
            return freshRepositories;
        }
    }

    private boolean isDataUpToDate(List<RepositoryInfo> storedRepositories) {
        LocalDateTime lastUpdated = storedRepositories.getFirst().lastUpdated();
        return lastUpdated.isAfter(LocalDateTime.now(clock).minusHours(1));
    }

    private List<RepositoryInfoDto> getRepositoriesAndBranchesForUser(String username) {
        List<GitHubResponse> repositories = gitHubClientService.fetchRepositoriesForUser(username);
        List<GitHubResponse> nonForkedRepositories = repositories.stream()
                .filter(repo -> !repo.fork())
                .toList();

        return nonForkedRepositories.stream()
                .map(repository -> {
                    List<BranchResponse> branches = gitHubClientService.fetchBranchesForUserAndRepository(username, repository.name());
                    List<BranchInfo> branchInfoList = branches.stream()
                            .map(branch -> new BranchInfo(branch.name(), branch.commit().sha()))
                            .toList();
                    return new RepositoryInfoDto(repository.name(), repository.owner().login(), branchInfoList, LocalDateTime.now());
                })
                .collect(Collectors.toList());
    }
}
