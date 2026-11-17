package com.quanta.bom.domain.port;

import com.quanta.bom.domain.model.BomItemModel;
import reactor.core.publisher.Mono;

public interface IBomItemCommandService {

    Mono<BomItemModel> addMaterial(BomItemModel item);
}
