/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import Environment.HBM2DDL_AUTO;
import TargetType.SCRIPT;
import java.io.File;
import java.nio.file.Files;
import java.util.EnumSet;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(H2Dialect.class)
@TestForIssue(jiraKey = "HHH-8805")
public class SchemaUpdateJoinColumnNoConstraintTest extends BaseUnitTestCase {
    private static final String DELIMITER = ";";

    @Test
    public void test() throws Exception {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(HBM2DDL_AUTO, "none").build();
        try {
            File output = File.createTempFile("update_script", ".sql");
            output.deleteOnExit();
            final MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(SchemaUpdateJoinColumnNoConstraintTest.Child.class).buildMetadata()));
            metadata.validate();
            new SchemaUpdate().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setDelimiter(SchemaUpdateJoinColumnNoConstraintTest.DELIMITER).setFormat(true).execute(EnumSet.of(SCRIPT), metadata);
            String outputContent = new String(Files.readAllBytes(output.toPath()));
            Assert.assertFalse(outputContent.toLowerCase().contains("foreign key"));
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Entity(name = "Parent")
    public static class Parent {
        @Id
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        private Long id;

        @ManyToOne
        @JoinColumn(name = "some_fk", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
        private SchemaUpdateJoinColumnNoConstraintTest.Parent parent;
    }
}

