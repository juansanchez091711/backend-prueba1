package com.quanta.bom.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.quanta.bom.application.command.ProductCommandService;
import com.quanta.bom.domain.model.ProductModel;
import com.quanta.bom.domain.port.IProductCommandRepository;
import com.quanta.bom.shared.ApplicationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ProductCommandServiceTest {

    private final IProductCommandRepository repository = product -> Mono.just(product.withId(1));
    private final TransactionalOperator passThrough = mock(TransactionalOperator.class);

    ProductCommandServiceTest() {
        when(passThrough.transactional(any(Mono.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private final ProductCommandService service = new ProductCommandService(repository, passThrough);

    @Test
    void createDevuelveProductoConId() {
        StepVerifier.create(service.create(ProductModel.create("Zapato")))
                .assertNext(result -> {
                    assertNotNull(result.id());
                    assertEquals("Zapato", result.name());
                })
                .verifyComplete();
    }

    @Test
    void nombreVacioLanza400() {
        ApplicationException ex = assertThrows(ApplicationException.class, () -> ProductModel.create("  "));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("El nombre del producto no puede estar vacío", ex.getMessage());
    }
}
