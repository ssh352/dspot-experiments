/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.converter;


import java.net.MalformedURLException;
import java.util.Date;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.hibernate.Session;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Jira HHH-8812 claims that explicit {@link javax.persistence.Convert} annotations are not processed when a orm.xml
 * file is used - specifically that the mixed case is not handled properly.
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-8812")
public class XmlWithExplicitConvertAnnotationsTest extends BaseNonConfigCoreFunctionalTestCase {
    // NOTE : essentially the same exact test as ExplicitDateConvertersTest, but here we will mix annotations and xml
    static boolean convertToDatabaseColumnCalled = false;

    static boolean convertToEntityAttributeCalled = false;

    public static class LongToDateConverter implements AttributeConverter<Date, Long> {
        @Override
        public Long convertToDatabaseColumn(Date attribute) {
            XmlWithExplicitConvertAnnotationsTest.convertToDatabaseColumnCalled = true;
            return attribute.getTime();
        }

        @Override
        public Date convertToEntityAttribute(Long dbData) {
            XmlWithExplicitConvertAnnotationsTest.convertToEntityAttributeCalled = true;
            return new Date(dbData);
        }
    }

    @Entity(name = "Entity1")
    public static class Entity1 {
        @Id
        private Integer id;

        private String name;

        @Convert(converter = XmlWithExplicitConvertAnnotationsTest.LongToDateConverter.class)
        private Date theDate;

        public Entity1() {
        }

        public Entity1(Integer id, String name, Date theDate) {
            this.id = id;
            this.name = name;
            this.theDate = theDate;
        }
    }

    public static class TestEntityListener {
        @PrePersist
        @PreUpdate
        private void listen(Object entity) {
            System.out.println("@PrePersist @PreUpdate listener event fired");
        }
    }

    @Test
    public void testSimpleConvertUsage() throws MalformedURLException {
        final EntityPersister ep = sessionFactory().getEntityPersister(XmlWithExplicitConvertAnnotationsTest.Entity1.class.getName());
        final Type theDatePropertyType = ep.getPropertyType("theDate");
        final AttributeConverterTypeAdapter type = ExtraAssertions.assertTyping(AttributeConverterTypeAdapter.class, theDatePropertyType);
        Assert.assertTrue(XmlWithExplicitConvertAnnotationsTest.LongToDateConverter.class.isAssignableFrom(type.getAttributeConverter().getConverterJavaTypeDescriptor().getJavaType()));
        resetFlags();
        Session session = openSession();
        session.getTransaction().begin();
        session.persist(new XmlWithExplicitConvertAnnotationsTest.Entity1(1, "1", new Date()));
        session.getTransaction().commit();
        session.close();
        Assert.assertTrue(XmlWithExplicitConvertAnnotationsTest.convertToDatabaseColumnCalled);
        resetFlags();
        session = openSession();
        session.getTransaction().begin();
        session.get(XmlWithExplicitConvertAnnotationsTest.Entity1.class, 1);
        session.getTransaction().commit();
        session.close();
        Assert.assertTrue(XmlWithExplicitConvertAnnotationsTest.convertToEntityAttributeCalled);
        session = openSession();
        session.getTransaction().begin();
        session.createQuery("delete Entity1").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }
}
