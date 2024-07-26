package org.project.githubrepositories.repository;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/repositories")
@AllArgsConstructor
public class RepositoryInfoController {
    private final RepositoryInfoService service;

    @GetMapping("/{username}")
    public ResponseEntity<List<RepositoryInfo>> getUserRepositories(@PathVariable String username) {
        List<RepositoryInfo> repositories = service.getRepositoriesInfoForUser(username);
        return ResponseEntity.ok(repositories);
    }
}
