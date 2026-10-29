# Product Management & Order Processing System

## Project Overview
This is a backend REST API project developed using Java, Spring Boot, Spring Data JPA, Hibernate, and MySQL.
The application manages products, categories, inventory, user authentication, and order processing.

## Technologies Used
* Java
* Spring Boot
* Spring Data JPA
* Hibernate
* MySQL
* Spring Security
* JWT Authentication
* Maven
* Postman
* Git and GitHub

## Main Features
### 1. User Authentication

* User registration
* User login
* JWT-based authentication
* Role-based access control

### 2. Product Management
* Create products
* View all products
* View product by ID
* Update product details
* Enable or disable products
### 3. Category Management
* Create product categories
* Associate products with categories
### 4. Inventory Management
* Create inventory records
* View inventory
* View inventory by product ID
* Update stock quantities

### 5. Cart and Order Processing
The planned functionality includes shopping cart management, checkout validation, order creation, and inventory reduction after successful orders.
## Database Tables
* users
* roles
* categories
* products
* inventory
* cart
* cart_items
* addresses
* orders
* order_items
 # REST API Documentation

**Base URL:** `http://localhost:8080`

**Content-Type:** `application/json`

For protected endpoints, include the JWT token in the request header:

`Authorization: Bearer <your-jwt-token>`

## 1. Authentication APIs

| Method | Endpoint              | Description                                | Authentication     |
| ------ | --------------------- | ------------------------------------------ | ------------------ |
| POST   | `/api/auth/register`  | Register a new user                        | Public             |
| POST   | `/api/auth/login`     | Authenticate a user and obtain a JWT token | Public             |
| POST   | `/api/admin/register` | Register an administrator                  | Public, if enabled |
| POST   | `/api/admin/login`    | Authenticate an administrator              | Public, if enabled |

### Register User

**Request:**

```http
POST /api/auth/register
```

Example request body — adjust the field names to match `RegisterRequestDto`:

```json
{
  "name": "Alok",
  "email": "alok@example.com",
  "password": "StrongPassword123"
}
```

### Login User

**Request:**

```http
POST /api/auth/login
```

Example request body — adjust the field names to match `LoginRequestDto`:

```json
{
  "email": "alok@example.com",
  "password": "StrongPassword123"
}
```

A successful login should return the authentication response defined by your application, including the JWT token if that is how your `AuthResponseDto` is structured.

## 2. Category APIs

| Method | Endpoint          | Description         | Authorization        |
| ------ | ----------------- | ------------------- | -------------------- |
| POST   | `/api/categories` | Create a category   | ADMIN or SUPER_ADMIN |
| GET    | `/api/categories` | Retrieve categories | Check SecurityConfig |

### Create Category

```http
POST /api/categories
```

Example request body — confirm the fields against your category entity or request DTO:

```json
{
  "name": "Electronics",
  "description": "Laptops, computers, and mobile phones"
}
```

## 3. Product APIs

| Method | Endpoint                                 | Description                 | Authorization        |
| ------ | ---------------------------------------- | --------------------------- | -------------------- |
| POST   | `/api/products`                          | Create a product            | ADMIN or SUPER_ADMIN |
| GET    | `/api/products`                          | Retrieve products           | Check SecurityConfig |
| GET    | `/api/products/{id}`                     | Retrieve a product by ID    | Check SecurityConfig |
| PUT    | `/api/products/{id}`                     | Update a product            | ADMIN or SUPER_ADMIN |
| PATCH  | `/api/products/{id}/status?enabled=true` | Enable or disable a product | ADMIN or SUPER_ADMIN |

### Create Product

```http
POST /api/products
```

Example request body — adapt it to your actual product request format:

```json
{
  "name": "Dell Laptop",
  "description": "Laptop for gaming",
  "price": 60000,
  "categoryId": 1,
  "enabled": true
}
```

### Get All Products

```http
GET /api/products
```

### Get Product by ID

```http
GET /api/products/1
```

### Update Product

```http
PUT /api/products/1
```

Use the product update request structure defined by your application.

### Enable or Disable Product

```http
PATCH /api/products/1/status?enabled=false
```

Set `enabled=true` to enable the product or `enabled=false` to disable it.

## 4. Inventory APIs

| Method | Endpoint                                  | Description                        | Authorization        |
| ------ | ----------------------------------------- | ---------------------------------- | -------------------- |
| POST   | `/api/inventory`                          | Create inventory for a product     | ADMIN or SUPER_ADMIN |
| GET    | `/api/inventory`                          | Retrieve all inventory             | ADMIN or SUPER_ADMIN |
| GET    | `/api/inventory/{id}`                     | Retrieve inventory by inventory ID | ADMIN or SUPER_ADMIN |
| GET    | `/api/inventory/product/{productId}`      | Retrieve inventory by product ID   | ADMIN or SUPER_ADMIN |
| PUT    | `/api/inventory/{id}?quantity={quantity}` | Update inventory quantity          | ADMIN or SUPER_ADMIN |

### Create Inventory

```http
POST /api/inventory
```

Request body:

```json
{
  "productId": 1,
  "quantity": 10
}
```

### Get All Inventory

```http
GET /api/inventory
```

### Get Inventory by ID

```http
GET /api/inventory/1
```

### Get Inventory by Product ID

```http
GET /api/inventory/product/1
```

### Update Inventory Quantity

```http
PUT /api/inventory/1?quantity=20
```

This request updates the quantity for inventory record ID `1` to `20`.

## 5. Cart APIs

Document these endpoints after confirming their controller mappings and implementation.

Expected operations:

* Add a product to the cart.
* Retrieve the current user's cart.
* Update a cart item's quantity.
* Remove a cart item.

## 6. Address APIs

Document these endpoints after confirming their controller mappings and implementation.

Expected operations:

* Add a delivery address.
* Retrieve the authenticated user's addresses.
* Update an address, if supported.
* Delete an address, if supported.

## 7. Order APIs

Document these endpoints after confirming their controller mappings and implementation.

Expected operations:

* Place an order.
* Retrieve the authenticated user's orders.
* Retrieve order details.
* Retrieve all orders for authorized administrators, if supported.

Before placing an order, the application must validate product availability and inventory. Inventory must only be reduced when order processing succeeds.

## 8. API Testing

Use Postman to test each endpoint.

For every API, verify:

1. The HTTP method and URL.
2. The request body, if required.
3. The authentication token and user role.
4. The HTTP status code.
5. The response body.
6. The corresponding database changes.
7. Error handling for invalid requests.

Only mark an endpoint as implemented and tested after verifying it against the running application.

## Development Environment
* IDE: IntelliJ IDEA
* Database: MySQL
* API Testing: Postman
* Build Tool: Maven
## Project Status
Authentication, basic product management, category management, and inventory APIs have been developed and tested.

Cart management, address management, order processing, and remaining validations are still under development.

## Author

Alok Kumar Lenka
