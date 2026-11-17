package com.quanta.bom.infrastructure.adapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.quanta.bom.domain.model.OperatorModel;
import com.quanta.bom.domain.port.IOperatorExternalPort;
import com.quanta.bom.shared.ApplicationException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class OperatorExternalAdapter implements IOperatorExternalPort {

    private static final String USERS_PATH = "/users";
    private static final String MSG_503 =
            "El servicio externo de operarios no está disponible en este momento. Intente más tarde";

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ExternalUser(Integer id, String name, String email, String phone) {
    }

    private final WebClient webClient;
    private final Duration timeout;

    public OperatorExternalAdapter(WebClient operatorsWebClient,
                                   @Value("${operarios.api.timeout-ms}") long timeoutMs) {
        this.webClient = operatorsWebClient;
        this.timeout = Duration.ofMillis(timeoutMs);
    }

    @Override
    public Flux<OperatorModel> findAll() {
        return webClient.get()
                .uri(USERS_PATH)
                .retrieve()
                .bodyToFlux(ExternalUser.class)
                .timeout(timeout)
                .map(u -> new OperatorModel(u.id(), u.name(), u.email(), u.phone()))
                .onErrorMap(ex -> !(ex instanceof ApplicationException),
                        ex -> new ApplicationException(HttpStatus.SERVICE_UNAVAILABLE, MSG_503, ex));
    }
}
