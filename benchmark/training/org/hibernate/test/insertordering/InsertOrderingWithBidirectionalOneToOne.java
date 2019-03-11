/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.insertordering;


import DialectChecks.SupportsJdbcDriverProxying;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.test.util.jdbc.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.mockito.Mockito;

import static CascadeType.PERSIST;
import static GenerationType.SEQUENCE;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-9864")
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class InsertOrderingWithBidirectionalOneToOne extends BaseNonConfigCoreFunctionalTestCase {
    private PreparedStatementSpyConnectionProvider connectionProvider = new PreparedStatementSpyConnectionProvider(true, false);

    @Test
    public void testBatching() throws SQLException {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.insertordering.Person worker = new org.hibernate.test.insertordering.Person();
            org.hibernate.test.insertordering.Person homestay = new org.hibernate.test.insertordering.Person();
            org.hibernate.test.insertordering.Address home = new org.hibernate.test.insertordering.Address();
            org.hibernate.test.insertordering.Address office = new org.hibernate.test.insertordering.Address();
            home.addPerson(homestay);
            office.addPerson(worker);
            session.persist(home);
            session.persist(office);
            connectionProvider.clear();
        });
        PreparedStatement addressPreparedStatement = connectionProvider.getPreparedStatement("insert into Address (ID) values (?)");
        Mockito.verify(addressPreparedStatement, Mockito.times(2)).addBatch();
        Mockito.verify(addressPreparedStatement, Mockito.times(1)).executeBatch();
        PreparedStatement personPreparedStatement = connectionProvider.getPreparedStatement("insert into Person (address_ID, ID) values (?, ?)");
        Mockito.verify(personPreparedStatement, Mockito.times(2)).addBatch();
        Mockito.verify(personPreparedStatement, Mockito.times(1)).executeBatch();
    }

    @Entity(name = "Address")
    public static class Address {
        @Id
        @Column(name = "ID", nullable = false)
        @SequenceGenerator(name = "ID", sequenceName = "ADDRESS_SEQ")
        @GeneratedValue(strategy = SEQUENCE, generator = "ID")
        private Long id;

        @OneToOne(mappedBy = "address", cascade = PERSIST)
        private InsertOrderingWithBidirectionalOneToOne.Person person;

        public void addPerson(InsertOrderingWithBidirectionalOneToOne.Person person) {
            this.person = person;
            person.address = this;
        }
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        @Column(name = "ID", nullable = false)
        @SequenceGenerator(name = "ID", sequenceName = "ADDRESS_SEQ")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID")
        private Long id;

        @OneToOne
        private InsertOrderingWithBidirectionalOneToOne.Address address;
    }
}

