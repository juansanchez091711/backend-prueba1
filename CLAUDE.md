# backend-prueba1: microservicio BOM (prueba técnica QUAN-1905)

Microservicio reactivo standalone: productos, BOM (lista de materiales), cálculo de materiales
para producción y consulta de operarios en una API externa.

## Regla principal
**La historia QUAN-1905 y sus subtareas (QUAN-1906 a QUAN-1911) son el contrato.** Rutas, nombres
de clases, paquetes, tablas, columnas, formatos de request/response, códigos HTTP y entregables se
implementan exactamente como los piden las subtareas. Si algo de este archivo no coincide con una
subtarea, gana la subtarea.

## Stack
Java 21 · Spring Boot 3.5.4 · WebFlux · R2DBC + H2 en memoria (modo PostgreSQL) · Flyway ·
JUnit 5 + StepVerifier · MockWebServer (okhttp) para el cliente externo.
Build: `./gradlew build` (compila y corre todos los tests). Ejecutar: `./gradlew bootRun`.

**No incluye**: multi-tenancy, autenticación, `quanta-common`, auditoría a ms-system, Docker.

## Arquitectura: CQRS + hexagonal (QUANTA SDD §3.1)
Paquete base `com.quanta.bom`:

```
com.quanta.bom
├── application/
│   ├── command/        # servicios del lado escritura: IProductCommandService → ProductCommandService ...
│   └── query/          # servicios del lado lectura:  IProductionQueryService → ProductionQueryService ...
├── domain/
│   ├── model/          # ProductModel, BomItemModel, ProductionCalculationModel (validaciones y cálculo)
│   ├── port/           # puertos: IProductCommandApi, IProductCommandRepository, IOperatorExternalPort ...
│   └── mapper/         # ProductMapper, BomItemMapper (@Component, manuales)
├── infrastructure/
│   ├── adapter/        # controllers (implementan I*Api), adaptadores DatabaseClient, OperatorExternalAdapter
│   ├── entity/         # ProductEntity, BomItemEntity (extienden AuditableEntity, @Table)
│   ├── dto/            # Records: CreateProductDto, CreateBomItemDto, ProductionCalculationDto, OperatorDto
│   └── config/         # WebClient @Bean, TransactionalOperator
└── shared/             # clases base locales: ApiResponseOut<T>, ApiResponseFactory,
                        # ApplicationException, AuditableEntity, GlobalExceptionHandler
```

Patrón por caso de uso (ver QUAN-1907): DTO Record → mapper → modelo de dominio (valida) →
puerto interactor `I*Service` → servicio con `TransactionalOperator` → puerto de salida
`I*Repository` → adaptador con `DatabaseClient`. El controller `@RestController` implementa la
interfaz `I*Api`. Las clases base son locales: no se depende de `quanta-common`.

## Persistencia
- Solo **`DatabaseClient`**. SQL en constantes `private static final`, bind params, proyecciones
  explícitas (nunca `SELECT *`) y `returnGeneratedValues("id")` en los INSERT.
  **Prohibido**: JPA/Hibernate, `ReactiveCrudRepository`, `R2dbcRepository`, `@Query` de Spring Data.
- Migraciones Flyway en `src/main/resources/db/migration`, exactamente las de QUAN-1906:
  `V1__create_product_table.sql` (`prd_product`) y `V2__create_bom_item_table.sql` (`prd_bom_item`),
  con columnas de auditoría y `record_state` (por defecto `'active'`).
- Solo se consideran registros con `record_state = 'active'`.

## API
- Rutas sin prefijo: `POST /products`, `POST /products/{productId}/materials`,
  `GET /production/calculate?productId=&quantity=`, `GET /operators`.
- Respuesta siempre en `ApiResponseOut<T>` con `data`, `success`, `message` (en éxito `message: null`).
- Códigos: 201 al crear, 200 en consultas, 400 validación, 404 no encontrado, 503 API externa caída
  o timeout. Producto sin BOM en el cálculo → 200 con `materials: []`.
- `GlobalExceptionHandler` con `@ControllerAdvice`; **mensajes de error en español**; nunca se
  expone el stacktrace.

## Código
- DTOs como **Records**. Mappers **manuales** (`@Component`), sin MapStruct.
- Validaciones de negocio y el cálculo (`bom_quantity * requested_quantity`) en el **modelo de
  dominio**, no en el servicio ni en el controller.
- 100 % reactivo: prohibido `.block()`, `.subscribe()` en producción y `Thread.sleep`.
- `WebClient` como `@Bean` (nunca instanciado por llamada); URL y timeout en `application.yml`
  (`operarios.api.base-url`, `operarios.api.timeout-ms`); errores con `onErrorResume`/`onErrorMap` → 503.

## Tests (QUAN-1911)
- StepVerifier en los servicios: `ProductCommandService.create()` (feliz + nombre vacío),
  `ProductionQueryService` con repositorio simulado (verifica la multiplicación).
- `OperatorExternalAdapter` con **MockWebServer**: feliz + timeout.
- Además, tests de controllers con `WebTestClient` para cubrir los códigos HTTP de cada subtarea.
- Cada criterio de aceptación de cada subtarea tiene al menos un test.

## README (QUAN-1911)
Descripción y arquitectura, requisitos (Java 21, Gradle), cómo compilar y ejecutar, tabla de
endpoints con ejemplos `curl` y decisiones de diseño.

## Git
- Ramas `feature/QUAN-{n}-{slug}` desde `develop`. PR hacia `develop`.
- Commits: `QUAN-{n}: descripción`.

## No tocar
- `gradle/wrapper/`
- `settings.gradle`
- `.gitattributes`
