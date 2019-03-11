package liquibase.database.core;


import liquibase.database.AbstractJdbcDatabaseTest;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class H2DatabaseTest extends AbstractJdbcDatabaseTest {
    public H2DatabaseTest() throws Exception {
        super(new H2Database());
    }

    @Override
    @Test
    public void supportsInitiallyDeferrableColumns() {
        Assert.assertFalse(getDatabase().supportsInitiallyDeferrableColumns());
    }

    @Override
    @Test
    public void getCurrentDateTimeFunction() {
        Assert.assertEquals("NOW()", getDatabase().getCurrentDateTimeFunction());
    }

    @Test
    public void testGetDefaultDriver() {
        Database database = getDatabase();
        Assert.assertEquals("org.h2.Driver", database.getDefaultDriver("jdbc:h2:mem:liquibase"));
        Assert.assertNull(database.getDefaultDriver("jdbc:db2://localhost;databaseName=liquibase"));
    }

    @Override
    @Test
    public void escapeTableName_noSchema() {
        Database database = getDatabase();
        Assert.assertEquals("tableName", database.escapeTableName(null, null, "tableName"));
    }

    @Override
    @Test
    public void escapeTableName_withSchema() {
        Database database = getDatabase();
        Assert.assertEquals("schemaName.tableName", database.escapeTableName("catalogName", "schemaName", "tableName"));
    }

    @Test
    public void versionBeforeMinMaxSequenceIntroductionShouldReturnFalse() throws DatabaseException {
        // GIVEN
        DatabaseConnection mockedConnection = Mockito.mock(DatabaseConnection.class);
        Mockito.when(mockedConnection.getDatabaseMajorVersion()).thenReturn(1);
        Mockito.when(mockedConnection.getDatabaseMinorVersion()).thenReturn(3);
        Mockito.when(mockedConnection.getDatabaseProductVersion()).thenReturn("1.3.174 (2013-10-19)");
        H2Database cut = ((H2Database) (getDatabase()));
        cut.setConnection(mockedConnection);
        // WHEN
        boolean result = cut.supportsMinMaxForSequences();
        // THEN
        Assert.assertFalse("Version 1.3.174 should not report minMaxSequence support", result);
    }

    @Test
    public void oldVersionShouldReturnFalse() throws DatabaseException {
        // GIVEN
        DatabaseConnection mockedConnection = Mockito.mock(DatabaseConnection.class);
        Mockito.when(mockedConnection.getDatabaseMajorVersion()).thenReturn(1);
        Mockito.when(mockedConnection.getDatabaseMinorVersion()).thenReturn(2);
        Mockito.when(mockedConnection.getDatabaseProductVersion()).thenReturn("1.2.001 (2010-12-31)");
        H2Database cut = ((H2Database) (getDatabase()));
        cut.setConnection(mockedConnection);
        // WHEN
        boolean result = cut.supportsMinMaxForSequences();
        // THEN
        Assert.assertFalse("Version 1.2.001 should not report minMaxSequence support", result);
    }

    @Test
    public void versionAfterMinMaxSequenceIntroductionShouldReturnTrue() throws DatabaseException {
        // GIVEN
        DatabaseConnection mockedConnection = Mockito.mock(DatabaseConnection.class);
        Mockito.when(mockedConnection.getDatabaseMajorVersion()).thenReturn(1);
        Mockito.when(mockedConnection.getDatabaseMinorVersion()).thenReturn(3);
        Mockito.when(mockedConnection.getDatabaseProductVersion()).thenReturn("1.3.175 (2014-01-18)");
        H2Database cut = ((H2Database) (getDatabase()));
        cut.setConnection(mockedConnection);
        // WHEN
        boolean result = cut.supportsMinMaxForSequences();
        // THEN
        Assert.assertTrue("Version 1.3.175 should not report minMaxSequence support", result);
    }

    @Test
    public void newerMinorVersionShouldReturnTrue() throws DatabaseException {
        // GIVEN
        DatabaseConnection mockedConnection = Mockito.mock(DatabaseConnection.class);
        Mockito.when(mockedConnection.getDatabaseMajorVersion()).thenReturn(1);
        Mockito.when(mockedConnection.getDatabaseMinorVersion()).thenReturn(4);
        Mockito.when(mockedConnection.getDatabaseProductVersion()).thenReturn("1.4.100 (2014-08-18)");
        H2Database cut = ((H2Database) (getDatabase()));
        cut.setConnection(mockedConnection);
        // WHEN
        boolean result = cut.supportsMinMaxForSequences();
        // THEN
        Assert.assertTrue("Version 1.4.100 should not report minMaxSequence support", result);
    }

    @Test
    public void newerMajorVersionShouldReturnTrue() throws DatabaseException {
        // GIVEN
        DatabaseConnection mockedConnection = Mockito.mock(DatabaseConnection.class);
        Mockito.when(mockedConnection.getDatabaseMajorVersion()).thenReturn(2);
        Mockito.when(mockedConnection.getDatabaseMinorVersion()).thenReturn(1);
        Mockito.when(mockedConnection.getDatabaseProductVersion()).thenReturn("2.1.100 (2017-08-18)");
        H2Database cut = ((H2Database) (getDatabase()));
        cut.setConnection(mockedConnection);
        // WHEN
        boolean result = cut.supportsMinMaxForSequences();
        // THEN
        Assert.assertTrue("Version 2.1..100 should not report minMaxSequence support", result);
    }
}

