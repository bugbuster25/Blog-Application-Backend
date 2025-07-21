# RaD-GP-C25-P-G2
## RESTful Blog App

This is a  simple RESTful Blog application built using Spring Boot 3, Spring Data JPA, Hibernate, and Maven. 
The application allows users to perform CRUD operations on users, blog posts and comments.

## Features

This blog application provides APIs to:
- Create, retrieve, update, and delete blog posts
- Add, update, and delete comments on posts
- Manage users associated with posts

The backend is built using Spring Boot and follows a layered architecture (Controller → Service → Repository).


## Prerequisites

Ensure you have the following installed:

- Java 17 or higher  
- Maven 3.8 or higher  
- IntelliJ IDEA (or any Java IDE)  
- Postman or cURL (for testing API endpoints)


## Spring Initializr Setup

The project was initialized using "https://start.spring.io/" with the following configurations:

- Project: Maven
- Language: Java
- Spring Boot Version: 3.5.0
- Project Metadata:
  - Packaging: Jar 
  - Java: 17
- Dependencies: 
  - Spring Web
  - Spring Data JPA
  - H2 Database (for testing)
  - Lombok
  - Spring Boot DevTools
  - Validation
  - Spring Boot Test

## How to Run

You can run the application in your IDE by executing the main method found:

    "src/main/java/com/example/blogapp/BlogappApplication.java"

The server will start on http://localhost:8080

## How to Test the API Endpoints

You can use Postman or any HTTP client to interact with the following REST endpoints.


### POST /api/users

Create a new user.

**Request Body**:

```json
{
  "username": "username123",
  "fullName": "User Name",
  "password": "password123"
}
```

#### 🔹 GET /api/users

Retrieve all users.


#### 🔹 GET /api/users/{id}

Retrieve a user by ID.


### PUT /api/users/{id}

Update an existing user.

**Request Body**:

```json
{
  "username": "updatedUsername",
  "fullName": "User Name",
  "password": "updatedPassword"
}
```

### DELETE /api/users/{id}

Delete a user by ID.


### POST /api/posts

Create a new blog post.

**Request Body**:

```json
{
  "title": "My First Post",
  "content": "This is my first blog post.",
  "userId": 1
}
```

### GET /api/posts

Retrieve all posts.

### GET /api/posts/{id}

Retrieve a specific post by ID.

### PUT /api/posts/{id}

Update an existing post.

**Request Body**:

```json
{
  "title": "Updated Title",
  "content": "Updated Content",
  "userId": 1
}
```

### DELETE /api/posts/{id}

Delete a post by ID.


### POST /api/comments

Add a new comment to a post.

**Request Body**:

```json
{
  "postId": 1,
  "userId": 1,
  "content": "Nice blog."
}
```

### PUT /api/comments/{id}

Update an existing comment.

**Request Body**:

```json
{
  "postId": 1,
  "userId": 1,
  "content": "Updated comment."
}
```

### DELETE /api/comments/{id}

Delete a comment by ID.


## Running Tests

### Unit Tests

Unit tests for services are located under:

    "src/test/java/com/example/blogapp/service/impl/"

Run them via your IDE by simply pressing run. 

### Integration Tests

Integration tests for REST endpoints are located under:

    "src/test/java/com/example/blogapp/controller/"

Run them via your IDE by simply pressing run.

## Technologies Used

* Java 17
* Spring Boot 3
* Spring Data JPA
* Hibernate
* Lombok
* H2 Database (for testing)
* Maven
* JUnit 5 + Mockito + AssertJ (for unit testing)
* Spring Test + MockMvc (for integration testing)


