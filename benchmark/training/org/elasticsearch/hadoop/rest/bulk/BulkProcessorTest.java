package org.elasticsearch.hadoop.rest.bulk;


import BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLERS;
import ConfigurationOptions.ES_BATCH_WRITE_RETRY_LIMIT;
import com.google.common.base.Charsets;
import org.elasticsearch.hadoop.EsHadoopException;
import org.elasticsearch.hadoop.cfg.Settings;
import org.elasticsearch.hadoop.handler.EsHadoopAbortHandlerException;
import org.elasticsearch.hadoop.handler.HandlerResult;
import org.elasticsearch.hadoop.handler.impl.DropAndLog;
import org.elasticsearch.hadoop.rest.Resource;
import org.elasticsearch.hadoop.rest.bulk.handler.BulkWriteErrorHandler;
import org.elasticsearch.hadoop.rest.bulk.handler.BulkWriteFailure;
import org.elasticsearch.hadoop.rest.bulk.handler.DelayableErrorCollector;
import org.elasticsearch.hadoop.rest.bulk.handler.impl.BulkWriteHandlerLoader;
import org.elasticsearch.hadoop.rest.stats.Stats;
import org.elasticsearch.hadoop.util.BytesArray;
import org.elasticsearch.hadoop.util.ClusterInfo;
import org.elasticsearch.hadoop.util.EsMajorVersion;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class BulkProcessorTest {
    private BulkOutputGenerator generator;

    private ClusterInfo esClusterInfo;

    private Settings testSettings;

    private String inputEntry;

    private Resource resource;

    public BulkProcessorTest(EsMajorVersion version, BulkOutputGenerator generator) {
        this.esClusterInfo = ClusterInfo.unnamedClusterWithVersion(version);
        this.generator = generator;
    }

    @Test
    public void testBulk00_Empty() throws Exception {
        BulkProcessor processor = getBulkProcessor();
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(0, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(0, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(0, stats.bulkRetries);
        Assert.assertEquals(0, stats.docsRetried);
        Assert.assertEquals(0, stats.docsAccepted);
    }

    @Test
    public void testBulk00_SuccessFromEmptyResponse() throws Exception {
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(5, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(0, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(0, stats.bulkRetries);
        Assert.assertEquals(0, stats.docsRetried);
        Assert.assertEquals(5, stats.docsAccepted);
    }

    @Test
    public void testBulk00_Success() throws Exception {
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(5, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(0, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(0, stats.bulkRetries);
        Assert.assertEquals(0, stats.docsRetried);
        Assert.assertEquals(5, stats.docsAccepted);
    }

    @Test
    public void testBulk01_OneRetry() throws Exception {
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addSuccess("index", 201).addRejection("index").addSuccess("index", 201).addRejection("index").addRejection("index").generate(), generator.setInfo(resource, 56).addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(5, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(0, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(1, stats.bulkRetries);
        Assert.assertEquals(3, stats.docsRetried);
        Assert.assertEquals(5, stats.docsAccepted);
    }

    @Test
    public void testBulk01_ThreeRetries() throws Exception {
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addSuccess("index", 201).addRejection("index").addSuccess("index", 201).addRejection("index").addRejection("index").generate(), generator.setInfo(resource, 56).addSuccess("index", 201).addRejection("index").addSuccess("index", 201).generate(), generator.setInfo(resource, 56).addRejection("index").generate(), generator.setInfo(resource, 56).addSuccess("index", 201).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(5, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(0, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(3, stats.bulkRetries);
        Assert.assertEquals(5, stats.docsRetried);
        Assert.assertEquals(5, stats.docsAccepted);
    }

    @Test
    public void testBulk02_OneRejectionBasedFailure() throws Exception {
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addSuccess("index", 201).addRejection("index").addSuccess("index", 201).addRejection("index").addRejection("index").generate(), generator.setInfo(resource, 56).addSuccess("index", 201).addRejection("index").addSuccess("index", 201).generate(), generator.setInfo(resource, 56).addRejection("index").generate(), generator.setInfo(resource, 56).addRejection("index").generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(4, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(1, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(3, stats.bulkRetries);
        Assert.assertEquals(5, stats.docsRetried);
        Assert.assertEquals(4, stats.docsAccepted);
    }

    @Test
    public void testBulk02_OneExplicitFailure() throws Exception {
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addSuccess("index", 201).addFailure("index", 401, "conflict", "This data is bogus").addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(4, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(1, bulkResponse.getDocsAborted());
        Assert.assertEquals("This data is bogus", bulkResponse.getDocumentErrors().get(0).getError().getMessage());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(0, stats.bulkRetries);
        Assert.assertEquals(0, stats.docsRetried);
        Assert.assertEquals(4, stats.docsAccepted);
    }

    @Test
    public void testBulk02_RejectionAndExplicitFailure() throws Exception {
        BulkProcessor processor = getBulkProcessor(// This will be retried still
        generator.setInfo(resource, 56).addSuccess("index", 201).addFailure("index", 401, "conflict", "This data is bogus").addSuccess("index", 201).addRejection("index").addSuccess("index", 201).generate(), generator.setInfo(resource, 56).addSuccess("index", 201).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(4, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(1, bulkResponse.getDocsAborted());
        Assert.assertEquals("This data is bogus", bulkResponse.getDocumentErrors().get(0).getError().getMessage());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(1, stats.bulkRetries);
        Assert.assertEquals(1, stats.docsRetried);
        Assert.assertEquals(4, stats.docsAccepted);
    }

    @Test
    public void testBulk03_LogOneRejectionBasedFailure() throws Exception {
        testSettings.setProperty(ES_WRITE_REST_ERROR_HANDLERS, "log");
        testSettings.setProperty((((BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLER) + ".log.") + (DropAndLog.CONF_LOGGER_NAME)), this.getClass().getName());
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addSuccess("index", 201).addRejection("index").addSuccess("index", 201).addRejection("index").addRejection("index").generate(), generator.setInfo(resource, 56).addSuccess("index", 201).addRejection("index").addSuccess("index", 201).generate(), generator.setInfo(resource, 56).addRejection("index").generate(), generator.setInfo(resource, 56).addRejection("index").generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(4, bulkResponse.getDocsSent());
        Assert.assertEquals(1, bulkResponse.getDocsSkipped());
        Assert.assertEquals(0, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(3, stats.bulkRetries);
        Assert.assertEquals(5, stats.docsRetried);
        Assert.assertEquals(4, stats.docsAccepted);
    }

    @Test
    public void testBulk03_LogOneExplicitFailure() throws Exception {
        testSettings.setProperty(ES_WRITE_REST_ERROR_HANDLERS, "log");
        testSettings.setProperty((((BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLER) + ".log.") + (DropAndLog.CONF_LOGGER_NAME)), this.getClass().getName());
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addSuccess("index", 201).addFailure("index", 401, "conflict", "This data is bogus").addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(4, bulkResponse.getDocsSent());
        Assert.assertEquals(1, bulkResponse.getDocsSkipped());
        Assert.assertEquals(0, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(0, stats.bulkRetries);
        Assert.assertEquals(0, stats.docsRetried);
        Assert.assertEquals(4, stats.docsAccepted);
    }

    @Test
    public void testBulk03_LogRejectionAndExplicitFailure() throws Exception {
        testSettings.setProperty(ES_WRITE_REST_ERROR_HANDLERS, "log");
        testSettings.setProperty((((BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLER) + ".log.") + (DropAndLog.CONF_LOGGER_NAME)), this.getClass().getName());
        BulkProcessor processor = getBulkProcessor(// This will be retried still
        generator.setInfo(resource, 56).addSuccess("index", 201).addFailure("index", 401, "conflict", "This data is bogus").addSuccess("index", 201).addRejection("index").addSuccess("index", 201).generate(), generator.setInfo(resource, 56).addSuccess("index", 201).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(4, bulkResponse.getDocsSent());
        Assert.assertEquals(1, bulkResponse.getDocsSkipped());
        Assert.assertEquals(0, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(1, stats.bulkRetries);
        Assert.assertEquals(1, stats.docsRetried);
        Assert.assertEquals(4, stats.docsAccepted);
    }

    /**
     * Case: User chooses retry in place
     *
     * Request 1) One record fails
     * Handler => Break input stream and retry the bytes
     * Request 2) Fails again
     * Handler => Should find attempt to be #2, copy bytes to byte array and retry
     * Request 3) Fails again
     * Handler => Should find attempt to be #3, copy bytes to byte array and replace element data value
     * Request 4) Fails again
     * Handler => Should find attempt to be #4. Retry in place.
     * Request 5) Succeed
     *
     * Tracking bytes array should be grown with this test. Find a way to verify that the data is recopied on attempt #4
     */
    public static class ComplicatedRetryHandler extends BulkWriteErrorHandler {
        private int attemptCount = 0;

        @Override
        public HandlerResult onError(BulkWriteFailure entry, DelayableErrorCollector<byte[]> collector) throws Exception {
            (attemptCount)++;
            if ((attemptCount) == 1) {
                Assert.assertEquals(1, entry.getNumberOfAttempts());
                Assert.assertEquals(401, entry.getResponseCode());
                // Unwrap the underlying master byte array. Should not be done in client code. Testing use only.
                return collector.retry(bytes());
            } else
                if ((attemptCount) == 2) {
                    Assert.assertEquals(2, entry.getNumberOfAttempts());
                    Assert.assertEquals(402, entry.getResponseCode());
                    // Copy the bytes into a byte array and retry the byte array.
                    byte[] copyDoc = BulkProcessorTest.copyDocumentBytes(entry);
                    return collector.retry(copyDoc);
                } else
                    if ((attemptCount) == 3) {
                        Assert.assertEquals(3, entry.getNumberOfAttempts());
                        Assert.assertEquals(403, entry.getResponseCode());
                        // Copy to string, replace element value 'A' with 'X' and retry document.
                        byte[] copyDoc = BulkProcessorTest.copyDocumentBytes(entry);
                        String data = new BytesArray(copyDoc).toString();
                        String newData = data.replace("A", "X");
                        return collector.retry(newData.getBytes(Charsets.UTF_8));
                    } else
                        if ((attemptCount) == 4) {
                            Assert.assertEquals(1, entry.getNumberOfAttempts());// New document should reset attempt count

                            Assert.assertEquals(404, entry.getResponseCode());
                            // Retry in place
                            return collector.retry();
                        } else {
                            throw new AssertionError("Invalid attempts");
                        }



        }
    }

    @Test
    public void testBulk04_ComplicatedHandlerRetries() throws Exception {
        testSettings.setProperty(ES_WRITE_REST_ERROR_HANDLERS, "complicated");
        testSettings.setProperty(((BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLER) + ".complicated"), BulkProcessorTest.ComplicatedRetryHandler.class.getName());
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addFailure("index", 401, "conflict", "This data is bogus").addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).generate(), generator.setInfo(resource, 56).addFailure("index", 402, "conflict", "This data is bogus").generate(), generator.setInfo(resource, 56).addFailure("index", 403, "conflict", "This data is bogus").generate(), generator.setInfo(resource, 56).addFailure("index", 404, "conflict", "This data is bogus").generate(), generator.setInfo(resource, 56).addSuccess("index", 201).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(5, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(0, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(4, stats.bulkRetries);
        Assert.assertEquals(4, stats.docsRetried);
        Assert.assertEquals(5, stats.docsAccepted);
    }

    /**
     * Case: Rolling data copying.
     *
     * Request 1) All documents failed
     * Handler => Always copy bytes and advance element value by 1 letter
     * Request 2) Half documents failed
     * Handler continues the same
     * Request 3) Only 1 document fails
     * Handler continues
     * Request 4) All succeeds.
     */
    public static class IncrementingHandler extends BulkWriteErrorHandler {
        @Override
        public HandlerResult onError(BulkWriteFailure entry, DelayableErrorCollector<byte[]> collector) throws Exception {
            Assert.assertEquals(401, entry.getResponseCode());
            // Copy the bytes into a byte array and retry the byte array.
            byte[] copyDoc = BulkProcessorTest.copyDocumentBytes(entry);
            copyDoc[25] = ((byte) ((copyDoc[25]) + 1));
            return collector.retry(copyDoc);
        }
    }

    @Test
    public void testBulk05_StackedNewDocuments() throws Exception {
        testSettings.setProperty(ES_WRITE_REST_ERROR_HANDLERS, "incrementer");
        testSettings.setProperty(((BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLER) + ".incrementer"), BulkProcessorTest.IncrementingHandler.class.getName());
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addFailure("index", 401, "conflict", "This data is bogus").addFailure("index", 401, "conflict", "This data is bogus").addFailure("index", 401, "conflict", "This data is bogus").addFailure("index", 401, "conflict", "This data is bogus").addFailure("index", 401, "conflict", "This data is bogus").generate(), generator.setInfo(resource, 56).addFailure("index", 401, "conflict", "This data is bogus").addSuccess("index", 201).addFailure("index", 401, "conflict", "This data is bogus").addSuccess("index", 201).addFailure("index", 401, "conflict", "This data is bogus").generate(), generator.setInfo(resource, 56).addSuccess("index", 201).addFailure("index", 401, "conflict", "This data is bogus").addSuccess("index", 201).generate(), generator.setInfo(resource, 56).addSuccess("index", 201).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(5, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(0, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(3, stats.bulkRetries);
        Assert.assertEquals(9, stats.docsRetried);
        Assert.assertEquals(5, stats.docsAccepted);
    }

    /**
     * Case: Mix documents that are retried in place with documents that are modified and retried.
     */
    @Test
    public void testBulk06_RetriesMixedWithNewDocuments() throws Exception {
        testSettings.setProperty(ES_WRITE_REST_ERROR_HANDLERS, "incrementer");
        testSettings.setProperty(((BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLER) + ".incrementer"), BulkProcessorTest.IncrementingHandler.class.getName());
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addFailure("index", 401, "conflict", "This data is bogus").addRejection("index").addRejection("index").addRejection("index").addRejection("index").generate(), generator.setInfo(resource, 56).addFailure("index", 401, "conflict", "This data is bogus").addRejection("index").addRejection("index").addRejection("index").addRejection("index").generate(), generator.setInfo(resource, 56).addSuccess("index", 201).addFailure("index", 401, "conflict", "This data is bogus").addRejection("index").addSuccess("index", 201).addSuccess("index", 201).generate(), generator.setInfo(resource, 56).addSuccess("index", 201).addSuccess("index", 201).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(5, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(0, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(3, stats.bulkRetries);
        Assert.assertEquals(12, stats.docsRetried);
        Assert.assertEquals(5, stats.docsAccepted);
    }

    /**
     * Case: Handler throws random Exceptions
     * Outcome: Processing fails fast.
     */
    public static class ExceptionThrowingHandler extends BulkWriteErrorHandler {
        @Override
        public HandlerResult onError(BulkWriteFailure entry, DelayableErrorCollector<byte[]> collector) throws Exception {
            throw new IllegalArgumentException("Whoopsie!");
        }
    }

    @Test(expected = EsHadoopException.class)
    public void testBulk07_HandlerThrowsExceptions() throws Exception {
        testSettings.setProperty(ES_WRITE_REST_ERROR_HANDLERS, "exception");
        testSettings.setProperty(((BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLER) + ".exception"), BulkProcessorTest.ExceptionThrowingHandler.class.getName());
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addFailure("index", 401, "conflict", "This data is bogus").addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).generate());
        processData(processor);
        processor.tryFlush();
        Assert.fail("Should have thrown exception and aborted handling data.");
    }

    /**
     * Case: Handler throws exception, wrapped in abort based exception
     * Outcome: Exception is collected and used as the reason for aborting that specific document.
     */
    public static class AbortingExceptionThrowingHandler extends BulkWriteErrorHandler {
        @Override
        public HandlerResult onError(BulkWriteFailure entry, DelayableErrorCollector<byte[]> collector) throws Exception {
            throw new EsHadoopAbortHandlerException("Abort the handler!!");
        }
    }

    @Test
    public void testBulk07_HandlerThrowsAbortExceptions() throws Exception {
        testSettings.setProperty(ES_WRITE_REST_ERROR_HANDLERS, "exception");
        testSettings.setProperty(((BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLER) + ".exception"), BulkProcessorTest.AbortingExceptionThrowingHandler.class.getName());
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addFailure("index", 401, "conflict", "This data is bogus").addSuccess("index", 201).addSuccess("index", 201).addRejection("index").addSuccess("index", 201).generate(), generator.setInfo(resource, 56).addRejection("index").generate(), generator.setInfo(resource, 56).addSuccess("index", 201).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(4, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(1, bulkResponse.getDocsAborted());
        Assert.assertEquals("Abort the handler!!", bulkResponse.getDocumentErrors().get(0).getError().getMessage());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(2, stats.bulkRetries);
        Assert.assertEquals(2, stats.docsRetried);
        Assert.assertEquals(4, stats.docsAccepted);
    }

    /**
     * Case: Evil or incorrect handler causes infinite loop.
     */
    public static class NeverSurrenderHandler extends BulkWriteErrorHandler {
        @Override
        public HandlerResult onError(BulkWriteFailure entry, DelayableErrorCollector<byte[]> collector) throws Exception {
            return collector.retry();// NEVER GIVE UP

        }
    }

    @Test(expected = EsHadoopException.class)
    public void testBulk08_HandlerLoopsForever() throws Exception {
        testSettings.setProperty(ES_WRITE_REST_ERROR_HANDLERS, "looper");
        testSettings.setProperty(((BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLER) + ".looper"), BulkProcessorTest.NeverSurrenderHandler.class.getName());
        testSettings.setProperty(ES_BATCH_WRITE_RETRY_LIMIT, "6");
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addRejection("index").addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).generate(), generator.setInfo(resource, 56).addRejection("index").generate(), generator.setInfo(resource, 56).addRejection("index").generate(), generator.setInfo(resource, 56).addRejection("index").generate(), generator.setInfo(resource, 56).addRejection("index").generate(), generator.setInfo(resource, 56).addRejection("index").generate(), generator.setInfo(resource, 56).addRejection("index").generate());
        processData(processor);
        processor.tryFlush();
        Assert.fail("This should fail with too many bulk requests.");
    }

    /**
     * Case: Handler returns data missing a new line, or invalid data all together.
     */
    public static class NewlineDroppingHandler extends BulkWriteErrorHandler {
        @Override
        public HandlerResult onError(BulkWriteFailure entry, DelayableErrorCollector<byte[]> collector) throws Exception {
            byte[] data = BulkProcessorTest.copyDocumentBytes(entry);
            byte[] sansNewline = new byte[(data.length) - 1];
            System.arraycopy(data, 0, sansNewline, 0, sansNewline.length);
            return collector.retry(sansNewline);
        }
    }

    public static class GarbageHandler extends BulkWriteErrorHandler {
        @Override
        public HandlerResult onError(BulkWriteFailure entry, DelayableErrorCollector<byte[]> collector) throws Exception {
            byte[] garbage = "{\"element\": \"A\"}".getBytes();
            return collector.retry(garbage);
        }
    }

    public static class StillGarbageHandler extends BulkWriteErrorHandler {
        @Override
        public HandlerResult onError(BulkWriteFailure entry, DelayableErrorCollector<byte[]> collector) throws Exception {
            byte[] garbage = "{\"element\": \"A\"}\n".getBytes();
            return collector.retry(garbage);
        }
    }

    @Test
    public void testBulk09_HandlerDropsNewline() throws Exception {
        testSettings.setProperty(ES_WRITE_REST_ERROR_HANDLERS, "drop");
        testSettings.setProperty(((BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLER) + ".drop"), BulkProcessorTest.NewlineDroppingHandler.class.getName());
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addFailure("index", 401, "invalid", "some failure").addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).generate(), generator.setInfo(resource, 56).addSuccess("index", 201).generate());
        processData(processor);
        BulkResponse bulkResponse = processor.tryFlush();
        Assert.assertEquals(5, bulkResponse.getDocsSent());
        Assert.assertEquals(0, bulkResponse.getDocsSkipped());
        Assert.assertEquals(0, bulkResponse.getDocsAborted());
        processor.close();
        Stats stats = processor.stats();
        Assert.assertEquals(1, stats.bulkRetries);
        Assert.assertEquals(1, stats.docsRetried);
        Assert.assertEquals(5, stats.docsAccepted);
    }

    @Test(expected = EsHadoopException.class)
    public void testBulk09_HandlerReturnsGarbage() throws Exception {
        testSettings.setProperty(ES_WRITE_REST_ERROR_HANDLERS, "garbage");
        testSettings.setProperty(((BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLER) + ".garbage"), BulkProcessorTest.GarbageHandler.class.getName());
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addFailure("index", 401, "invalid", "some failure").addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).generate());
        processData(processor);
        processor.tryFlush();
        Assert.fail("This should fail since the retry handler returned garbage");
    }

    @Test(expected = EsHadoopException.class)
    public void testBulk09_HandlerStillReturnsGarbage() throws Exception {
        testSettings.setProperty(ES_WRITE_REST_ERROR_HANDLERS, "garbage");
        testSettings.setProperty(((BulkWriteHandlerLoader.ES_WRITE_REST_ERROR_HANDLER) + ".garbage"), BulkProcessorTest.StillGarbageHandler.class.getName());
        BulkProcessor processor = getBulkProcessor(generator.setInfo(resource, 56).addFailure("index", 401, "invalid", "some failure").addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).addSuccess("index", 201).generate());
        processData(processor);
        processor.tryFlush();
        Assert.fail("This should fail since the retry handler returned garbage");
    }
}

