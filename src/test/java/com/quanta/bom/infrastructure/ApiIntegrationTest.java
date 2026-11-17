package com.quanta.bom.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.quanta.bom.domain.model.OperatorModel;
import com.quanta.bom.domain.port.IOperatorExternalPort;
import com.quanta.bom.shared.ApplicationException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
class ApiIntegrationTest {

    @Autowired
    WebTestClient client;
    @Autowired
    DatabaseClient databaseClient;
    @Autowired
    org.springframework.context.ApplicationContext context;
    @MockitoBean
    IOperatorExternalPort operatorPort;

    private Integer crearProducto(String name) {
        Map<?, ?> body = client.post().uri("/products").bodyValue(Map.of("name", name))
                .exchange().expectStatus().isCreated()
                .expectBody(Map.class).returnResult().getResponseBody();
        assertNotNull(body);
        return (Integer) ((Map<?, ?>) body.get("data")).get("id");
    }

    @Test
    void flywayCreaTablas() {
        StepVerifier.create(databaseClient.sql("SELECT COUNT(*) AS c FROM prd_product")
                        .map((r, m) -> r.get("c", Long.class)).one())
                .expectNextCount(1).verifyComplete();
        StepVerifier.create(databaseClient.sql("SELECT COUNT(*) AS c FROM prd_bom_item")
                        .map((r, m) -> r.get("c", Long.class)).one())
                .expectNextCount(1).verifyComplete();
    }

    @Test
    void crearProductoValido201() {
        client.post().uri("/products").bodyValue(Map.of("name", "Zapato"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isNotEmpty()
                .jsonPath("$.data.name").isEqualTo("Zapato")
                .jsonPath("$.message").isEmpty();
    }

    @Test
    void crearProductoNombreVacio400() {
        client.post().uri("/products").bodyValue(Map.of("name", " "))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").isEqualTo("El nombre del producto no puede estar vacío");
    }

    @Test
    void crearProductoNombreNulo400() {
        client.post().uri("/products").bodyValue(Map.of())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void agregarMaterialYCalcular() {
        Integer id = crearProducto("Zapato BOM");
        agregar(id, "Cuero", 2);
        agregar(id, "Suela", 1);
        agregar(id, "Cordones", 1);
        client.get().uri("/production/calculate?productId={p}&quantity=100", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.product").isEqualTo("Zapato BOM")
                .jsonPath("$.data.materials.length()").isEqualTo(3)
                .jsonPath("$.data.materials[0].material").isEqualTo("Cuero")
                .jsonPath("$.data.materials[0].required").isEqualTo(200.0)
                .jsonPath("$.data.materials[1].required").isEqualTo(100.0)
                .jsonPath("$.data.materials[2].required").isEqualTo(100.0)
                .jsonPath("$.message").isEmpty();
    }

    private void agregar(Integer id, String material, int qty) {
        client.post().uri("/products/{id}/materials", id)
                .bodyValue(Map.of("material", material, "quantity", qty))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.id").isNotEmpty()
                .jsonPath("$.data.productId").isEqualTo(id)
                .jsonPath("$.data.material").isEqualTo(material);
    }

    @Test
    void materialAProductoInexistente404() {
        client.post().uri("/products/9999/materials")
                .bodyValue(Map.of("material", "Cuero", "quantity", 2))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("El producto 9999 no existe");
    }

    @Test
    void materialCantidadInvalida400() {
        client.post().uri("/products/1/materials")
                .bodyValue(Map.of("material", "Cuero", "quantity", 0))
                .exchange()
                .expectStatus().isBadRequest();
        client.post().uri("/products/1/materials")
                .bodyValue(Map.of("material", "", "quantity", 1))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void calculoProductoSinBomDevuelveListaVacia() {
        Integer id = crearProducto("Gorra");
        client.get().uri("/production/calculate?productId={p}&quantity=50", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.materials.length()").isEqualTo(0);
    }

    @Test
    void calculoProductoInexistente404() {
        client.get().uri("/production/calculate?productId=9999&quantity=5")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void calculoCantidadNoPositiva400() {
        client.get().uri("/production/calculate?productId=1&quantity=0")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void operadoresDevuelve200() {
        when(operatorPort.findAll()).thenReturn(
                Flux.just(new OperatorModel(1, "Leanne Graham", "sincere@april.biz", "1-770-736-8031")));
        client.get().uri("/operators")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data[0].name").isEqualTo("Leanne Graham")
                .jsonPath("$.data[0].phone").isEqualTo("1-770-736-8031");
    }

    @Test
    void operadoresApiCaidaDevuelve503SinStacktrace() {
        when(operatorPort.findAll()).thenReturn(Flux.error(
                new ApplicationException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio externo no está disponible")));
        client.get().uri("/operators")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectBody()
                .jsonPath("$.message").isEqualTo("El servicio externo no está disponible")
                .jsonPath("$.trace").doesNotExist();
    }

    @Test
    void webClientEsBeanUnico() {
        assertEquals(1, context.getBeansOfType(WebClient.class).size());
    }
}
