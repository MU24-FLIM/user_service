# Microservices application with Spring Boot and MySQL

## User Service

### Getting started

#### 1. Set up environmental variables in each service:

`user_service` and `review_service`:


    DB_URL - URL to the database. (jdbc:mysql://localhost:3306/user_service_db)


    DB_USERNAME - Your server username


    DB_PASSWORD - Your server password 


    USER_CLIENT_URL - http://localhost:8080 


    REVIEW_CLIENT_URL - http://localhost:8081

#### 2. Preparing the MySQL database
Create a MySQL database for each service, `user_db`, `review_db`.

#### 3. Run each Java-application. They create the necessary tables and mock data for each service.

### Endpoints

This part of the task is intended for the `user_service` and his connection with the `review_service`.
User service has all CRUD operations implemented.
When `/user/id` is called, all reviews of that user are displayed.

#### Body for creating and updating user:

```
{
  "username": "ivana",
  "email": "ivana54@gmail.com",
  "age": 30
}
```

| HTTP Method  | URL                                | Descripton        |
|--------------|------------------------------------|-------------------|
| GET          | http:localhost:8080/users          | Get all Users     |
| GET          | http:localhost:8080/users/{userid} | Get User by ID    |
| POST         | http:localhost:8080/users          | Create new User   |
| PUT          | http:localhost:8080/users/{userid} | Update User by ID |
| DELETE       | http:localhost:8080/users/{userid} | Delete User by ID |


|            | DataType | Constraints                     |
|------------|----------|---------------------------------|
| `username` | String   | Not null, size: min: 2, max: 20 |
| `email`    | String   | Not null, size: min: 2, max: 30 |
| `age`      | Byte     | Not null                        |
