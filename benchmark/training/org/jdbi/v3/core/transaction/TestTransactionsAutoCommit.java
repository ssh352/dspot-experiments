/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.transaction;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class TestTransactionsAutoCommit {
    public static final String SAMPLE_SQL = "insert into something (id, name) values (?, ?)";

    @Test
    public void restoreAutoCommitInitialStateOnUnexpectedError() throws Exception {
        final Connection connection = Mockito.mock(Connection.class);
        final PreparedStatement statement = Mockito.mock(PreparedStatement.class);
        InOrder inOrder = Mockito.inOrder(connection, statement);
        Handle h = Jdbi.create(() -> connection).open();
        Mockito.when(connection.getAutoCommit()).thenReturn(true);
        Mockito.when(connection.prepareStatement(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(statement);
        Mockito.when(statement.execute()).thenReturn(true);
        Mockito.when(statement.getUpdateCount()).thenReturn(1);
        // throw e.g some underlying database error
        Mockito.doThrow(new SQLException("infrastructure error")).when(connection).commit();
        h.begin();
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> {
            h.execute(SAMPLE_SQL, 1L, "Tom");
            // throws exception on commit
            h.commit();
        });
        // expected behaviour chain:
        // 1. store initial auto-commit state
        inOrder.verify(connection, Mockito.atLeastOnce()).getAutoCommit();
        // 2. turn off auto-commit
        inOrder.verify(connection).setAutoCommit(false);
        // 3. execute statement (without commit)
        inOrder.verify(connection).prepareStatement("insert into something (id, name) values (?, ?)", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        inOrder.verify(statement).execute();
        inOrder.verify(statement).getUpdateCount();
        // 4. commit transaction
        inOrder.verify(connection).commit();
        // 5. set auto-commit back to initial state
        inOrder.verify(connection).setAutoCommit(true);
    }
}
