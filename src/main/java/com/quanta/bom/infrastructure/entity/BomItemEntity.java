package com.quanta.bom.infrastructure.entity;

import com.quanta.bom.shared.AuditableEntity;
import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Table;

@Table("prd_bom_item")
public class BomItemEntity extends AuditableEntity {

    public Integer id;
    public Integer productId;
    public String material;
    public BigDecimal quantity;
}
