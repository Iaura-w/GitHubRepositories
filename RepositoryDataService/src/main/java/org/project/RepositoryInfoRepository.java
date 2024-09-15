package org.project;

import org.project.dto.RepositoryInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RepositoryInfoRepository extends MongoRepository<RepositoryInfo, String> {

    List<RepositoryInfo> findAllByOwnerLoginIgnoreCase(String username);

    void deleteAllByOwnerLoginIgnoreCase(String username);
}
