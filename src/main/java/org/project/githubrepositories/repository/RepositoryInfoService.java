package org.project.githubrepositories.repository;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.project.githubrepositories.http.RemoteHttpClient;
import org.project.githubrepositories.repository.dto.RepositoryInfoDto;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class RepositoryInfoService {
    private final RemoteHttpClient gitHubHttpClient;
    private final RepositoryInfoRepository repositoryInfoRepository;

    public List<RepositoryInfoDto> getRepositoriesInfoForUser(String username) {
        List<RepositoryInfo> storedRepositories = repositoryInfoRepository.findAllByOwnerLogin(username);
        if (!storedRepositories.isEmpty() && isDataUpToDate(storedRepositories)) {
            log.info("Found data in db repository for user {}", username);
            return storedRepositories.stream()
                    .map(RepositoryInfoMapper::mapToRepositoryInfoDto)
                    .toList();
        } else {
            log.info("Fetching new data from API for user {}", username);
            List<RepositoryInfoDto> freshRepositories = gitHubHttpClient.getRepositoriesInformationForUser(username);
            repositoryInfoRepository.deleteAllByOwnerLogin(username);
            repositoryInfoRepository.saveAll(freshRepositories.stream()
                    .map(RepositoryInfoMapper::mapToRepositoryInfo)
                    .toList());
            return freshRepositories;
        }
    }

    private boolean isDataUpToDate(List<RepositoryInfo> storedRepositories) {
        LocalDateTime lastUpdated = storedRepositories.getFirst().lastUpdated();
        return lastUpdated.isAfter(LocalDateTime.now().minusHours(1));
    }
}
