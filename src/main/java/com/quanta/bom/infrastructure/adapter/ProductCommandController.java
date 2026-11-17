package com.quanta.bom.infrastructure.adapter;

import com.quanta.bom.domain.mapper.ProductMapper;
import com.quanta.bom.domain.port.IProductCommandApi;
import com.quanta.bom.domain.port.IProductCommandService;
import com.quanta.bom.infrastructure.dto.CreateProductDto;
import com.quanta.bom.infrastructure.dto.ProductDto;
import com.quanta.bom.shared.ApiResponseFactory;
import com.quanta.bom.shared.ApiResponseOut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ProductCommandController implements IProductCommandApi {

    private final IProductCommandService service;
    private final ProductMapper mapper;

    public ProductCommandController(IProductCommandService service, ProductMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    @PostMapping("/products")
    public Mono<ResponseEntity<ApiResponseOut<ProductDto>>> create(@RequestBody CreateProductDto dto) {
        return Mono.fromSupplier(() -> mapper.toModel(dto))
                .flatMap(service::create)
                .map(model -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponseFactory.ok(mapper.toDto(model))));
    }
}
