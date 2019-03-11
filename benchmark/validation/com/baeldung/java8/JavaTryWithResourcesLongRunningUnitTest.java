package com.baeldung.java8;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JavaTryWithResourcesLongRunningUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(JavaTryWithResourcesLongRunningUnitTest.class);

    private static final String TEST_STRING_HELLO_WORLD = "Hello World";

    private Date resource1Date;

    private Date resource2Date;

    // tests
    /* Example for using Try_with_resources */
    @Test
    public void whenWritingToStringWriter_thenCorrectlyWritten() {
        final StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw, true)) {
            pw.print(JavaTryWithResourcesLongRunningUnitTest.TEST_STRING_HELLO_WORLD);
        }
        Assert.assertEquals(sw.getBuffer().toString(), JavaTryWithResourcesLongRunningUnitTest.TEST_STRING_HELLO_WORLD);
    }

    /* Example for using multiple resources */
    @Test
    public void givenStringToScanner_whenWritingToStringWriter_thenCorrectlyWritten() {
        final StringWriter sw = new StringWriter();
        try (Scanner sc = new Scanner(JavaTryWithResourcesLongRunningUnitTest.TEST_STRING_HELLO_WORLD);PrintWriter pw = new PrintWriter(sw, true)) {
            while (sc.hasNext()) {
                pw.print(sc.nextLine());
            } 
        }
        Assert.assertEquals(sw.getBuffer().toString(), JavaTryWithResourcesLongRunningUnitTest.TEST_STRING_HELLO_WORLD);
    }

    /* Example to show order in which the resources are closed */
    @Test
    public void whenFirstAutoClosableResourceIsinitializedFirst_thenFirstAutoClosableResourceIsReleasedFirst() throws Exception {
        try (JavaTryWithResourcesLongRunningUnitTest.AutoCloseableResourcesFirst af = new JavaTryWithResourcesLongRunningUnitTest.AutoCloseableResourcesFirst();JavaTryWithResourcesLongRunningUnitTest.AutoCloseableResourcesSecond as = new JavaTryWithResourcesLongRunningUnitTest.AutoCloseableResourcesSecond()) {
            af.doSomething();
            as.doSomething();
        }
        Assert.assertTrue(resource1Date.after(resource2Date));
    }

    class AutoCloseableResourcesFirst implements AutoCloseable {
        public AutoCloseableResourcesFirst() {
            JavaTryWithResourcesLongRunningUnitTest.LOG.debug("Constructor -> AutoCloseableResources_First");
        }

        public void doSomething() {
            JavaTryWithResourcesLongRunningUnitTest.LOG.debug("Something -> AutoCloseableResources_First");
        }

        @Override
        public void close() throws Exception {
            JavaTryWithResourcesLongRunningUnitTest.LOG.debug("Closed AutoCloseableResources_First");
            resource1Date = new Date();
        }
    }

    class AutoCloseableResourcesSecond implements AutoCloseable {
        public AutoCloseableResourcesSecond() {
            JavaTryWithResourcesLongRunningUnitTest.LOG.debug("Constructor -> AutoCloseableResources_Second");
        }

        public void doSomething() {
            JavaTryWithResourcesLongRunningUnitTest.LOG.debug("Something -> AutoCloseableResources_Second");
        }

        @Override
        public void close() throws Exception {
            JavaTryWithResourcesLongRunningUnitTest.LOG.debug("Closed AutoCloseableResources_Second");
            resource2Date = new Date();
            Thread.sleep(10000);
        }
    }
}

