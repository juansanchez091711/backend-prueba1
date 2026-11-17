package com.quanta.bom.domain.mapper;

import com.quanta.bom.domain.model.ProductModel;
import com.quanta.bom.infrastructure.dto.CreateProductDto;
import com.quanta.bom.infrastructure.dto.ProductDto;
import com.quanta.bom.infrastructure.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductModel toModel(CreateProductDto dto) {
        return ProductModel.create(dto.name());
    }

    public ProductModel toModel(ProductEntity entity) {
        return new ProductModel(entity.id, entity.name);
    }

    public ProductEntity toEntity(ProductModel model) {
        ProductEntity entity = new ProductEntity();
        entity.id = model.id();
        entity.name = model.name();
        return entity;
    }

    public ProductDto toDto(ProductModel model) {
        return new ProductDto(model.id(), model.name());
    }
}
