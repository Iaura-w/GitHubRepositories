package org.project.dto;

public record BranchResponse(
        String name,
        Commit commit
) {
}
