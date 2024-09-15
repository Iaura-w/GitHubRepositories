package org.project.dto;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document
public record RepositoryInfo(
        String id,
        String repositoryName,
        String ownerLogin,
        List<BranchInfo> branches,
        @LastModifiedDate LocalDateTime lastUpdated
) {
    public RepositoryInfo(String repositoryName, String ownerLogin, List<BranchInfo> branches, LocalDateTime lastUpdated) {
        this(null, repositoryName, ownerLogin, branches, lastUpdated);
    }
}
