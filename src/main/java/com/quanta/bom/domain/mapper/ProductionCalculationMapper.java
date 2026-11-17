package com.quanta.bom.domain.mapper;

import com.quanta.bom.domain.model.OperatorModel;
import com.quanta.bom.domain.model.ProductionCalculationModel;
import com.quanta.bom.infrastructure.dto.OperatorDto;
import com.quanta.bom.infrastructure.dto.ProductionCalculationDto;
import org.springframework.stereotype.Component;

@Component
public class ProductionCalculationMapper {

    public ProductionCalculationDto toDto(ProductionCalculationModel model) {
        return new ProductionCalculationDto(model.product(), model.quantity(),
                model.materials().stream()
                        .map(m -> new ProductionCalculationDto.MaterialDto(m.material(), m.required()))
                        .toList());
    }

    public OperatorDto toDto(OperatorModel model) {
        return new OperatorDto(model.id(), model.name(), model.email(), model.phone());
    }
}
