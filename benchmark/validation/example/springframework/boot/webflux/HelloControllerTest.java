package example.springframework.boot.webflux;


import com.linecorp.armeria.spring.web.reactive.ArmeriaClientAutoConfiguration;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;


@RunWith(SpringRunner.class)
@WebFluxTest(HelloController.class)
@ImportAutoConfiguration(ArmeriaClientAutoConfiguration.class)
public class HelloControllerTest {
    @Inject
    HelloController controller;

    @Test
    public void getHelloWorld() {
        WebTestClient.bindToController(controller).build().get().uri("/hello").exchange().expectStatus().isOk().expectBody(String.class).isEqualTo("Hello, World");
    }
}

