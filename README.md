# backend-prueba1 — Microservicio BOM

Microservicio reactivo (Java 21, Spring Boot 3.5, WebFlux, R2DBC + H2, Flyway) para gestionar
productos, su lista de materiales (BOM), calcular materiales de producción y consultar operarios
en una API externa (JSONPlaceholder).

## Arquitectura (CQRS + hexagonal)

Paquete base `com.quanta.bom`:

- `application/command` y `application/query`: servicios (lado escritura / lectura).
- `domain/model`: modelos con validaciones y el cálculo; `domain/port`: interfaces `I*Api`,
  `I*Service`, `I*Repository`, `IOperatorExternalPort`; `domain/mapper`: mappers manuales.
- `infrastructure/adapter`: controllers, adaptadores `DatabaseClient` y cliente externo;
  `infrastructure/entity`, `dto` (Records), `config` (`WebClient` y `TransactionalOperator`).
- `shared`: `ApiResponseOut`, `ApiResponseFactory`, `ApplicationException`, `AuditableEntity`,
  `GlobalExceptionHandler`.

## Requisitos

- Java 21
- Gradle (se incluye `./gradlew`)

## Compilar, probar y ejecutar

```bash
./gradlew build      # compila y corre los tests
./gradlew bootRun    # arranca en http://localhost:8080
```

Configuración en `application.yml`: `operarios.api.base-url` y `operarios.api.timeout-ms`.

## Endpoints

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| POST | `/products` | Crear producto | 201 |
| POST | `/products/{productId}/materials` | Agregar material al BOM | 201 / 404 / 400 |
| GET | `/production/calculate?productId=&quantity=` | Materiales requeridos | 200 / 404 / 400 |
| GET | `/operators` | Operarios desde API externa | 200 / 503 |

```bash
curl -X POST localhost:8080/products -H 'Content-Type: application/json' -d '{"name":"Zapato"}'
curl -X POST localhost:8080/products/1/materials -H 'Content-Type: application/json' \
  -d '{"material":"Cuero","quantity":2}'
curl "localhost:8080/production/calculate?productId=1&quantity=100"
curl localhost:8080/operators
```

Todas las respuestas usan `{ "data": ..., "success": true|false, "message": ... }`.

## Decisiones de diseño

- Solo `DatabaseClient` (sin JPA ni repositorios Spring Data); SQL en constantes con bind params.
- Validaciones y multiplicación (`bom_quantity * quantity`) en el modelo de dominio.
- Producto sin BOM en el cálculo responde 200 con `materials: []`.
- Solo se consideran registros con `record_state = 'active'`.
- `WebClient` es un único bean; timeout y errores externos se traducen a 503 en español.
- Mensajes de error en español, sin stacktrace.
