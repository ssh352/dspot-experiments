package com.github.dreamhead.moco.junit;


import com.github.dreamhead.moco.AbstractMocoStandaloneTest;
import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.Moco;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class MocoJunitJsonHttpsRunnerTest extends AbstractMocoStandaloneTest {
    private final HttpsCertificate DEFAULT_CERTIFICATE = HttpsCertificate.certificate(Moco.pathResource("cert.jks"), "mocohttps", "mocohttps");

    @Rule
    public MocoJunitRunner runner = MocoJunitRunner.jsonHttpsRunner(12306, "src/test/resources/foo.json", DEFAULT_CERTIFICATE);

    @Test
    public void should_return_expected_message() throws IOException {
        Assert.assertThat(helper.get(httpsRoot()), CoreMatchers.is("foo"));
    }

    @Test
    public void should_return_expected_message_2() throws IOException {
        Assert.assertThat(helper.get(httpsRoot()), CoreMatchers.is("foo"));
    }
}

