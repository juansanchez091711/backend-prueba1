package com.quanta.bom.application.command;

import com.quanta.bom.domain.model.ProductModel;
import com.quanta.bom.domain.port.IProductCommandRepository;
import com.quanta.bom.domain.port.IProductCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Service
public class ProductCommandService implements IProductCommandService {

    private final IProductCommandRepository repository;
    private final TransactionalOperator transactionalOperator;

    public ProductCommandService(IProductCommandRepository repository, TransactionalOperator transactionalOperator) {
        this.repository = repository;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<ProductModel> create(ProductModel product) {
        return transactionalOperator.transactional(repository.save(product));
    }
}
