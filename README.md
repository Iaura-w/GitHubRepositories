# GitHubRepositories

This project is a Java application that retrieves information about a user's GitHub non-fork repositories and their branches. It uses the GitHub API to fetch repository details, including the repository name, owner's login, and branch information (branch name and last commit SHA). The application also stores the retrieved repository information in a MongoDB database for 1 hour, ensuring that data can be reused without redundant API calls.

## Technologies

- Java 21
- Spring Boot 3
- Lombok
- MongoDB
- Spring Data MongoDB
- Docker Compose

## Application
- Use the `docker-compose.yml` file to start MongoDB: ``` docker-compose up -d ```
- To run application with Gradle: ```$ ./gradlew bootRun```
- The application should be available at: https://localhost:8080
- GET http://localhost:8080/repositories/{username}: Retrieves a list of repositories for the specified GitHub username along with
  informations.

## Example responses:
- lastUpdated: The timestamp indicating when the repository data was last fetched from GitHub and stored in the MongoDB database.

- Response for existing user

  <img src="https://github.com/user-attachments/assets/7377e368-6d0c-4f11-834f-c7b0ff17e40e" width=500>


- Response for non existing user

  <img src="https://github.com/user-attachments/assets/1c653881-615b-4b26-b581-05e39aea17e5" width=500>
