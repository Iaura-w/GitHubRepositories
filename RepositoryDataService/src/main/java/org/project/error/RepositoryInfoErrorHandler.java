package org.project.error;

import lombok.extern.log4j.Log4j2;
import org.project.error.ApiRateLimitException;
import org.project.error.GitHubUserNotFoundException;
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
    public RepositoryErrorResponse userNotFound(GitHubUserNotFoundException exception) {
        final String message = exception.getMessage();
        log.error(message);
        return new RepositoryErrorResponse(HttpStatus.NOT_FOUND, message);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ApiRateLimitException.class)
    @ResponseBody
    public RepositoryErrorResponse rateLimit(ApiRateLimitException exception) {
        final String message = exception.getMessage();
        log.error(message);
        return new RepositoryErrorResponse(HttpStatus.FORBIDDEN, message);
    }
}