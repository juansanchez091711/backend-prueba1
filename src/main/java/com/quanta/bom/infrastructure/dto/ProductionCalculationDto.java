package com.quanta.bom.infrastructure.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductionCalculationDto(String product, BigDecimal quantity, List<MaterialDto> materials) {

    public record MaterialDto(String material, BigDecimal required) {
    }
}
