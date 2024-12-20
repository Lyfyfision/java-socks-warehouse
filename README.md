# Socks Management API

## Project Description

The **Socks Management API** provides a RESTful service for managing inventory and operations of socks in a warehouse. This API supports various functionalities—including registering incoming/outgoing socks, querying inventory with filters, and updating sock information. Additionally, it supports the batch upload of sock stocks from an Excel file, and the API is fully documented with Swagger/OpenAPI for simplified interaction.

### Key Features

1. **Registering Socks Income**
    - **Endpoint**: `POST /api/socks/income`
    - **Functionality**: Adds socks to the warehouse inventory based on the provided color, cotton percentage, and quantity.
    - **Example Request**:
      ```json
      {
          "color": "red",
          "cottonPercentage": 50,
          "quantity": 100
      }
      ```  

2. **Registering Socks Outcome**
    - **Endpoint**: `POST /api/socks/outcome`
    - **Functionality**: Deducts socks from the inventory if there is sufficient stock available.
    - **Example Request**:
      ```json
      {
          "color": "white",
          "cottonPercentage": 70,
          "quantity": 20
      }
      ```  

3. **Get Socks Count with Filters**
    - **Endpoint**: `GET /api/socks`
    - **Functionality**: Retrieves the total quantity of socks that satisfy the provided **color**, **cotton percentage range or condition** filters.
    - **Filtering Options**:
        - Filter by a specific cotton percentage range: `30-70`, using the range operator (`-`).
        - Filter by operators `<`, `>` or `=` for cotton percentage.
        - Color-specific filtering.
    - **Example Requests**:
        - `/api/socks?color=red&cottonPercentage=>50`
        - `/api/socks?cottonPercentage=30-70`

4. **Sorted Inventory Retrieval**
    - **Endpoint**: `GET /api/socks/all`
    - **Functionality**: Returns the sorted list of existing socks based on the provided sorting field.
    - **Supported Parameters**:
        - `sortBy`: Field to sort by (e.g., `color`, `cottonPercentage`, etc.).

5. **Updating Sock Information**
    - **Endpoint**: `PUT /api/socks/{id}`
    - **Functionality**: Updates the details of an existing sock entry in the inventory by ID.
    - **Example Request**:
      ```json
      {
          "id": 1,
          "color": "yellow",
          "cottonPercentage": 70,
          "quantity": 20
      }
      ```  

6. **Batch Upload from Excel**
    - **Endpoint**: `POST /api/socks/batch`
    - **Functionality**: Allows warehouse administrators to upload stocks in batch using an `.xlsx` file. The file must contain sock details including `color`, `cottonPercentage`, and `quantity`.

7. **Documentation**
    - Swagger/OpenAPI documentation is available at: `/swagger`. It provides a visual interface to explore, test, and use the API endpoints, and also contains detailed descriptions of available API features.

---

## Technologies Used

The following technologies and tools have been utilized in the design and implementation of the Socks Management API:
- **Programming Language**: Java 17
- **Spring Framework**: Spring Boot 3.4 for creating REST APIs
- **Database**: PostgreSQL, used for storing sock inventory data
- **File Upload**: Supports batch uploads in `.xlsx` (Excel) format
- **Build Tool**: Gradle
- **Testing Tools**: JUnit and Mockito for unit testing and mocking repository dependencies
- **API Documentation**: Swagger/OpenAPI for interactive API documentation

---

## Installation and Setup

### Prerequisites

Ensure the following are installed on your machine:
1. Java Development Kit (JDK) 17+
2. PostgreSQL Database
3. Gradle (tested with version 7.x)

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repository/socks-management.git
   cd socks-management
   ```

2. Update the `application.properties` file with your PostgreSQL configuration:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/socksdb
   spring.datasource.username=<your-database-username>
   spring.datasource.password=<your-database-password>
   ```

3. Build the project using Gradle:
   ```bash
   ./gradlew clean build
   ```

4. Run the application:
   ```bash
   java -jar build/libs/socks-management.jar
   ```

5. Access the Swagger API documentation at `http://localhost:8080/swagger`.

---

## Testing

The application includes unit tests to ensure functionality and data integrity. Testing is implemented using **JUnit** and **Mockito**. You can run all the tests using Gradle:

```bash
./gradlew test
```

---

## API Reference

### Base URL: `http://localhost:8080/api/socks`

### Endpoints

|HTTP Method | Endpoint                  | Description                                  | Example Query                                    |  
|------------|---------------------------|----------------------------------------------|------------------------------------------------|
| **POST**   | `/income`                 | Register the arrival of socks               | `{ "color": "red", "cottonPercentage": 50, "quantity": 100 }`  |  
| **POST**   | `/outcome`                | Register the issuance of socks              | `{ "color": "white", "cottonPercentage": 70, "quantity": 20 }` |  
| **GET**    | `/`                       | Get total sock count using filters          | `/api/socks?color=red&cottonPercentage=>50` |  
| **GET**    | `/all`                    | Retrieve all socks with sorting             | `/api/socks/all?sortBy=color` |  
| **PUT**    | `/{id}`                   | Update sock inventory details               | `{ "id": 1, "color": "yellow", "cottonPercentage": 70, "quantity": 20 }` |  
| **POST**   | `/batch`                  | Upload batch stock from Excel               | Upload `.xlsx` file.|