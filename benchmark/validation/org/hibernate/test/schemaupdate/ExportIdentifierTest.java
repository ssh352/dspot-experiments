/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import DialectChecks.SupportsSequences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


@RequiresDialectFeature(SupportsSequences.class)
public class ExportIdentifierTest extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12935")
    public void testUniqueExportableIdentifier() {
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        final MetadataBuildingOptions options = new org.hibernate.boot.internal.MetadataBuilderImpl.MetadataBuildingOptionsImpl(ssr);
        final Database database = new Database(options);
        database.locateNamespace(null, null);
        database.locateNamespace(Identifier.toIdentifier("catalog1"), null);
        database.locateNamespace(Identifier.toIdentifier("catalog2"), null);
        database.locateNamespace(null, Identifier.toIdentifier("schema1"));
        database.locateNamespace(null, Identifier.toIdentifier("schema2"));
        database.locateNamespace(Identifier.toIdentifier("catalog_both_1"), Identifier.toIdentifier("schema_both_1"));
        database.locateNamespace(Identifier.toIdentifier("catalog_both_2"), Identifier.toIdentifier("schema_both_2"));
        final List<String> exportIdentifierList = new ArrayList<>();
        final Set<String> exportIdentifierSet = new HashSet<>();
        try {
            addTables("aTable", database.getNamespaces(), exportIdentifierList, exportIdentifierSet);
            addSimpleAuxiliaryDatabaseObject(database.getNamespaces(), exportIdentifierList, exportIdentifierSet);
            addNamedAuxiliaryDatabaseObjects("aNamedAuxiliaryDatabaseObject", database.getNamespaces(), exportIdentifierList, exportIdentifierSet);
            addSequences("aSequence", database.getNamespaces(), exportIdentifierList, exportIdentifierSet);
            Assert.assertEquals(exportIdentifierList.size(), exportIdentifierSet.size());
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

