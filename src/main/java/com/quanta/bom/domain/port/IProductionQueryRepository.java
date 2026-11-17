package com.quanta.bom.domain.port;

import com.quanta.bom.domain.model.ProductBomModel;
import reactor.core.publisher.Mono;

public interface IProductionQueryRepository {

    /** Emite el producto activo con sus materiales activos; vacío si el producto no existe. */
    Mono<ProductBomModel> findProductBom(Integer productId);
}
