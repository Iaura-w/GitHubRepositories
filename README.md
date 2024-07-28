# GitHubRepositories
This project is a Java application that retrieves information about user's GitHub non-fork repositories and their branches. 
It uses the GitHub API to fetch repository details, including the repository name, owner's login, and branch information (it's name and last commit sha).

## Technologies
- Java 21
- Spring Boot 3
- Lombok

## Application
- To run application with Gradle: ```$ ./gradlew bootRun```
- The application should be available at: http://localhost:8080
- GET /repositories/{username}: Retrieves a list of repositories for the specified GitHub username along with informations.
## Example responses:
- Response for existing user

  <img src="https://github.com/user-attachments/assets/de680ebb-9447-4efd-86c3-a6551bc14d2e" width=500>

- Response for non existing user

  <img src="https://github.com/user-attachments/assets/6abd1efc-2ab9-4795-9cdb-3d0253b31167" width=500>


