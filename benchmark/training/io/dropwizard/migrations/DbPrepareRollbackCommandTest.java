package io.dropwizard.migrations;


import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collections;
import net.jcip.annotations.NotThreadSafe;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.jupiter.api.Test;


@NotThreadSafe
public class DbPrepareRollbackCommandTest extends AbstractMigrationTest {
    private final DbPrepareRollbackCommand<TestMigrationConfiguration> prepareRollbackCommand = new DbPrepareRollbackCommand(new TestMigrationDatabaseConfiguration(), TestMigrationConfiguration.class, "migrations-ddl.xml");

    private TestMigrationConfiguration conf;

    @Test
    public void testRun() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        prepareRollbackCommand.setOutputStream(new PrintStream(baos));
        prepareRollbackCommand.run(null, new Namespace(Collections.emptyMap()), conf);
        assertThat(baos.toString(AbstractMigrationTest.UTF_8)).contains("ALTER TABLE PUBLIC.persons DROP COLUMN email;").contains("DROP TABLE PUBLIC.persons;");
    }

    @Test
    public void testPrepareOnlyChange() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        prepareRollbackCommand.setOutputStream(new PrintStream(baos));
        prepareRollbackCommand.run(null, new Namespace(Collections.singletonMap("count", 1)), conf);
        assertThat(baos.toString(AbstractMigrationTest.UTF_8)).contains("DROP TABLE PUBLIC.persons;");
    }

    @Test
    public void testPrintHelp() throws Exception {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        AbstractMigrationTest.createSubparser(prepareRollbackCommand).printHelp(new PrintWriter(new OutputStreamWriter(out, AbstractMigrationTest.UTF_8), true));
        assertThat(out.toString(AbstractMigrationTest.UTF_8)).isEqualTo(String.format(("usage: db prepare-rollback [-h] [--migrations MIGRATIONS-FILE]%n" + ((((((((((((((((((((("          [--catalog CATALOG] [--schema SCHEMA] [-c COUNT] [-i CONTEXTS]%n" + "          [file]%n") + "%n") + "Generate rollback DDL scripts for pending change sets.%n") + "%n") + "positional arguments:%n") + "  file                   application configuration file%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n") + "  --migrations MIGRATIONS-FILE%n") + "                         the file containing  the  Liquibase migrations for%n") + "                         the application%n") + "  --catalog CATALOG      Specify  the   database   catalog   (use  database%n") + "                         default if omitted)%n") + "  --schema SCHEMA        Specify the database schema  (use database default%n") + "                         if omitted)%n") + "  -c COUNT, --count COUNT%n") + "                         limit script to  the  specified  number of pending%n") + "                         change sets%n") + "  -i CONTEXTS, --include CONTEXTS%n") + "                         include change sets from the given context%n"))));
    }
}

