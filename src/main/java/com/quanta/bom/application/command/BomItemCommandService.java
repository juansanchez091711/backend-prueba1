package com.quanta.bom.application.command;

import com.quanta.bom.domain.model.BomItemModel;
import com.quanta.bom.domain.port.IBomItemCommandRepository;
import com.quanta.bom.domain.port.IBomItemCommandService;
import com.quanta.bom.shared.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
public class BomItemCommandService implements IBomItemCommandService {

    private final IBomItemCommandRepository repository;
    private final TransactionalOperator transactionalOperator;

    public BomItemCommandService(IBomItemCommandRepository repository, TransactionalOperator transactionalOperator) {
        this.repository = repository;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<BomItemModel> addMaterial(BomItemModel item) {
        Mono<BomItemModel> flow = repository.existsActiveProduct(item.productId())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? repository.save(item)
                        : Mono.error(new ApplicationException(HttpStatus.NOT_FOUND,
                                "El producto " + item.productId() + " no existe")));
        return transactionalOperator.transactional(flow);
    }
}
