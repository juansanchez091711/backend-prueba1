package com.quanta.bom.domain.port;

import com.quanta.bom.infrastructure.dto.OperatorDto;
import com.quanta.bom.shared.ApiResponseOut;
import java.util.List;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IOperatorQueryApi {

    Mono<ResponseEntity<ApiResponseOut<List<OperatorDto>>>> findAll();
}
