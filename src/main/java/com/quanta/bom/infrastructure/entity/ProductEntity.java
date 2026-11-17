package com.quanta.bom.infrastructure.entity;

import com.quanta.bom.shared.AuditableEntity;
import org.springframework.data.relational.core.mapping.Table;

@Table("prd_product")
public class ProductEntity extends AuditableEntity {

    public Integer id;
    public String name;
}
