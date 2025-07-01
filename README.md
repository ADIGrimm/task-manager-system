# Task manager API

This project is a RESTful API for a task management system, designed to help users organize their work efficiently through projects, tasks, and collaboration tools. The API provides endpoints for user authentication, project and task management, commenting, file attachments via Dropbox integration, and label organization. It supports role-based user actions and personal workspace customization.
## Tech Stack

**Frameworks and Libraries:** Spring Boot (Core Framework), MapStruct, Lombok, Liquibase  

**Database:** MySQL, H2

**API Documentation:** SpringDoc OpenAPI

**Security:** Spring Security, JSON Web Tockens 

**Testing:** Mockito, Spring Security Test

**Build and Dependency Management:** Maven


## Usage/Examples

You can see and try all the functions (and models) by activating the project and going to http://localhost:8080/swagger-ui.html (check the port are you working with) by these credentials:
```
    email: admin@email.com
    password: 1234
```


## Installation

**Requeried software:** Java JDK, Maven, MySQL, Docker 

**Installation instruction**

1) Clone the repository
```bash
  git clone https://github.com/ADIGrimm/online-bookstore.git
```

2) Navigate to the project directory
```bash
  cd task-management
```

3) Install dependencies using Maven
```bash
  mvn clean install
```

4) Set up environment variables
```bash
  cp .env.template .env
  # Don't forget to edit .env file to match your configuration
```

5) Build JAR file
```bash
  Create a Dropbox app in your Dropbox App Console.
  Generate a Dropbox access token.
  Add the token to your .env file: DROPBOX_ACCESS_TOKEN=your_generated_access_token
```

6) Build JAR file
```bash
  mvn clean package
```

7) Run the application
```bash
  docker-compose up
```

    
## Running Tests

To run tests, run the following command

```bash
  mvn test
```


## Acknowledgements
 - Mate Academy mentors and students for helping create this API
 - Spring, SQL, Maven, PostMan, and Swagger for access to API development and testing tools
