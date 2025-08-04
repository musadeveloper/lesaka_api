# Smart Safe CashUp API

A Spring Boot REST API for processing daily cash ups from general stores into a smart safe. The API calculates transaction fees, stores profits, and sends funds to a api endpoint.

---

## Features

- Accepts cash up amount
- Calculates transaction fees at 11 cents per R100
- Stores transaction fee data for profit calculation
- Sends the total amount in the safe to an external API
- Swagger UI documentation for easy API exploration

---

## Technologies Used

- Java 17+
- Spring Boot
- Spring Data JPA (Hibernate)
- WebClient (Spring Reactive HTTP client)
- Lombok
- Swagger (springdoc-openapi)
- JUnit & Mockito (unit testing)

- ## API Usage

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Example Request

```http
POST /api/store/cashup
Content-Type: application/json

{
  "name": "Astron Energy (Maitland Road)",
  "amount": 135000
}
