package com.quanta.bom.application.query;

import com.quanta.bom.domain.model.OperatorModel;
import com.quanta.bom.domain.port.IOperatorExternalPort;
import com.quanta.bom.domain.port.IOperatorQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class OperatorQueryService implements IOperatorQueryService {

    private final IOperatorExternalPort externalPort;

    public OperatorQueryService(IOperatorExternalPort externalPort) {
        this.externalPort = externalPort;
    }

    @Override
    public Flux<OperatorModel> findAll() {
        return externalPort.findAll();
    }
}
