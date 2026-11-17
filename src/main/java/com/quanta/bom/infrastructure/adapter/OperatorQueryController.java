package com.quanta.bom.infrastructure.adapter;

import com.quanta.bom.domain.mapper.ProductionCalculationMapper;
import com.quanta.bom.domain.port.IOperatorQueryApi;
import com.quanta.bom.domain.port.IOperatorQueryService;
import com.quanta.bom.infrastructure.dto.OperatorDto;
import com.quanta.bom.shared.ApiResponseFactory;
import com.quanta.bom.shared.ApiResponseOut;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class OperatorQueryController implements IOperatorQueryApi {

    private final IOperatorQueryService service;
    private final ProductionCalculationMapper mapper;

    public OperatorQueryController(IOperatorQueryService service, ProductionCalculationMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    @GetMapping("/operators")
    public Mono<ResponseEntity<ApiResponseOut<List<OperatorDto>>>> findAll() {
        return service.findAll()
                .map(mapper::toDto)
                .collectList()
                .map(list -> ResponseEntity.ok(ApiResponseFactory.ok(list)));
    }
}
