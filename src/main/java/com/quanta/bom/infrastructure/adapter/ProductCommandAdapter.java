package com.quanta.bom.infrastructure.adapter;

import com.quanta.bom.domain.mapper.ProductMapper;
import com.quanta.bom.domain.model.ProductModel;
import com.quanta.bom.domain.port.IProductCommandRepository;
import com.quanta.bom.infrastructure.entity.ProductEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ProductCommandAdapter implements IProductCommandRepository {

    private static final String INSERT_PRODUCT =
            "INSERT INTO prd_product (name, record_state) VALUES (:name, 'active')";

    private final DatabaseClient databaseClient;
    private final ProductMapper mapper;

    public ProductCommandAdapter(DatabaseClient databaseClient, ProductMapper mapper) {
        this.databaseClient = databaseClient;
        this.mapper = mapper;
    }

    @Override
    public Mono<ProductModel> save(ProductModel product) {
        ProductEntity entity = mapper.toEntity(product);
        return databaseClient.sql(INSERT_PRODUCT)
                .bind("name", entity.name)
                .filter(statement -> statement.returnGeneratedValues("id"))
                .map((row, metadata) -> row.get("id", Integer.class))
                .one()
                .map(product::withId);
    }
}
