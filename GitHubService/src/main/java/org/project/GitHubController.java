package org.project;

import lombok.AllArgsConstructor;
import org.project.dto.BranchResponse;
import org.project.dto.GitHubResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/github")
@AllArgsConstructor
public class GitHubController {

    private final GitHubService gitHubService;

    @GetMapping("/repositories/{username}")
    public List<GitHubResponse> getRepositoriesForUser(@PathVariable String username) {
        return gitHubService.getRepositoriesForUser(username);
    }

    @GetMapping("/repositories/{username}/{repositoryName}/branches")
    public List<BranchResponse> getBranchesForRepository(@PathVariable String username, @PathVariable String repositoryName) {
        return gitHubService.getBranchesForRepository(username, repositoryName);
    }
}
