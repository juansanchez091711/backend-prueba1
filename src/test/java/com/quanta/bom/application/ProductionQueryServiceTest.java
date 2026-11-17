package com.quanta.bom.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quanta.bom.application.query.ProductionQueryService;
import com.quanta.bom.domain.model.BomItemModel;
import com.quanta.bom.domain.model.ProductBomModel;
import com.quanta.bom.domain.port.IProductionQueryRepository;
import com.quanta.bom.shared.ApplicationException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ProductionQueryServiceTest {

    private static BomItemModel item(String material, String qty) {
        return new BomItemModel(1, 1, material, new BigDecimal(qty));
    }

    private final IProductionQueryRepository repository = id -> switch (id) {
        case 1 -> Mono.just(new ProductBomModel("Zapato",
                List.of(item("Cuero", "2"), item("Suela", "1"), item("Cordones", "1"))));
        case 2 -> Mono.just(new ProductBomModel("Gorra", List.of()));
        default -> Mono.empty();
    };
    private final ProductionQueryService service = new ProductionQueryService(repository);

    @Test
    void multiplicaCantidades() {
        StepVerifier.create(service.calculate(1, new BigDecimal("100")))
                .assertNext(r -> {
                    assertEquals("Zapato", r.product());
                    assertEquals(3, r.materials().size());
                    assertEquals(0, new BigDecimal("200").compareTo(r.materials().get(0).required()));
                    assertEquals(0, new BigDecimal("100").compareTo(r.materials().get(1).required()));
                    assertEquals(0, new BigDecimal("100").compareTo(r.materials().get(2).required()));
                })
                .verifyComplete();
    }

    @Test
    void productoSinBomDevuelveListaVacia() {
        StepVerifier.create(service.calculate(2, new BigDecimal("50")))
                .assertNext(r -> assertEquals(0, r.materials().size()))
                .verifyComplete();
    }

    @Test
    void productoInexistenteLanza404() {
        StepVerifier.create(service.calculate(99, new BigDecimal("5")))
                .expectErrorSatisfies(e -> {
                    assertEquals(HttpStatus.NOT_FOUND, ((ApplicationException) e).getStatus());
                    assertEquals("El producto 99 no existe", e.getMessage());
                })
                .verify();
    }

    @Test
    void cantidadNoPositivaLanza400() {
        StepVerifier.create(service.calculate(1, BigDecimal.ZERO))
                .expectErrorSatisfies(e ->
                        assertEquals(HttpStatus.BAD_REQUEST, ((ApplicationException) e).getStatus()))
                .verify();
    }
}
