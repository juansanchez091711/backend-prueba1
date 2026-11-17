package com.quanta.bom.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.quanta.bom.infrastructure.adapter.OperatorExternalAdapter;
import com.quanta.bom.shared.ApplicationException;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

class OperatorExternalAdapterTest {

    private MockWebServer server;
    private OperatorExternalAdapter adapter;
    private OperatorExternalAdapter timeoutAdapter;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        WebClient client = WebClient.builder().baseUrl(server.url("/").toString()).build();
        adapter = new OperatorExternalAdapter(client, 3000);
        timeoutAdapter = new OperatorExternalAdapter(client, 300);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void mapeaCamposRelevantes() {
        server.enqueue(new MockResponse().setHeader("Content-Type", "application/json").setBody(
                "[{\"id\":1,\"name\":\"Leanne Graham\",\"username\":\"Bret\",\"email\":\"sincere@april.biz\","
                        + "\"phone\":\"1-770-736-8031\",\"address\":{\"city\":\"X\"}}]"));
        StepVerifier.create(adapter.findAll())
                .assertNext(o -> {
                    assertEquals(1, o.id());
                    assertEquals("Leanne Graham", o.name());
                    assertEquals("sincere@april.biz", o.email());
                    assertEquals("1-770-736-8031", o.phone());
                })
                .verifyComplete();
    }

    @Test
    void timeoutDevuelve503() {
        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));
        StepVerifier.create(timeoutAdapter.findAll())
                .expectErrorSatisfies(e -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ((ApplicationException) e).getStatus());
                })
                .verify();
    }

    @Test
    void errorServidorDevuelve503() {
        server.enqueue(new MockResponse().setResponseCode(500));
        StepVerifier.create(adapter.findAll())
                .expectErrorSatisfies(e -> {
                    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ((ApplicationException) e).getStatus());
                })
                .verify();
    }
}
