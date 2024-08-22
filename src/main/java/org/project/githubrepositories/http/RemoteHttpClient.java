package org.project.githubrepositories.http;

import org.project.githubrepositories.repository.RepositoryInfoDto;

import java.util.List;

public interface RemoteHttpClient {
    List<RepositoryInfoDto> getRepositoriesInformationForUser(String username);
}
