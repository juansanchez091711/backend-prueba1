package com.quanta.bom.infrastructure.dto;

import java.math.BigDecimal;

public record CreateBomItemDto(String material, BigDecimal quantity) {
}
