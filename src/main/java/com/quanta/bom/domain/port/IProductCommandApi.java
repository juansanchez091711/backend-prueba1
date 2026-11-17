package com.quanta.bom.domain.port;

import com.quanta.bom.infrastructure.dto.CreateProductDto;
import com.quanta.bom.infrastructure.dto.ProductDto;
import com.quanta.bom.shared.ApiResponseOut;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IProductCommandApi {

    Mono<ResponseEntity<ApiResponseOut<ProductDto>>> create(CreateProductDto dto);
}
