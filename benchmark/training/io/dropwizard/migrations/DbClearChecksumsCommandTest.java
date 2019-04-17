package io.dropwizard.migrations;


import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import liquibase.Liquibase;
import net.jcip.annotations.NotThreadSafe;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


@NotThreadSafe
public class DbClearChecksumsCommandTest extends AbstractMigrationTest {
    private DbClearChecksumsCommand<TestMigrationConfiguration> clearChecksums = new DbClearChecksumsCommand(TestMigrationConfiguration::getDataSource, TestMigrationConfiguration.class, "migrations.xml");

    @Test
    public void testRun() throws Exception {
        final Liquibase liquibase = Mockito.mock(Liquibase.class);
        clearChecksums.run(new Namespace(Collections.emptyMap()), liquibase);
        Mockito.verify(liquibase).clearCheckSums();
    }

    @Test
    public void testHelpPage() throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        AbstractMigrationTest.createSubparser(clearChecksums).printHelp(new PrintWriter(new OutputStreamWriter(out, AbstractMigrationTest.UTF_8), true));
        assertThat(out.toString(AbstractMigrationTest.UTF_8)).isEqualTo(String.format(("usage: db clear-checksums [-h] [--migrations MIGRATIONS-FILE]%n" + ((((((((((((((("          [--catalog CATALOG] [--schema SCHEMA] [file]%n" + "%n") + "Removes all saved checksums from the database log%n") + "%n") + "positional arguments:%n") + "  file                   application configuration file%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n") + "  --migrations MIGRATIONS-FILE%n") + "                         the file containing  the  Liquibase migrations for%n") + "                         the application%n") + "  --catalog CATALOG      Specify  the   database   catalog   (use  database%n") + "                         default if omitted)%n") + "  --schema SCHEMA        Specify the database schema  (use database default%n") + "                         if omitted)%n"))));
    }
}
