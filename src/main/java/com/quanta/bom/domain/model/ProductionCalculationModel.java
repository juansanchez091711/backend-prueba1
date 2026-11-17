package com.quanta.bom.domain.model;

import com.quanta.bom.shared.ApplicationException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;

public record ProductionCalculationModel(String product, BigDecimal quantity, List<RequiredMaterial> materials) {

    public record RequiredMaterial(String material, BigDecimal required) {
    }

    public static void validateQuantity(BigDecimal quantity) {
        if (quantity == null || quantity.signum() <= 0) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "La cantidad a producir debe ser mayor a cero");
        }
    }

    public static ProductionCalculationModel calculate(ProductBomModel bom, BigDecimal quantity) {
        validateQuantity(quantity);
        List<RequiredMaterial> required = bom.items().stream()
                .map(item -> new RequiredMaterial(item.material(), item.quantity().multiply(quantity)))
                .toList();
        return new ProductionCalculationModel(bom.productName(), quantity, required);
    }
}
