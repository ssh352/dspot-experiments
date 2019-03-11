package io.dropwizard.jersey.sessions;


import Invocation.Builder;
import MediaType.TEXT_PLAIN;
import io.dropwizard.jersey.AbstractJerseyTest;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;


public class HttpSessionFactoryTest extends AbstractJerseyTest {
    @Test
    public void passesInHttpSessions() throws Exception {
        Response firstResponse = target("/session/").request(TEXT_PLAIN).post(Entity.entity("Mr. Peeps", TEXT_PLAIN));
        final Map<String, NewCookie> cookies = firstResponse.getCookies();
        firstResponse.close();
        Invocation.Builder builder = target("/session/").request().accept(TEXT_PLAIN);
        for (NewCookie cookie : cookies.values()) {
            builder.cookie(cookie);
        }
        assertThat(builder.get(String.class)).isEqualTo("Mr. Peeps");
    }
}

