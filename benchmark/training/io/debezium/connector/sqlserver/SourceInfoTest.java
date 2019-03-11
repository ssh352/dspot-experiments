/**
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.sqlserver;


import SourceInfo.DEBEZIUM_CONNECTOR_KEY;
import SourceInfo.DEBEZIUM_VERSION_KEY;
import org.junit.Test;


public class SourceInfoTest {
    private SourceInfo source;

    @Test
    public void versionIsPresent() {
        assertThat(source.struct().getString(DEBEZIUM_VERSION_KEY)).isEqualTo(Module.version());
    }

    @Test
    public void connectorIsPresent() {
        assertThat(source.struct().getString(DEBEZIUM_CONNECTOR_KEY)).isEqualTo(Module.name());
    }
}

