package org.project.githubrepositories.repository;

import java.util.List;

public record RepositoryInfo(
        String repositoryName,
        String ownerLogin,
        List<BranchInfo> branches
) {
}
