package org.project.error;

public class GitHubUserNotFoundException extends RuntimeException {
    public GitHubUserNotFoundException(String user) {
        super(String.format("User %s not exists on GitHub", user));
    }
}
