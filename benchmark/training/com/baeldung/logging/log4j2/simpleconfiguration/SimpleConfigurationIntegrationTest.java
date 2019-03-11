/**
 * This class invokes the configuration factory through the run time property,
 * as defined in section 4.2 of the "Programmatic Configuration with Log4j 2"
 */
package com.baeldung.logging.log4j2.simpleconfiguration;


import com.baeldung.logging.log4j2.Log4j2BaseIntegrationTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.junit.Test;


public class SimpleConfigurationIntegrationTest extends Log4j2BaseIntegrationTest {
    @Test
    public void givenSimpleConfigurationPlugin_whenUsingFlowMarkers_thenLogsCorrectly() throws Exception {
        Logger logger = LogManager.getLogger(this.getClass());
        Marker markerContent = MarkerManager.getMarker("FLOW");
        logger.debug(markerContent, "Debug log message");
        logger.info(markerContent, "Info log message");
        logger.error(markerContent, "Error log message");
    }
}

