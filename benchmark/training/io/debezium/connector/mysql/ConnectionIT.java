/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;


import io.debezium.util.Testing;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Test;


public class ConnectionIT implements Testing {
    @Test
    public void shouldDoStuffWithDatabase() throws SQLException {
        final UniqueDatabase DATABASE = new UniqueDatabase("readbinlog", "readbinlog_test");
        DATABASE.createAndInitialize();
        try (MySQLConnection conn = MySQLConnection.forTestDatabase(DATABASE.getDatabaseName())) {
            connect();
            // Set up the table as one transaction and wait to see the events ...
            conn.execute("DROP TABLE IF EXISTS person", ("CREATE TABLE person (" + ((((("  name VARCHAR(255) primary key," + "  birthdate DATE NULL,") + "  age INTEGER NULL DEFAULT 10,") + "  salary DECIMAL(5,2),") + "  bitStr BIT(18)") + ")")));
            execute("SELECT * FROM person");
            try (ResultSet rs = connection().getMetaData().getColumns("readbinlog_test", null, null, null)) {
                // if ( Testing.Print.isEnabled() ) conn.print(rs);
            }
        }
    }
}

