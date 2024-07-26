package org.project.githubrepositories.repository.error;

import org.springframework.http.HttpStatus;

public record RepositoryErrorResponse(
        HttpStatus status,
        String message
) {
}
