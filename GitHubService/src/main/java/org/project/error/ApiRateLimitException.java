package org.project.error;

public class ApiRateLimitException extends RuntimeException {
    public ApiRateLimitException() {
        super("GitHub API rate limit exceeded, try again later.");
    }
}
