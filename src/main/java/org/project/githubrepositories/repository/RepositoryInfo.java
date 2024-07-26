package org.project.githubrepositories.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RepositoryInfo {
    private String name;
    private String ownerLogin;
    private List<BranchInfo> branchInfoList;
}
