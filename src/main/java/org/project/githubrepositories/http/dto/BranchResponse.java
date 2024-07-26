package org.project.githubrepositories.http.dto;

public record BranchResponse(
        String name,
        Commit commit
) {
}
