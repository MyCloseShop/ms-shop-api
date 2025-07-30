# MS-COMMERCE

## Description

A microservice for handling e-commerce operations built with Jakarta EE and Spring Framework. This service manages
product catalog, inventory, orders, and transactions.

## Technologies & Dependencies

- Jakarta EE
- Spring Boot
- Spring Data JPA
- Spring MVC
- Lombok
- PostgreSQL
- Maven

## Setup & Running

1. Ensure you have JDK 17+ installed
2. Install PostgreSQL and create a database
3. Configure database properties in `application.properties`
4. Build the project: `mvn clean install`
5. Run the application: `mvn spring-boot:run`

## API Documentation

### Products

- GET `/api/v1/products` - List all products
- GET `/api/v1/products/{id}` - Get product by ID
- POST `/api/v1/products` - Create new product
- PUT `/api/v1/products/{id}` - Update product
- DELETE `/api/v1/products/{id}` - Delete product

### Orders

- GET `/api/v1/orders` - List all orders
- GET `/api/v1/orders/{id}` - Get order by ID
- POST `/api/v1/orders` - Create new order
- PUT `/api/v1/orders/{id}` - Update order status

## Database Schema

The service uses PostgreSQL with the following main entities:

- Products
- Orders
- Customers
- Transactions

## Contributing

Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.