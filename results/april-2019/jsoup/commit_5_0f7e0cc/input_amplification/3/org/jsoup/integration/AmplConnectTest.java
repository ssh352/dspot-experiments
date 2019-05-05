package org.jsoup.integration;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.integration.servlets.EchoServlet;
import org.jsoup.integration.servlets.InterruptedServlet;
import org.jsoup.integration.servlets.SlowRider;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


public class AmplConnectTest {
    private static String echoUrl;

    @BeforeClass
    public static void setUp() throws Exception {
        TestServer.start();
        AmplConnectTest.echoUrl = EchoServlet.Url;
    }

    @org.junit.AfterClass
    public static void tearDown() throws Exception {
        TestServer.stop();
    }

    private static String ihVal(String key, Document doc) {
        return doc.select((("th:contains(" + key) + ") + td")).first().text();
    }

    @Ignore
    @Test
    public void canInterruptBodyStringRead() throws IOException, InterruptedException {
        final String[] body = new String[1];
        Thread runner = new Thread(new Runnable() {
            public void run() {
                try {
                    Connection.Response res = Jsoup.connect(SlowRider.Url).timeout((15 * 1000)).execute();
                    body[0] = res.body();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        runner.start();
        Thread.sleep((1000 * 3));
        runner.interrupt();
        Assert.assertTrue(runner.isInterrupted());
        runner.join();
        Assert.assertTrue(((body[0].length()) > 0));
        Assert.assertTrue(body[0].contains("<p>Are you still there?"));
    }

    @Ignore
    @Test
    public void canInterruptDocumentRead() throws IOException, InterruptedException {
        final String[] body = new String[1];
        Thread runner = new Thread(new Runnable() {
            public void run() {
                try {
                    Connection.Response res = Jsoup.connect(SlowRider.Url).timeout((15 * 1000)).execute();
                    body[0] = res.parse().text();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        runner.start();
        Thread.sleep((1000 * 3));
        runner.interrupt();
        Assert.assertTrue(runner.isInterrupted());
        runner.join();
        Assert.assertTrue(((body[0].length()) == 0));
    }

    @Ignore
    @Test
    public void totalTimeout() throws IOException {
        int timeout = 3 * 1000;
        long start = System.currentTimeMillis();
        boolean threw = false;
        try {
            Jsoup.connect(SlowRider.Url).timeout(timeout).get();
        } catch (SocketTimeoutException e) {
            long end = System.currentTimeMillis();
            long took = end - start;
            Assert.assertTrue(("Time taken was " + took), (took > timeout));
            Assert.assertTrue(("Time taken was " + took), (took < (timeout * 1.2)));
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Ignore
    @Test
    public void slowReadOk() throws IOException {
        Document doc = Jsoup.connect(SlowRider.Url).data(SlowRider.MaxTimeParam, "2000").get();
        Element h1 = doc.selectFirst("h1");
        Assert.assertEquals("outatime", h1.text());
    }

    @Ignore
    @Test
    public void infiniteReadSupported() throws IOException {
        Document doc = Jsoup.connect(SlowRider.Url).timeout(0).data(SlowRider.MaxTimeParam, "2000").get();
        Element h1 = doc.selectFirst("h1");
        Assert.assertEquals("outatime", h1.text());
    }

    @Test(timeout = 10000)
    public void postFiles_literalMutationString66717_failAssert0() throws IOException {
        try {
            File thumb = ParseTest.getFile("");
            File html = ParseTest.getFile("/htmltests/google-ipod.html");
            Document res = Jsoup.connect(EchoServlet.Url).data("firstname", "Jay").data("firstPart", thumb.getName(), new FileInputStream(thumb), "image/jpeg").data("secondPart", html.getName(), new FileInputStream(html)).data("surname", "Soup").post();
            AmplConnectTest.ihVal("Parts", res);
            AmplConnectTest.ihVal("Part secondPart ContentType", res);
            AmplConnectTest.ihVal("Part secondPart Name", res);
            AmplConnectTest.ihVal("Part secondPart Filename", res);
            AmplConnectTest.ihVal("Part secondPart Size", res);
            AmplConnectTest.ihVal("Part firstPart ContentType", res);
            AmplConnectTest.ihVal("Part firstPart Name", res);
            AmplConnectTest.ihVal("Part firstPart Filename", res);
            AmplConnectTest.ihVal("Part firstPart Size", res);
            AmplConnectTest.ihVal("firstname", res);
            AmplConnectTest.ihVal("surname", res);

            org.junit.Assert.fail("postFiles_literalMutationString66717 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void postFiles_literalMutationString66733_failAssert0null94386_failAssert0_literalMutationString118852_failAssert0() throws IOException {
        try {
            {
                {
                    File thumb = ParseTest.getFile("/htmltests/thumb.jpg");
                    File html = ParseTest.getFile("");
                    Document res = Jsoup.connect(EchoServlet.Url).data("fistname", "Jay").data("firstPart", thumb.getName(), new FileInputStream(thumb), "image/jpeg").data("secondPart", html.getName(), new FileInputStream(html)).data("surname", "Soup").post();
                    AmplConnectTest.ihVal("Parts", res);
                    AmplConnectTest.ihVal("Part secondPart ContentType", res);
                    AmplConnectTest.ihVal("Part secondPart Name", res);
                    AmplConnectTest.ihVal("Part secondPart Filename", res);
                    AmplConnectTest.ihVal("Part secondPart Size", res);
                    AmplConnectTest.ihVal("Part firstPart ContentType", res);
                    AmplConnectTest.ihVal("Part firstPart Name", null);
                    AmplConnectTest.ihVal("Part firstPart Filename", res);
                    AmplConnectTest.ihVal("Part firstPart Size", res);
                    AmplConnectTest.ihVal("firstname", res);
                    AmplConnectTest.ihVal("surname", res);

                    org.junit.Assert.fail("postFiles_literalMutationString66733 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("postFiles_literalMutationString66733_failAssert0null94386 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("postFiles_literalMutationString66733_failAssert0null94386_failAssert0_literalMutationString118852 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void postFiles_literalMutationString66725_failAssert0() throws IOException {
        try {
            File thumb = ParseTest.getFile("/htmltests/thumb.jpg");
            File html = ParseTest.getFile("");
            Document res = Jsoup.connect(EchoServlet.Url).data("firstname", "Jay").data("firstPart", thumb.getName(), new FileInputStream(thumb), "image/jpeg").data("secondPart", html.getName(), new FileInputStream(html)).data("surname", "Soup").post();
            AmplConnectTest.ihVal("Parts", res);
            AmplConnectTest.ihVal("Part secondPart ContentType", res);
            AmplConnectTest.ihVal("Part secondPart Name", res);
            AmplConnectTest.ihVal("Part secondPart Filename", res);
            AmplConnectTest.ihVal("Part secondPart Size", res);
            AmplConnectTest.ihVal("Part firstPart ContentType", res);
            AmplConnectTest.ihVal("Part firstPart Name", res);
            AmplConnectTest.ihVal("Part firstPart Filename", res);
            AmplConnectTest.ihVal("Part firstPart Size", res);
            AmplConnectTest.ihVal("firstname", res);
            AmplConnectTest.ihVal("surname", res);

            org.junit.Assert.fail("postFiles_literalMutationString66725 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void postFiles_literalMutationString66808_failAssert0null95819_failAssert0_literalMutationString110956_failAssert0() throws IOException {
        try {
            {
                {
                    File thumb = ParseTest.getFile("/htmltests/thumb.jpg");
                    File html = ParseTest.getFile("");
                    Document res = Jsoup.connect(EchoServlet.Url).data("firstname", "Jay").data("firstPart", thumb.getName(), new FileInputStream(thumb), "image/jpeg").data("secondPart", html.getName(), new FileInputStream(html)).data("surname", "Soup").post();
                    AmplConnectTest.ihVal("Parts", res);
                    AmplConnectTest.ihVal("Part secondPart ContentType", res);
                    AmplConnectTest.ihVal("Part secondPart Name", res);
                    AmplConnectTest.ihVal("Part secondPart Filename", res);
                    AmplConnectTest.ihVal("Part secondPart Size", res);
                    AmplConnectTest.ihVal("Part firstPart ContentType", res);
                    AmplConnectTest.ihVal("Part firstPart Na,me", res);
                    AmplConnectTest.ihVal("Part firstPart Filename", res);
                    AmplConnectTest.ihVal("Part firstPart Size", res);
                    AmplConnectTest.ihVal("firstname", res);
                    AmplConnectTest.ihVal(null, res);

                    org.junit.Assert.fail("postFiles_literalMutationString66808 should have thrown NullPointerException");
                }
                org.junit.Assert.fail("postFiles_literalMutationString66808_failAssert0null95819 should have thrown NullPointerException");
            }
            org.junit.Assert.fail("postFiles_literalMutationString66808_failAssert0null95819_failAssert0_literalMutationString110956 should have thrown FileNotFoundException");
        } catch (FileNotFoundException expected) {
            Assert.assertEquals("/tmp/dspot-experiments/dataset/april-2019/jsoup_parent/target/test-classes/org/jsoup/integration (Is a directory)", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesEmptyStreamDuringParseRead_add215868_failAssert0_literalMutationNumber215938_failAssert0_literalMutationNumber217097_failAssert0() throws IOException {
        try {
            {
                {
                    Connection.Response res = Jsoup.connect(InterruptedServlet.Url).timeout(1).execute();
                    boolean threw = false;
                    {
                        Document document = res.parse();
                        document.title();
                        document.title();
                    }
                    org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_add215868 should have thrown IOException");
                }
                org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_add215868_failAssert0_literalMutationNumber215938 should have thrown IOException");
            }
            org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_add215868_failAssert0_literalMutationNumber215938_failAssert0_literalMutationNumber217097 should have thrown SocketTimeoutException");
        } catch (SocketTimeoutException expected) {
            Assert.assertEquals("Read timeout", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesEmptyStreamDuringParseRead_add215866_failAssert0_literalMutationNumber215909_failAssert0_literalMutationNumber217036_failAssert0() throws IOException {
        try {
            {
                {
                    Jsoup.connect(InterruptedServlet.Url);
                    Connection.Response res = Jsoup.connect(InterruptedServlet.Url).timeout(2).execute();
                    boolean threw = false;
                    {
                        Document document = res.parse();
                        document.title();
                    }
                    org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_add215866 should have thrown IOException");
                }
                org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_add215866_failAssert0_literalMutationNumber215909 should have thrown SocketTimeoutException");
            }
            org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_add215866_failAssert0_literalMutationNumber215909_failAssert0_literalMutationNumber217036 should have thrown SocketTimeoutException");
        } catch (SocketTimeoutException expected) {
            Assert.assertEquals("Read timeout", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void handlesEmptyStreamDuringParseRead_literalMutationNumber215861_failAssert0_literalMutationNumber215957_failAssert0_literalMutationNumber216982_failAssert0() throws IOException {
        try {
            {
                {
                    Connection.Response res = Jsoup.connect(InterruptedServlet.Url).timeout(2).execute();
                    boolean threw = false;
                    {
                        Document document = res.parse();
                        document.title();
                    }
                    org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationNumber215861 should have thrown IOException");
                }
                org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationNumber215861_failAssert0_literalMutationNumber215957 should have thrown SocketTimeoutException");
            }
            org.junit.Assert.fail("handlesEmptyStreamDuringParseRead_literalMutationNumber215861_failAssert0_literalMutationNumber215957_failAssert0_literalMutationNumber216982 should have thrown SocketTimeoutException");
        } catch (SocketTimeoutException expected) {
            Assert.assertEquals("Read timeout", expected.getMessage());
        }
    }
}
