package com.quanta.bom.domain.port;

import com.quanta.bom.domain.model.BomItemModel;
import reactor.core.publisher.Mono;

public interface IBomItemCommandRepository {

    Mono<Boolean> existsActiveProduct(Integer productId);

    Mono<BomItemModel> save(BomItemModel item);
}
