# Aplazo BNPL API

This is a RESTful API for managing credit lines and processing purchases with installment payment schemes (BNPL), built with Spring Boot 3, Java 17, PostgreSQL, Sprind Data and Maven

---

## 🚀 Getting Started

Follow these steps to get your project up and running on your local machine.

### ⚙️ Installation & Execution

1.  **Navigate to the project directory:**
    ```bash
    cd your-repository-name # Make sure you're in your project's root directory
    ```

2.  **Configure your application:**
    Ensure your `src/main/resources/application.properties` file is correctly configured to connect to your Docker database. Default Docker Compose values are usually aligned.

    Example key configurations:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```

3.  **Spin up the database and init application with Docker compose:**
 
    ```bash
    docker-compose up -d
    ```

The application should start on `http://localhost:8080` (or your configured port).

4.  **Run the Spring Boot application:**

5.  **Load Catalogs and Initial Data:**
    Once the application is running, open your browser or use a tool like Postman/cURL to hit the `seed` endpoint. This will load necessary catalogs (e.g., credit line rules, payment schemas, roles) into the database.
    ```
    POST http://localhost:8080/v1/seed
    ```
    You should see a success message in your browser or tool.


---

## 🛠️ API Usage

The API exposes several endpoints to interact with the credit and purchase management system. But before you need to create and user

---

## **Example Endpoint: User Registration**

`POST /v1/users/register`

```json
{
    "username": "admin",
    "password": "12345"
}
```

## **Example Endpoint: Client Registration**

`POST /v1/customers`

```json
{
  "firstName": "Laura",
  "lastName": "Perez",
  "secondLastName": "Flores",
  "dateOfBirth": "1999/07/21"
}
```

---

## **Example Endpoint: Purchase Registration**

`POST /v1/loans`

```json
{
    "customerId": "9214e1d1-c832-4e36-a7a2-dca5591b2595",
    "amount": 77777
}
```

