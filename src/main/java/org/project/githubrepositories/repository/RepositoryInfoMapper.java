package org.project.githubrepositories.repository;

import org.project.githubrepositories.repository.dto.RepositoryInfoDto;

public class RepositoryInfoMapper {

    public static RepositoryInfoDto mapToRepositoryInfoDto(RepositoryInfo repositoryInfo) {
        return new RepositoryInfoDto(
                repositoryInfo.repositoryName(),
                repositoryInfo.ownerLogin(),
                repositoryInfo.branches(),
                repositoryInfo.lastUpdated()
        );
    }

    public static RepositoryInfo mapToRepositoryInfo(RepositoryInfoDto dto) {
        return new RepositoryInfo(
                dto.repositoryName(),
                dto.ownerLogin(),
                dto.branches(),
                dto.lastUpdated()
        );
    }
}
