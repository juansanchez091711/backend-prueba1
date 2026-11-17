package com.quanta.bom.domain.model;

import com.quanta.bom.shared.ApplicationException;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;

public record BomItemModel(Integer id, Integer productId, String material, BigDecimal quantity) {

    public static BomItemModel create(Integer productId, String material, BigDecimal quantity) {
        if (material == null || material.isBlank()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "El nombre del material no puede estar vacío");
        }
        if (quantity == null || quantity.signum() <= 0) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "La cantidad del material debe ser mayor a cero");
        }
        return new BomItemModel(null, productId, material.trim(), quantity);
    }

    public BomItemModel withId(Integer newId) {
        return new BomItemModel(newId, productId, material, quantity);
    }
}
