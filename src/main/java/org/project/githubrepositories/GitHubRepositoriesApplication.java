package org.project.githubrepositories;

import org.project.githubrepositories.http.GitHubHttpClient;
import org.project.githubrepositories.repository.RepositoryInfo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class GitHubRepositoriesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitHubRepositoriesApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(GitHubHttpClient client) {
        return args -> {
            List<RepositoryInfo> repositoriesInformationForUser = client.getRepositoriesInformationForUser("Iaura-w");
            System.out.println(repositoriesInformationForUser);
        };
    }
}
