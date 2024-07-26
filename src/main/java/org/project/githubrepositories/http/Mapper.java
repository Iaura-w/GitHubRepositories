package org.project.githubrepositories.http;

import org.project.githubrepositories.http.dto.BranchResponse;
import org.project.githubrepositories.repository.BranchInfo;

public class Mapper {
    public static BranchInfo branchResponseToBranchInfoMapper(BranchResponse branchResponse) {
        return new BranchInfo(branchResponse.name(), branchResponse.commit().sha());
    }
}
