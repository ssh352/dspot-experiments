package io.dropwizard.views.freemarker;


import MediaType.TEXT_HTML;
import Response.Status.OK;
import ViewRenderExceptionMapper.TEMPLATE_ERROR_MSG;
import io.dropwizard.logging.BootstrapLogging;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;


public class FreemarkerViewRendererTest extends JerseyTest {
    static {
        BootstrapLogging.bootstrap();
    }

    @Path("/test/")
    @Produces(MediaType.TEXT_HTML)
    public static class ExampleResource {
        @GET
        @Path("/absolute")
        public AbsoluteView showAbsolute() {
            return new AbsoluteView("yay");
        }

        @GET
        @Path("/relative")
        public RelativeView showRelative() {
            return new RelativeView();
        }

        @GET
        @Path("/bad")
        public BadView showBad() {
            return new BadView();
        }

        @GET
        @Path("/error")
        public ErrorView showError() {
            return new ErrorView();
        }

        @POST
        @Path("/auto-escaping")
        public AutoEscapingView showUserInputSafely(@FormParam("input")
        String userInput) {
            return new AutoEscapingView(userInput);
        }
    }

    @Test
    public void rendersViewsWithAbsoluteTemplatePaths() throws Exception {
        final String response = target("/test/absolute").request().get(String.class);
        assertThat(response).isEqualTo("Woop woop. yay\n");
    }

    @Test
    public void rendersViewsWithRelativeTemplatePaths() throws Exception {
        final String response = target("/test/relative").request().get(String.class);
        assertThat(response).isEqualTo("Ok.\n");
    }

    @Test
    public void returnsA500ForViewsWithBadTemplatePaths() throws Exception {
        try {
            target("/test/bad").request().get(String.class);
            failBecauseExceptionWasNotThrown(WebApplicationException.class);
        } catch (WebApplicationException e) {
            assertThat(e.getResponse().getStatus()).isEqualTo(500);
            assertThat(e.getResponse().readEntity(String.class)).isEqualTo(TEMPLATE_ERROR_MSG);
        }
    }

    @Test
    public void rendersViewsUsingUnsafeInputWithAutoEscapingEnabled() throws Exception {
        final String unsafe = "<script>alert(\"hello\")</script>";
        final Response response = target("/test/auto-escaping").request().post(Entity.form(new Form("input", unsafe)));
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        assertThat(response.getHeaderString("content-type")).isEqualToIgnoringCase(TEXT_HTML);
        assertThat(response.readEntity(String.class)).doesNotContain(unsafe);
    }
}

