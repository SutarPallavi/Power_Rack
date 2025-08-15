# PowerRack

An end-to-end e-commerce demo application built with Spring Boot microservices and a Vite + React frontend.

## Tech Stack
- Backend
  - Java 17
  - Spring Boot 2.7.x (Web, Data JPA, Validation)
  - Spring Security (BCrypt, DAO AuthenticationProvider)
  - H2 Database (file-based) for local development
  - Maven (multi-module build)
- Frontend
  - React 18
  - Vite 5 (dev server and bundler)
  - Vanilla CSS (custom theme)

## Architecture
- customer-service (Spring Boot)
  - Port: 8081
  - Auth: Spring Security (BCrypt). Endpoints under `/api/auth`
  - H2 console exposed for local dev: `/h2-console`
- product-service (Spring Boot)
  - Port: 8082
  - Provides product catalog: `/api/products`
- cart-service (Spring Boot)
  - Port: 8083
  - Manages per-user cart: `/api/cart/{username}`
- ecom-frontend (Vite + React)
  - Port: 5173
  - Talks directly to services above during local dev

All three backend services share the same H2 file database for convenience in local development.

## Service URLs
- Frontend: `http://localhost:5173/`
- Customer Service: `http://localhost:8081/`
  - Register: `POST /api/auth/register`
  - Login: `POST /api/auth/login`
  - H2 Console: `http://localhost:8081/h2-console`
- Product Service: `http://localhost:8082/`
  - Products: `GET /api/products`
- Cart Service: `http://localhost:8083/`
  - Get cart: `GET /api/cart/{username}`
  - Add item: `POST /api/cart/{username}/items`
  - Increment: `POST /api/cart/{username}/items/{id}/increment`
  - Decrement: `POST /api/cart/{username}/items/{id}/decrement`
  - Delete: `DELETE /api/cart/{username}/items/{id}`
  - Clear: `DELETE /api/cart/{username}`

## Database (H2)
- JDBC URL: `jdbc:h2:file:~/ecomdb/ecom;AUTO_SERVER=TRUE`
- H2 Console: `http://localhost:8081/h2-console`
- Username: `sa`
- Password: (empty)
- Windows DB file path: `C:\Users\<you>\ecomdb\ecom.mv.db`

Note: Because all services point to the same file-based H2 DB, you can inspect and manage data from any single service’s H2 console during development.

## Build
Requires Java 17+ and Maven.

From the repo root:

```bash
mvn -DskipTests clean package
```

Artifacts:
- `customer-service/target/customer-service-0.0.1-SNAPSHOT.jar`
- `product-service/target/product-service-0.0.1-SNAPSHOT.jar`
- `cart-service/target/cart-service-0.0.1-SNAPSHOT.jar`

Frontend:
```bash
cd ecom-frontend
npm ci
npm run dev -- --host
```

## Run (local)
In three terminals:

```bash
java -jar customer-service/target/customer-service-0.0.1-SNAPSHOT.jar
java -jar product-service/target/product-service-0.0.1-SNAPSHOT.jar
java -jar cart-service/target/cart-service-0.0.1-SNAPSHOT.jar
```

Then open the frontend:
```bash
cd ecom-frontend
npm run dev -- --host
# Visit http://localhost:5173/
```

## Authentication
- Registration hashes passwords with BCrypt (Spring Security), and login is handled via `AuthenticationManager` (DAO auth provider + `UserDetailsService`).
- Unregistered users and invalid passwords receive HTTP 401.

## Notes
- This project is for demo/learning purposes. Do not use the provided dummy tokens in production.
