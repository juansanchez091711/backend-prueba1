package com.quanta.bom.domain.port;

import com.quanta.bom.domain.model.OperatorModel;
import reactor.core.publisher.Flux;

public interface IOperatorQueryService {

    Flux<OperatorModel> findAll();
}
