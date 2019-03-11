package liquibase.database.core;


import junit.framework.TestCase;
import liquibase.database.Database;


public class DerbyDatabaseTest extends TestCase {
    public void testGetDefaultDriver() {
        Database database = new DerbyDatabase();
        TestCase.assertEquals("org.apache.derby.jdbc.EmbeddedDriver", database.getDefaultDriver("java:derby:liquibase;create=true"));
        TestCase.assertNull(database.getDefaultDriver("jdbc:oracle://localhost;databaseName=liquibase"));
    }

    public void testGetDateLiteral() {
        TestCase.assertEquals("TIMESTAMP('2008-01-25 13:57:41')", new DerbyDatabase().getDateLiteral("2008-01-25 13:57:41"));
        TestCase.assertEquals("TIMESTAMP('2008-01-25 13:57:41.300000')", new DerbyDatabase().getDateLiteral("2008-01-25 13:57:41.3"));
        TestCase.assertEquals("TIMESTAMP('2008-01-25 13:57:41.340000')", new DerbyDatabase().getDateLiteral("2008-01-25 13:57:41.34"));
        TestCase.assertEquals("TIMESTAMP('2008-01-25 13:57:41.347000')", new DerbyDatabase().getDateLiteral("2008-01-25 13:57:41.347"));
    }
}

