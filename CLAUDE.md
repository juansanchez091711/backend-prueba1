# backend-prueba1: microservicio BOM (prueba del agente backend)

Microservicio reactivo standalone para la prueba técnica QUAN-1905. Gestiona productos, su
lista de materiales (BOM), calcula materiales para una producción y consulta operarios de una
API externa. Sigue las convenciones de QUANTA, recortadas a lo que aplica aquí.

## Stack
Java 21 · Spring Boot 3.5.4 · WebFlux · R2DBC + H2 en memoria · Flyway · JUnit 5 + StepVerifier.
Build: `./gradlew build` (compila y corre todos los tests).

**No incluye**: multi-tenancy, autenticación, `quanta-common`, auditoría a ms-system, Docker.

## Arquitectura: hexagonal + CQRS
Paquete base `com.quanta.bom`. Estructura por capas:

```
com.quanta.bom
├── domain/
│   ├── model/          # Records de dominio inmutables (Producto, Material, ...)
│   └── exception/      # Excepciones de negocio (NoEncontradoException, ReglaNegocioException)
├── application/
│   ├── port/in/        # Casos de uso: interfaces separadas Command (escritura) / Query (lectura)
│   ├── port/out/       # Puertos de salida (repositorios, clientes externos)
│   └── service/        # Implementaciones de los casos de uso
└── infrastructure/
    ├── adapter/in/web/        # Controllers / handlers, DTOs de entrada y salida
    ├── adapter/out/persistence/  # Adaptadores R2DBC con DatabaseClient
    ├── adapter/out/client/    # WebClient hacia APIs externas
    ├── mapper/                # Mappers manuales
    └── config/                # Beans y configuración
```

Reglas:
- **CQRS**: los casos de uso de escritura (`CrearProductoCommand`...) y de lectura
  (`CalcularProduccionQuery`...) son interfaces distintas. Un servicio no mezcla ambos.
- El **dominio no depende** de Spring, R2DBC ni de la capa web.
- Los controllers solo llaman puertos `in`; nunca a repositorios.

## Persistencia
- Solo **`DatabaseClient`** (SQL explícito). **Prohibido**: JPA/Hibernate, `ReactiveCrudRepository`,
  `R2dbcRepository` y `@Query` de Spring Data.
- Esquema con **Flyway** en `src/main/resources/db/migration`, archivos `V{n}__{descripcion}.sql`.
  Nunca se modifica una migración ya existente: se agrega una nueva.
- Nombres de tablas y columnas en `snake_case`, en español.

## API
- Toda respuesta se envuelve en **`ApiResponseOut<T>`** (record en `infrastructure/adapter/in/web`):
  `success` (boolean), `message` (String, en español), `data` (T).
- Prefijo `/api`. Recursos en plural y en español: `/api/productos`, `/api/operarios`.
- Códigos: 201 al crear, 200 en consultas, 400 validación, 404 no encontrado,
  503 si una API externa falla o vence el timeout.
- Manejo de errores centralizado con `@RestControllerAdvice`; **nunca** se expone el stacktrace.
- **Mensajes de error en español** (p. ej. "El producto 5 no existe").

## Código
- DTOs y modelos de dominio como **Records**.
- **Mappers manuales** (clases con métodos estáticos o componentes): sin MapStruct ni ModelMapper.
- 100 % reactivo: prohibido `.block()`, `.subscribe()` en código de producción, `Thread.sleep`.
- Validación de entrada con Bean Validation (`@NotBlank`, `@Positive`...) en los DTOs de entrada.
- Clientes externos con **WebClient**, `timeout` configurable (`operarios.api.timeout-ms`) y
  manejo de errores que traduce fallas a una excepción de negocio → 503.

## Tests
- Casos de uso y servicios con **StepVerifier** y puertos simulados (Mockito).
- Adaptadores de persistencia contra H2 real (R2DBC) con las migraciones de Flyway.
- Controllers con `WebTestClient`.
- Nombres de test en español que describan el comportamiento: `calculaMaterialesParaCantidadSolicitada`.
- Cada criterio de aceptación tiene al menos un test.

## Git
- Ramas `feature/QUAN-{n}-{slug}` desde `develop`. PR hacia `develop`.
- Commits: `QUAN-{n}: descripción`.

## No tocar
- `gradle/wrapper/`
- `settings.gradle`
- `.gitattributes`
