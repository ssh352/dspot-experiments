package com.ctrip.framework.apollo.tracer;


import com.ctrip.framework.apollo.tracer.internals.NullTransaction;
import com.ctrip.framework.apollo.tracer.spi.MessageProducer;
import com.ctrip.framework.apollo.tracer.spi.Transaction;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class TracerTest {
    private MessageProducer someProducer;

    @Test
    public void testLogError() throws Exception {
        String someMessage = "someMessage";
        Throwable someCause = Mockito.mock(Throwable.class);
        Tracer.logError(someMessage, someCause);
        Mockito.verify(someProducer, Mockito.times(1)).logError(someMessage, someCause);
    }

    @Test
    public void testLogErrorWithException() throws Exception {
        String someMessage = "someMessage";
        Throwable someCause = Mockito.mock(Throwable.class);
        Mockito.doThrow(RuntimeException.class).when(someProducer).logError(someMessage, someCause);
        Tracer.logError(someMessage, someCause);
        Mockito.verify(someProducer, Mockito.times(1)).logError(someMessage, someCause);
    }

    @Test
    public void testLogErrorWithOnlyCause() throws Exception {
        Throwable someCause = Mockito.mock(Throwable.class);
        Tracer.logError(someCause);
        Mockito.verify(someProducer, Mockito.times(1)).logError(someCause);
    }

    @Test
    public void testLogErrorWithOnlyCauseWithException() throws Exception {
        Throwable someCause = Mockito.mock(Throwable.class);
        Mockito.doThrow(RuntimeException.class).when(someProducer).logError(someCause);
        Tracer.logError(someCause);
        Mockito.verify(someProducer, Mockito.times(1)).logError(someCause);
    }

    @Test
    public void testLogEvent() throws Exception {
        String someType = "someType";
        String someName = "someName";
        Tracer.logEvent(someType, someName);
        Mockito.verify(someProducer, Mockito.times(1)).logEvent(someType, someName);
    }

    @Test
    public void testLogEventWithException() throws Exception {
        String someType = "someType";
        String someName = "someName";
        Mockito.doThrow(RuntimeException.class).when(someProducer).logEvent(someType, someName);
        Tracer.logEvent(someType, someName);
        Mockito.verify(someProducer, Mockito.times(1)).logEvent(someType, someName);
    }

    @Test
    public void testLogEventWithStatusAndNameValuePairs() throws Exception {
        String someType = "someType";
        String someName = "someName";
        String someStatus = "someStatus";
        String someNameValuePairs = "someNameValuePairs";
        Tracer.logEvent(someType, someName, someStatus, someNameValuePairs);
        Mockito.verify(someProducer, Mockito.times(1)).logEvent(someType, someName, someStatus, someNameValuePairs);
    }

    @Test
    public void testLogEventWithStatusAndNameValuePairsWithException() throws Exception {
        String someType = "someType";
        String someName = "someName";
        String someStatus = "someStatus";
        String someNameValuePairs = "someNameValuePairs";
        Mockito.doThrow(RuntimeException.class).when(someProducer).logEvent(someType, someName, someStatus, someNameValuePairs);
        Tracer.logEvent(someType, someName, someStatus, someNameValuePairs);
        Mockito.verify(someProducer, Mockito.times(1)).logEvent(someType, someName, someStatus, someNameValuePairs);
    }

    @Test
    public void testNewTransaction() throws Exception {
        String someType = "someType";
        String someName = "someName";
        Transaction someTransaction = Mockito.mock(Transaction.class);
        Mockito.when(someProducer.newTransaction(someType, someName)).thenReturn(someTransaction);
        Transaction result = Tracer.newTransaction(someType, someName);
        Mockito.verify(someProducer, Mockito.times(1)).newTransaction(someType, someName);
        Assert.assertEquals(someTransaction, result);
    }

    @Test
    public void testNewTransactionWithException() throws Exception {
        String someType = "someType";
        String someName = "someName";
        Mockito.when(someProducer.newTransaction(someType, someName)).thenThrow(RuntimeException.class);
        Transaction result = Tracer.newTransaction(someType, someName);
        Mockito.verify(someProducer, Mockito.times(1)).newTransaction(someType, someName);
        Assert.assertTrue((result instanceof NullTransaction));
    }
}

