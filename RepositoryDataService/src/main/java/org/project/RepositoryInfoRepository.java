package org.project;

import org.project.dto.RepositoryInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RepositoryInfoRepository extends MongoRepository<RepositoryInfo, String> {

    List<RepositoryInfo> findAllByOwnerLogin(String username);

    void deleteAllByOwnerLogin(String username);
}
