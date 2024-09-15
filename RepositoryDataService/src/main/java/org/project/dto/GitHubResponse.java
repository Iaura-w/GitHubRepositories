package org.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GitHubResponse(
        String name,
        Owner owner,
        boolean fork,
        @JsonProperty("branches_url")
        String branchesUrl) {
}