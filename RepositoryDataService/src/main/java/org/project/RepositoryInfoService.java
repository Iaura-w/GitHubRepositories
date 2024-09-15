package org.project;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.project.dto.GitHubResponse;
import org.project.dto.RepositoryInfo;
import org.project.dto.RepositoryInfoDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class RepositoryInfoService {
    private final RestTemplate restTemplate;
    private final RepositoryInfoRepository repositoryInfoRepository;
    private final Clock clock;

    public List<RepositoryInfoDto> getRepositoriesInfoForUser(String username) {
        List<RepositoryInfo> storedRepositories = repositoryInfoRepository.findAllByOwnerLogin(username);
        if (!storedRepositories.isEmpty() && isDataUpToDate(storedRepositories)) {
            log.info("Found data in db repository for user {}", username);
            return storedRepositories.stream()
                    .map(RepositoryInfoMapper::mapToRepositoryInfoDto)
                    .toList();
        } else {
            log.info("Fetching new data from API for user {}", username);
            String url = String.format("http://github-service/github/repositories/%s", username);
            List<GitHubResponse> freshRepositories = restTemplate.getForObject(url, List.class);
            repositoryInfoRepository.deleteAllByOwnerLogin(username);
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
}
