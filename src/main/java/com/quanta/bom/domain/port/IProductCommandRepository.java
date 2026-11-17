package com.quanta.bom.domain.port;

import com.quanta.bom.domain.model.ProductModel;
import reactor.core.publisher.Mono;

public interface IProductCommandRepository {

    Mono<ProductModel> save(ProductModel product);
}
