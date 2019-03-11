/**
 * Copyright (c) 2010-2019. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventhandling.tokenstore.jdbc;


import java.time.Clock;
import java.time.Duration;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.GlobalSequenceTrackingToken;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventhandling.tokenstore.AbstractTokenEntry;
import org.axonframework.eventhandling.tokenstore.UnableToClaimTokenException;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class JdbcTokenStoreTest {
    @Inject
    private DataSource dataSource;

    @Inject
    @Named("tokenStore")
    private JdbcTokenStore tokenStore;

    @Inject
    @Named("concurrentTokenStore")
    private JdbcTokenStore concurrentTokenStore;

    @Inject
    @Named("stealingTokenStore")
    private JdbcTokenStore stealingTokenStore;

    @Inject
    private TransactionManager transactionManager;

    @Test
    public void testClaimAndUpdateToken() {
        transactionManager.executeInTransaction(() -> tokenStore.initializeTokenSegments("test", 1));
        transactionManager.executeInTransaction(() -> assertNull(tokenStore.fetchToken("test", 0)));
        TrackingToken token = new GlobalSequenceTrackingToken(1L);
        transactionManager.executeInTransaction(() -> tokenStore.storeToken(token, "test", 0));
        transactionManager.executeInTransaction(() -> assertEquals(token, tokenStore.fetchToken("test", 0)));
    }

    @Transactional
    @Test
    public void testInitializeTokens() {
        tokenStore.initializeTokenSegments("test1", 7);
        int[] actual = tokenStore.fetchSegments("test1");
        Arrays.sort(actual);
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3, 4, 5, 6 }, actual);
    }

    @SuppressWarnings("Duplicates")
    @Transactional
    @Test
    public void testInitializeTokensAtGivenPosition() {
        tokenStore.initializeTokenSegments("test1", 7, new GlobalSequenceTrackingToken(10));
        int[] actual = tokenStore.fetchSegments("test1");
        Arrays.sort(actual);
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3, 4, 5, 6 }, actual);
        for (int segment : actual) {
            Assert.assertEquals(new GlobalSequenceTrackingToken(10), tokenStore.fetchToken("test1", segment));
        }
    }

    @Transactional
    @Test(expected = UnableToClaimTokenException.class)
    public void testInitializeTokensWhileAlreadyPresent() {
        tokenStore.fetchToken("test1", 1);
        tokenStore.initializeTokenSegments("test1", 7);
    }

    @Transactional
    @Test
    public void testQuerySegments() {
        transactionManager.executeInTransaction(() -> {
            tokenStore.initializeTokenSegments("test", 1);
            tokenStore.initializeTokenSegments("proc1", 2);
            tokenStore.initializeTokenSegments("proc2", 2);
        });
        transactionManager.executeInTransaction(() -> assertNull(tokenStore.fetchToken("test", 0)));
        transactionManager.executeInTransaction(() -> tokenStore.storeToken(new GlobalSequenceTrackingToken(1L), "proc1", 0));
        transactionManager.executeInTransaction(() -> tokenStore.storeToken(new GlobalSequenceTrackingToken(2L), "proc1", 1));
        transactionManager.executeInTransaction(() -> tokenStore.storeToken(new GlobalSequenceTrackingToken(2L), "proc2", 1));
        transactionManager.executeInTransaction(() -> {
            final int[] segments = tokenStore.fetchSegments("proc1");
            Assert.assertThat(segments.length, is(2));
        });
        transactionManager.executeInTransaction(() -> {
            final int[] segments = tokenStore.fetchSegments("proc2");
            Assert.assertThat(segments.length, is(2));
        });
        transactionManager.executeInTransaction(() -> {
            final int[] segments = tokenStore.fetchSegments("proc3");
            Assert.assertThat(segments.length, is(0));
        });
    }

    @Test
    public void testClaimAndUpdateTokenWithoutTransaction() {
        transactionManager.executeInTransaction(() -> tokenStore.initializeTokenSegments("test", 1));
        Assert.assertNull(tokenStore.fetchToken("test", 0));
        TrackingToken token = new GlobalSequenceTrackingToken(1L);
        tokenStore.storeToken(token, "test", 0);
        Assert.assertEquals(token, tokenStore.fetchToken("test", 0));
    }

    @Test
    public void testClaimTokenConcurrently() {
        transactionManager.executeInTransaction(() -> tokenStore.initializeTokenSegments("concurrent", 1));
        transactionManager.executeInTransaction(() -> assertNull(tokenStore.fetchToken("concurrent", 0)));
        try {
            transactionManager.executeInTransaction(() -> concurrentTokenStore.fetchToken("concurrent", 0));
            Assert.fail("Expected UnableToClaimTokenException");
        } catch (UnableToClaimTokenException e) {
            // expected
        }
    }

    @Test
    public void testClaimTokenConcurrentlyAfterRelease() {
        transactionManager.executeInTransaction(() -> tokenStore.initializeTokenSegments("concurrent", 1));
        transactionManager.executeInTransaction(() -> tokenStore.fetchToken("concurrent", 0));
        transactionManager.executeInTransaction(() -> tokenStore.releaseClaim("concurrent", 0));
        transactionManager.executeInTransaction(() -> assertNull(concurrentTokenStore.fetchToken("concurrent", 0)));
    }

    @Test
    public void testClaimTokenConcurrentlyAfterTimeLimit() {
        transactionManager.executeInTransaction(() -> tokenStore.initializeTokenSegments("concurrent", 1));
        transactionManager.executeInTransaction(() -> tokenStore.fetchToken("concurrent", 0));
        AbstractTokenEntry.clock = Clock.offset(Clock.systemUTC(), Duration.ofHours(1));
        transactionManager.executeInTransaction(() -> assertNull(concurrentTokenStore.fetchToken("concurrent", 0)));
    }

    @Test
    public void testStealToken() {
        transactionManager.executeInTransaction(() -> tokenStore.initializeTokenSegments("stealing", 1));
        transactionManager.executeInTransaction(() -> assertNull(tokenStore.fetchToken("stealing", 0)));
        transactionManager.executeInTransaction(() -> assertNull(stealingTokenStore.fetchToken("stealing", 0)));
        try {
            transactionManager.executeInTransaction(() -> tokenStore.storeToken(new GlobalSequenceTrackingToken(0), "stealing", 0));
            Assert.fail("Expected UnableToClaimTokenException");
        } catch (UnableToClaimTokenException e) {
            // expected
        }
        transactionManager.executeInTransaction(() -> tokenStore.releaseClaim("stealing", 0));
        // claim should still be on stealingTokenStore:
        transactionManager.executeInTransaction(() -> stealingTokenStore.storeToken(new GlobalSequenceTrackingToken(1), "stealing", 0));
    }

    @Test
    public void testStoreAndLoadAcrossTransactions() {
        transactionManager.executeInTransaction(() -> tokenStore.initializeTokenSegments("multi", 1));
        transactionManager.executeInTransaction(() -> {
            tokenStore.fetchToken("multi", 0);
            tokenStore.storeToken(new GlobalSequenceTrackingToken(1), "multi", 0);
        });
        transactionManager.executeInTransaction(() -> {
            TrackingToken actual = tokenStore.fetchToken("multi", 0);
            assertEquals(new GlobalSequenceTrackingToken(1), actual);
            tokenStore.storeToken(new GlobalSequenceTrackingToken(2), "multi", 0);
        });
        transactionManager.executeInTransaction(() -> {
            TrackingToken actual = tokenStore.fetchToken("multi", 0);
            assertEquals(new GlobalSequenceTrackingToken(2), actual);
        });
    }

    @Test
    public void testClaimAndDeleteToken() {
        transactionManager.executeInTransaction(() -> tokenStore.initializeTokenSegments("test1", 2));
        tokenStore.fetchToken("test1", 0);
        tokenStore.fetchToken("test1", 1);
        tokenStore.deleteToken("test1", 1);
        Assert.assertArrayEquals(new int[]{ 0 }, tokenStore.fetchSegments("test1"));
    }

    @Test(expected = UnableToClaimTokenException.class)
    public void testDeleteUnclaimedTokenFails() {
        tokenStore.fetchToken("test1", 1);
        tokenStore.releaseClaim("test1", 1);
        tokenStore.deleteToken("test1", 1);
    }

    @Transactional
    @Test(expected = UnableToClaimTokenException.class)
    public void testDeleteTokenFailsWhenClaimedByOtherNode() {
        concurrentTokenStore.fetchToken("test1", 1);
        tokenStore.deleteToken("test1", 1);
    }

    @Configuration
    public static class Context {
        @SuppressWarnings("Duplicates")
        @Bean
        public DataSource dataSource() {
            JDBCDataSource dataSource = new JDBCDataSource();
            dataSource.setUrl("jdbc:hsqldb:mem:testdb");
            dataSource.setUser("sa");
            dataSource.setPassword("");
            return new org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy(dataSource);
        }

        @Bean
        public PlatformTransactionManager txManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        public JdbcTokenStore tokenStore(DataSource dataSource) {
            return JdbcTokenStore.builder().connectionProvider(dataSource::getConnection).serializer(XStreamSerializer.builder().build()).build();
        }

        @Bean
        public JdbcTokenStore concurrentTokenStore(DataSource dataSource) {
            return JdbcTokenStore.builder().connectionProvider(dataSource::getConnection).serializer(XStreamSerializer.builder().build()).claimTimeout(Duration.ofSeconds(2)).nodeId("concurrent").build();
        }

        @Bean
        public JdbcTokenStore stealingTokenStore(DataSource dataSource) {
            return JdbcTokenStore.builder().connectionProvider(dataSource::getConnection).serializer(XStreamSerializer.builder().build()).claimTimeout(Duration.ofSeconds((-1))).nodeId("stealing").build();
        }

        @Bean
        public TransactionManager transactionManager(PlatformTransactionManager txManager) {
            // noinspection Duplicates
            return () -> {
                TransactionStatus transaction = txManager.getTransaction(new DefaultTransactionDefinition());
                return new Transaction() {
                    @Override
                    public void commit() {
                        txManager.commit(transaction);
                    }

                    @Override
                    public void rollback() {
                        txManager.rollback(transaction);
                    }
                };
            };
        }
    }
}

