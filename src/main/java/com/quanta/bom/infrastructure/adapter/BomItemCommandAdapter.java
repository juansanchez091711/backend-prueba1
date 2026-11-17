package com.quanta.bom.infrastructure.adapter;

import com.quanta.bom.domain.mapper.BomItemMapper;
import com.quanta.bom.domain.model.BomItemModel;
import com.quanta.bom.domain.port.IBomItemCommandRepository;
import com.quanta.bom.infrastructure.entity.BomItemEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BomItemCommandAdapter implements IBomItemCommandRepository {

    private static final String SELECT_ACTIVE_PRODUCT =
            "SELECT id FROM prd_product WHERE id = :id AND record_state = 'active'";
    private static final String INSERT_ITEM =
            "INSERT INTO prd_bom_item (product_id, material, quantity, record_state) "
                    + "VALUES (:productId, :material, :quantity, 'active')";

    private final DatabaseClient databaseClient;
    private final BomItemMapper mapper;

    public BomItemCommandAdapter(DatabaseClient databaseClient, BomItemMapper mapper) {
        this.databaseClient = databaseClient;
        this.mapper = mapper;
    }

    @Override
    public Mono<Boolean> existsActiveProduct(Integer productId) {
        return databaseClient.sql(SELECT_ACTIVE_PRODUCT)
                .bind("id", productId)
                .map((row, metadata) -> row.get("id", Integer.class))
                .one()
                .map(id -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<BomItemModel> save(BomItemModel item) {
        BomItemEntity entity = mapper.toEntity(item);
        return databaseClient.sql(INSERT_ITEM)
                .bind("productId", entity.productId)
                .bind("material", entity.material)
                .bind("quantity", entity.quantity)
                .filter(statement -> statement.returnGeneratedValues("id"))
                .map((row, metadata) -> row.get("id", Integer.class))
                .one()
                .map(item::withId);
    }
}
