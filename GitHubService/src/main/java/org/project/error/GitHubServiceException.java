package org.project.error;

public class GitHubServiceException extends RuntimeException {
    public GitHubServiceException(String message) {
        super(message);
    }
}