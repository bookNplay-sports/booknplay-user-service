User Service is a microservice of the BookNPlay Sports Turf Booking Platform. It is responsible for managing user registration, login, authentication, and profile management. It also provides forgot password and reset password functionality, securely encrypts passwords using BCrypt, and supports three user roles: CUSTOMER, TURF_OWNER, and ADMIN. The service integrates with the Notification Service to send email notifications for user registration, profile updates, and password reset activities.

Technology Stack: Java 21, Spring Boot, Spring Security, Spring Data JPA, MySQL, Maven, and Eureka Client.

Configuration: Service Name: user-service | Port: 8082 | Database: user_db | Eureka Server: http://localhost:8761

The service is part of the BookNPlay microservices architecture and communicates with other services through REST APIs.User Service
User Service is a microservice of the BookNPlay Sports Turf Booking Platform. It is responsible for managing user registration, login, authentication, and profile management. It also provides forgot password and reset password functionality, securely encrypts passwords using BCrypt, and supports three user roles: CUSTOMER, TURF_OWNER, and ADMIN. The service integrates with the Notification Service to send email notifications for user registration, profile updates, and password reset activities.
