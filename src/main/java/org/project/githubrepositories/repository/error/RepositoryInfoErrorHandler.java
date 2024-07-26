package org.project.githubrepositories.repository.error;

import lombok.extern.log4j.Log4j2;
import org.project.githubrepositories.http.error.GitHubUserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Log4j2
public class RepositoryInfoErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GitHubUserNotFoundException.class)
    @ResponseBody
    public RepositoryErrorResponse offerNotFound(GitHubUserNotFoundException exception) {
        final String message = exception.getMessage();
        log.error(message);
        return new RepositoryErrorResponse(HttpStatus.NOT_FOUND, message);
    }
}