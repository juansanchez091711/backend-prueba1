package com.quanta.bom.domain.port;

import com.quanta.bom.infrastructure.dto.BomItemDto;
import com.quanta.bom.infrastructure.dto.CreateBomItemDto;
import com.quanta.bom.shared.ApiResponseOut;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IBomItemCommandApi {

    Mono<ResponseEntity<ApiResponseOut<BomItemDto>>> addMaterial(Integer productId, CreateBomItemDto dto);
}
