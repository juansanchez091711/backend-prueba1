package com.quanta.bom.infrastructure.adapter;

import com.quanta.bom.domain.model.BomItemModel;
import com.quanta.bom.domain.model.ProductBomModel;
import com.quanta.bom.domain.port.IProductionQueryRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ProductionQueryAdapter implements IProductionQueryRepository {

    private static final String SELECT_PRODUCT_BOM =
            "SELECT p.id AS product_id, p.name AS product_name, b.id AS item_id, "
                    + "b.material AS material, b.quantity AS quantity "
                    + "FROM prd_product p "
                    + "LEFT JOIN prd_bom_item b ON b.product_id = p.id AND b.record_state = 'active' "
                    + "WHERE p.id = :productId AND p.record_state = 'active' "
                    + "ORDER BY b.id";

    private record Row(String productName, Integer itemId, String material, BigDecimal quantity) {
    }

    private final DatabaseClient databaseClient;

    public ProductionQueryAdapter(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<ProductBomModel> findProductBom(Integer productId) {
        return databaseClient.sql(SELECT_PRODUCT_BOM)
                .bind("productId", productId)
                .map((row, metadata) -> new Row(
                        row.get("product_name", String.class),
                        row.get("item_id", Integer.class),
                        row.get("material", String.class),
                        row.get("quantity", BigDecimal.class)))
                .all()
                .collectList()
                .filter(rows -> !rows.isEmpty())
                .map(rows -> toModel(productId, rows));
    }

    private ProductBomModel toModel(Integer productId, List<Row> rows) {
        List<BomItemModel> items = new ArrayList<>();
        for (Row r : rows) {
            if (r.itemId() != null) {
                items.add(new BomItemModel(r.itemId(), productId, r.material(), r.quantity()));
            }
        }
        return new ProductBomModel(rows.get(0).productName(), items);
    }
}
