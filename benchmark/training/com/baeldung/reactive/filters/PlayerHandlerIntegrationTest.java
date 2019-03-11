package com.baeldung.reactive.filters;


import SpringBootTest.WebEnvironment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@WithMockUser
public class PlayerHandlerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void whenPlayerNameIsBaeldung_thenWebFilterIsApplied() {
        EntityExchangeResult<String> result = webTestClient.get().uri("/players/baeldung").exchange().expectStatus().isOk().expectBody(String.class).returnResult();
        Assert.assertEquals(result.getResponseBody(), "baeldung");
        Assert.assertEquals(result.getResponseHeaders().getFirst("web-filter"), "web-filter-test");
    }

    @Test
    public void whenPlayerNameIsTest_thenHandlerFilterFunctionIsApplied() {
        webTestClient.get().uri("/players/test").exchange().expectStatus().isForbidden();
    }
}

