package org.project;

import lombok.AllArgsConstructor;
import org.project.dto.RepositoryInfoDto;
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
    public ResponseEntity<List<RepositoryInfoDto>> getUserRepositories(@PathVariable String username) {
        List<RepositoryInfoDto> repositories = service.getRepositoriesInfoForUser(username);
        return ResponseEntity.ok(repositories);
    }
}
