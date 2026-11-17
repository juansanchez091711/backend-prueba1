package com.quanta.bom.application.query;

import com.quanta.bom.domain.model.ProductionCalculationModel;
import com.quanta.bom.domain.port.IProductionQueryRepository;
import com.quanta.bom.domain.port.IProductionQueryService;
import com.quanta.bom.shared.ApplicationException;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductionQueryService implements IProductionQueryService {

    private final IProductionQueryRepository repository;

    public ProductionQueryService(IProductionQueryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<ProductionCalculationModel> calculate(Integer productId, BigDecimal quantity) {
        return Mono.fromRunnable(() -> ProductionCalculationModel.validateQuantity(quantity))
                .then(repository.findProductBom(productId))
                .switchIfEmpty(Mono.error(new ApplicationException(HttpStatus.NOT_FOUND,
                        "El producto " + productId + " no existe")))
                .map(bom -> ProductionCalculationModel.calculate(bom, quantity));
    }
}
