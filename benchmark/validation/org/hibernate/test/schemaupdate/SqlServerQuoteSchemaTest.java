/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS;
import TargetType.DATABASE;
import TargetType.SCRIPT;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12106")
@RequiresDialect(SQLServerDialect.class)
public class SqlServerQuoteSchemaTest extends BaseCoreFunctionalTestCase {
    private File output;

    @Test
    public void test() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(GLOBALLY_QUOTED_IDENTIFIERS, Boolean.TRUE.toString()).build();
        try {
            output.deleteOnExit();
            final MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(SqlServerQuoteSchemaTest.MyEntity.class).buildMetadata()));
            metadata.validate();
            new SchemaUpdate().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setDelimiter(";").setFormat(true).execute(EnumSet.of(DATABASE, SCRIPT), metadata);
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
        try {
            String fileContent = new String(Files.readAllBytes(output.toPath()));
            Pattern fileContentPattern = Pattern.compile("create table \\[my\\-schema\\]\\.\\[my_entity\\]");
            Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
            Assert.assertThat(fileContentMatcher.find(), Is.is(true));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        ssr = new StandardServiceRegistryBuilder().applySetting(GLOBALLY_QUOTED_IDENTIFIERS, Boolean.TRUE.toString()).build();
        try {
            final MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(SqlServerQuoteSchemaTest.MyEntityUpdated.class).buildMetadata()));
            metadata.validate();
            new SchemaUpdate().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setDelimiter(";").setFormat(true).execute(EnumSet.of(DATABASE, SCRIPT), metadata);
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
        try {
            String fileContent = new String(Files.readAllBytes(output.toPath()));
            Pattern fileContentPattern = Pattern.compile("alter table \\[my\\-schema\\]\\.\\[my_entity\\]");
            Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
            Assert.assertThat(fileContentMatcher.find(), Is.is(true));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Entity(name = "MyEntity")
    @Table(name = "my_entity", schema = "my-schema")
    public static class MyEntity {
        @Id
        public Integer id;
    }

    @Entity(name = "MyEntity")
    @Table(name = "my_entity", schema = "my-schema")
    public static class MyEntityUpdated {
        @Id
        public Integer id;

        private String title;
    }
}

