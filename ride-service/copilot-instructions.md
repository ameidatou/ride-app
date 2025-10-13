# Copilot Instructions for Ride Service

## Project Structure
- `entity/`: JPA entities
- `repository/`: Spring Data JPA repositories
- `service/`: Business logic
- `controller/`: REST API controllers
- `security/`: JWT and security configuration
- `resources/`: Application configuration and migrations

## Setup
- Build: `mvn clean package`
- Run: `mvn spring-boot:run`
- Docker: `mvn compile jib:dockerBuild`
- Database: PostgreSQL, migrations via Flyway

## Development
- Use Lombok annotations for entities and DTOs
- Document APIs with Swagger annotations
- Secure endpoints with JWT

## Testing
- Unit and integration tests in `src/test/java`
