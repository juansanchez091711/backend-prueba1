package com.quanta.bom.domain.port;

import com.quanta.bom.domain.model.ProductionCalculationModel;
import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface IProductionQueryService {

    Mono<ProductionCalculationModel> calculate(Integer productId, BigDecimal quantity);
}
