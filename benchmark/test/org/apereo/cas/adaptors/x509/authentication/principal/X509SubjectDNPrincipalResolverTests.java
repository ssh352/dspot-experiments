package org.apereo.cas.adaptors.x509.authentication.principal;


import java.security.cert.X509Certificate;
import java.util.Optional;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Scott Battaglia
 * @author Jan Van der Velpen
 * @since 3.0.0
 */
public class X509SubjectDNPrincipalResolverTests extends AbstractX509CertificateTests {
    private final X509SubjectDNPrincipalResolver resolver = new X509SubjectDNPrincipalResolver();

    @Test
    public void verifyResolvePrincipalInternal() {
        val c = new X509CertificateCredential(new X509Certificate[]{ AbstractX509CertificateTests.VALID_CERTIFICATE });
        c.setCertificate(AbstractX509CertificateTests.VALID_CERTIFICATE);
        Assertions.assertEquals(AbstractX509CertificateTests.VALID_CERTIFICATE.getSubjectDN().getName(), this.resolver.resolve(c, Optional.of(CoreAuthenticationTestUtils.getPrincipal()), Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler())).getId());
    }

    @Test
    public void verifySupport() {
        val c = new X509CertificateCredential(new X509Certificate[]{ AbstractX509CertificateTests.VALID_CERTIFICATE });
        Assertions.assertTrue(this.resolver.supports(c));
    }

    @Test
    public void verifySupportFalse() {
        Assertions.assertFalse(this.resolver.supports(new UsernamePasswordCredential()));
    }
}
