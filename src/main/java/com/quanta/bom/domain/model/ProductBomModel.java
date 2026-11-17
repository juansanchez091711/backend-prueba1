package com.quanta.bom.domain.model;

import java.util.List;

/** Producto con sus materiales activos (resultado del JOIN de consulta). */
public record ProductBomModel(String productName, List<BomItemModel> items) {
}
