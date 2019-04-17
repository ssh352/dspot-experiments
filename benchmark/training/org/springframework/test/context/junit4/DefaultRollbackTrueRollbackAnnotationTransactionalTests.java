/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.test.context.junit4;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.transaction.TransactionTestUtils;
import org.springframework.transaction.annotation.Transactional;


/**
 * Integration test which verifies proper transactional behavior when the default
 * rollback flag is explicitly set to {@code true} via {@link Rollback @Rollback}.
 *
 * <p>Also tests configuration of the transaction manager qualifier configured
 * via {@link Transactional @Transactional}.
 *
 * @author Sam Brannen
 * @since 4.2
 * @see Rollback
 * @see Transactional#transactionManager
 * @see DefaultRollbackTrueTransactionalTests
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EmbeddedPersonDatabaseTestsConfig.class, inheritLocations = false)
@Transactional("txMgr")
@Rollback(true)
public class DefaultRollbackTrueRollbackAnnotationTransactionalTests extends AbstractTransactionalSpringRunnerTests {
    private static int originalNumRows;

    private static JdbcTemplate jdbcTemplate;

    @Test(timeout = 1000)
    public void modifyTestDataWithinTransaction() {
        TransactionTestUtils.assertInTransaction(true);
        Assert.assertEquals("Adding jane", 1, AbstractTransactionalSpringRunnerTests.addPerson(DefaultRollbackTrueRollbackAnnotationTransactionalTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.JANE));
        Assert.assertEquals("Adding sue", 1, AbstractTransactionalSpringRunnerTests.addPerson(DefaultRollbackTrueRollbackAnnotationTransactionalTests.jdbcTemplate, AbstractTransactionalSpringRunnerTests.SUE));
        Assert.assertEquals("Verifying the number of rows in the person table within a transaction.", 3, AbstractTransactionalSpringRunnerTests.countRowsInPersonTable(DefaultRollbackTrueRollbackAnnotationTransactionalTests.jdbcTemplate));
    }
}
