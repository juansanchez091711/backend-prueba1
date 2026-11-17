package com.quanta.bom.domain.mapper;

import com.quanta.bom.domain.model.BomItemModel;
import com.quanta.bom.infrastructure.dto.BomItemDto;
import com.quanta.bom.infrastructure.dto.CreateBomItemDto;
import com.quanta.bom.infrastructure.entity.BomItemEntity;
import org.springframework.stereotype.Component;

@Component
public class BomItemMapper {

    public BomItemModel toModel(Integer productId, CreateBomItemDto dto) {
        return BomItemModel.create(productId, dto.material(), dto.quantity());
    }

    public BomItemModel toModel(BomItemEntity entity) {
        return new BomItemModel(entity.id, entity.productId, entity.material, entity.quantity);
    }

    public BomItemEntity toEntity(BomItemModel model) {
        BomItemEntity entity = new BomItemEntity();
        entity.id = model.id();
        entity.productId = model.productId();
        entity.material = model.material();
        entity.quantity = model.quantity();
        return entity;
    }

    public BomItemDto toDto(BomItemModel model) {
        return new BomItemDto(model.id(), model.productId(), model.material(), model.quantity());
    }
}
