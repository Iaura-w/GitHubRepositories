package org.project.githubrepositories.http;

import org.project.githubrepositories.repository.dto.RepositoryInfoDto;

import java.util.List;

public interface RemoteHttpClient {
    List<RepositoryInfoDto> getRepositoriesInformationForUser(String username);
}
