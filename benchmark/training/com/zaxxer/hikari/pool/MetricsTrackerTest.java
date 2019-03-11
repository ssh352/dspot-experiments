package com.zaxxer.hikari.pool;


import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.mocks.StubDataSource;
import java.sql.Connection;
import java.sql.SQLTransientConnectionException;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author wvuong@chariotsolutions.com on 2/16/17.
 */
public class MetricsTrackerTest {
    @Test(expected = SQLTransientConnectionException.class)
    public void connectionTimeoutIsRecorded() throws Exception {
        int timeoutMillis = 1000;
        int timeToCreateNewConnectionMillis = timeoutMillis * 2;
        StubDataSource stubDataSource = new StubDataSource();
        stubDataSource.setConnectionAcquistionTime(timeToCreateNewConnectionMillis);
        MetricsTrackerTest.StubMetricsTracker metricsTracker = new MetricsTrackerTest.StubMetricsTracker();
        try (HikariDataSource ds = TestElf.newHikariDataSource()) {
            ds.setMinimumIdle(0);
            ds.setMaximumPoolSize(1);
            ds.setConnectionTimeout(timeoutMillis);
            ds.setDataSource(stubDataSource);
            ds.setMetricsTrackerFactory(( poolName, poolStats) -> metricsTracker);
            try (Connection c = ds.getConnection()) {
                TestCase.fail("Connection shouldn't have been successfully created due to configured connection timeout");
            } finally {
                // assert that connection timeout was measured
                Assert.assertThat(metricsTracker.connectionTimeoutRecorded, CoreMatchers.is(true));
                // assert that measured time to acquire connection should be roughly equal or greater than the configured connection timeout time
                Assert.assertTrue(((metricsTracker.connectionAcquiredNanos) >= (TimeUnit.NANOSECONDS.convert(timeoutMillis, TimeUnit.MILLISECONDS))));
            }
        }
    }

    private static class StubMetricsTracker implements IMetricsTracker {
        private Long connectionCreatedMillis;

        private Long connectionAcquiredNanos;

        private Long connectionBorrowedMillis;

        private boolean connectionTimeoutRecorded;

        @Override
        public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
            this.connectionCreatedMillis = connectionCreatedMillis;
        }

        @Override
        public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
            this.connectionAcquiredNanos = elapsedAcquiredNanos;
        }

        @Override
        public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
            this.connectionBorrowedMillis = elapsedBorrowedMillis;
        }

        @Override
        public void recordConnectionTimeout() {
            this.connectionTimeoutRecorded = true;
        }
    }
}

