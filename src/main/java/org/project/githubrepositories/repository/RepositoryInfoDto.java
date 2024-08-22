package org.project.githubrepositories.repository;

import java.time.LocalDateTime;
import java.util.List;

public record RepositoryInfoDto(
        String repositoryName,
        String ownerLogin,
        List<BranchInfo> branches,
        LocalDateTime lastUpdated
) {
}
