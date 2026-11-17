package com.quanta.bom.infrastructure.adapter;

import com.quanta.bom.domain.mapper.ProductionCalculationMapper;
import com.quanta.bom.domain.port.IProductionQueryApi;
import com.quanta.bom.domain.port.IProductionQueryService;
import com.quanta.bom.infrastructure.dto.ProductionCalculationDto;
import com.quanta.bom.shared.ApiResponseFactory;
import com.quanta.bom.shared.ApiResponseOut;
import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ProductionQueryController implements IProductionQueryApi {

    private final IProductionQueryService service;
    private final ProductionCalculationMapper mapper;

    public ProductionQueryController(IProductionQueryService service, ProductionCalculationMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    @GetMapping("/production/calculate")
    public Mono<ResponseEntity<ApiResponseOut<ProductionCalculationDto>>> calculate(
            @RequestParam Integer productId, @RequestParam BigDecimal quantity) {
        return service.calculate(productId, quantity)
                .map(model -> ResponseEntity.ok(ApiResponseFactory.ok(mapper.toDto(model))));
    }
}
