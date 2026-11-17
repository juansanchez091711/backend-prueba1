package com.quanta.bom.domain.port;

import com.quanta.bom.infrastructure.dto.ProductionCalculationDto;
import com.quanta.bom.shared.ApiResponseOut;
import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IProductionQueryApi {

    Mono<ResponseEntity<ApiResponseOut<ProductionCalculationDto>>> calculate(Integer productId, BigDecimal quantity);
}
