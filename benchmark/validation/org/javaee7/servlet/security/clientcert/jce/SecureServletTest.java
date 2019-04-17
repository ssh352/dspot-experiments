/**
 * Copyright Payara Services Limited *
 */
package org.javaee7.servlet.security.clientcert.jce;


import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import java.net.URL;
import java.util.logging.Logger;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class SecureServletTest {
    private static Logger log = Logger.getLogger(SecureServletTest.class.getName());

    private static final String WEBAPP_SRC = "src/main/webapp";

    @ArquillianResource
    private URL base;

    private URL baseHttps;

    private WebClient webClient;

    private static String clientKeyStorePath;

    @Test
    public void testGetWithCorrectCredentials() throws Exception {
        System.out.println("\n*********** TEST START ***************************\n");
        try {
            // With Bouncy Castle installed, do the request via HTTPS to the secured
            // Servlet
            TextPage page = webClient.getPage(((baseHttps) + "SecureServlet"));
            SecureServletTest.log.info(page.getContent());
            Assert.assertTrue("my GET", page.getContent().contains("principal CN=u1"));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
