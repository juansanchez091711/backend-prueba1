package com.quanta.bom.infrastructure.adapter;

import com.quanta.bom.domain.mapper.BomItemMapper;
import com.quanta.bom.domain.port.IBomItemCommandApi;
import com.quanta.bom.domain.port.IBomItemCommandService;
import com.quanta.bom.infrastructure.dto.BomItemDto;
import com.quanta.bom.infrastructure.dto.CreateBomItemDto;
import com.quanta.bom.shared.ApiResponseFactory;
import com.quanta.bom.shared.ApiResponseOut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class BomItemCommandController implements IBomItemCommandApi {

    private final IBomItemCommandService service;
    private final BomItemMapper mapper;

    public BomItemCommandController(IBomItemCommandService service, BomItemMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    @PostMapping("/products/{productId}/materials")
    public Mono<ResponseEntity<ApiResponseOut<BomItemDto>>> addMaterial(
            @PathVariable Integer productId, @RequestBody CreateBomItemDto dto) {
        return Mono.fromSupplier(() -> mapper.toModel(productId, dto))
                .flatMap(service::addMaterial)
                .map(model -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponseFactory.ok(mapper.toDto(model))));
    }
}
