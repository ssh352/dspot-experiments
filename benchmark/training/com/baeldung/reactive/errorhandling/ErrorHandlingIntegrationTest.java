package com.baeldung.reactive.errorhandling;


import MediaType.APPLICATION_JSON_UTF8;
import MediaType.TEXT_PLAIN;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@WithMockUser
public class ErrorHandlingIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void givenErrorReturn_whenUsernamePresent_thenOk() throws IOException {
        String s = webTestClient.get().uri("/api/endpoint1?name={username}", "Tony").accept(TEXT_PLAIN).exchange().returnResult(String.class).getResponseBody().blockFirst();
        Assert.assertEquals("Hello, Tony", s);
    }

    @Test
    public void givenErrorReturn_whenNoUsername_thenOk() throws IOException {
        String s = webTestClient.get().uri("/api/endpoint1").accept(TEXT_PLAIN).exchange().returnResult(String.class).getResponseBody().blockFirst();
        Assert.assertEquals("Hello, Stranger", s);
    }

    @Test
    public void givenResumeFallback_whenUsernamePresent_thenOk() throws IOException {
        String s = webTestClient.get().uri("/api/endpoint2?name={username}", "Tony").accept(TEXT_PLAIN).exchange().returnResult(String.class).getResponseBody().blockFirst();
        Assert.assertEquals("Hello, Tony", s);
    }

    @Test
    public void givenResumeFallback_whenNoUsername_thenOk() throws IOException {
        String s = webTestClient.get().uri("/api/endpoint2").accept(TEXT_PLAIN).exchange().returnResult(String.class).getResponseBody().blockFirst();
        Assert.assertEquals("Hello, Stranger", s);
    }

    @Test
    public void givenResumeDynamicValue_whenUsernamePresent_thenOk() throws IOException {
        String s = webTestClient.get().uri("/api/endpoint3?name={username}", "Tony").accept(TEXT_PLAIN).exchange().returnResult(String.class).getResponseBody().blockFirst();
        Assert.assertEquals("Hello, Tony", s);
    }

    @Test
    public void givenResumeDynamicValue_whenNoUsername_thenOk() throws IOException {
        String s = webTestClient.get().uri("/api/endpoint3").accept(TEXT_PLAIN).exchange().returnResult(String.class).getResponseBody().blockFirst();
        Assert.assertEquals("Hi, I looked around for your name but found: No value present", s);
    }

    @Test
    public void givenResumeRethrow_whenUsernamePresent_thenOk() throws IOException {
        String s = webTestClient.get().uri("/api/endpoint4?name={username}", "Tony").accept(TEXT_PLAIN).exchange().returnResult(String.class).getResponseBody().blockFirst();
        Assert.assertEquals("Hello, Tony", s);
    }

    @Test
    public void givenResumeRethrow_whenNoUsername_thenOk() throws IOException {
        webTestClient.get().uri("/api/endpoint4").accept(TEXT_PLAIN).exchange().expectStatus().isBadRequest().expectHeader().contentType(APPLICATION_JSON_UTF8).expectBody().jsonPath("$.message").isNotEmpty().jsonPath("$.message").isEqualTo("please provide a name");
    }

    @Test
    public void givenGlobalErrorHandling_whenUsernamePresent_thenOk() throws IOException {
        String s = webTestClient.get().uri("/api/endpoint5?name={username}", "Tony").accept(TEXT_PLAIN).exchange().returnResult(String.class).getResponseBody().blockFirst();
        Assert.assertEquals("Hello, Tony", s);
    }

    @Test
    public void givenGlobalErrorHandling_whenNoUsername_thenOk() throws IOException {
        webTestClient.get().uri("/api/endpoint5").accept(TEXT_PLAIN).exchange().expectStatus().isBadRequest().expectHeader().contentType(APPLICATION_JSON_UTF8).expectBody().jsonPath("$.message").isNotEmpty().jsonPath("$.message").isEqualTo("please provide a name");
    }
}
