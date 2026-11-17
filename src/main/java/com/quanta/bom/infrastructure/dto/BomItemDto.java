package com.quanta.bom.infrastructure.dto;

import java.math.BigDecimal;

public record BomItemDto(Integer id, Integer productId, String material, BigDecimal quantity) {
}
