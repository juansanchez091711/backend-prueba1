package com.quanta.bom.domain.model;

import com.quanta.bom.shared.ApplicationException;
import org.springframework.http.HttpStatus;

public record ProductModel(Integer id, String name) {

    public static ProductModel create(String name) {
        if (name == null || name.isBlank()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "El nombre del producto no puede estar vacío");
        }
        return new ProductModel(null, name.trim());
    }

    public ProductModel withId(Integer newId) {
        return new ProductModel(newId, name);
    }
}
