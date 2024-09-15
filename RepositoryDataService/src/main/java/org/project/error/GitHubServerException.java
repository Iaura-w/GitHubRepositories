package org.project.error;

import org.springframework.http.HttpStatusCode;

public class GitHubServerException extends RuntimeException {
    public GitHubServerException(String user, HttpStatusCode statusCode) {
        super(String.format("Server error for user {%s}, status code: %s", user, statusCode));
    }
}
