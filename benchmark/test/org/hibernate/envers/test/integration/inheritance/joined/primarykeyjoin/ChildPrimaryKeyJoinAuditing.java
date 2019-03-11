/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.inheritance.joined.primarykeyjoin;


import java.util.Arrays;
import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.integration.inheritance.joined.ParentEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Adam Warski (adam at warski dot org)
 */
public class ChildPrimaryKeyJoinAuditing extends BaseEnversJPAFunctionalTestCase {
    private Integer id1;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        id1 = 1;
        // Rev 1
        em.getTransaction().begin();
        ChildPrimaryKeyJoinEntity ce = new ChildPrimaryKeyJoinEntity(id1, "x", 1L);
        em.persist(ce);
        em.getTransaction().commit();
        // Rev 2
        em.getTransaction().begin();
        ce = em.find(ChildPrimaryKeyJoinEntity.class, id1);
        ce.setData("y");
        ce.setNumVal(2L);
        em.getTransaction().commit();
    }

    @Test
    public void testRevisionsCounts() {
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(ChildPrimaryKeyJoinEntity.class, id1));
    }

    @Test
    public void testHistoryOfChildId1() {
        ChildPrimaryKeyJoinEntity ver1 = new ChildPrimaryKeyJoinEntity(id1, "x", 1L);
        ChildPrimaryKeyJoinEntity ver2 = new ChildPrimaryKeyJoinEntity(id1, "y", 2L);
        assert getAuditReader().find(ChildPrimaryKeyJoinEntity.class, id1, 1).equals(ver1);
        assert getAuditReader().find(ChildPrimaryKeyJoinEntity.class, id1, 2).equals(ver2);
        assert getAuditReader().find(ParentEntity.class, id1, 1).equals(ver1);
        assert getAuditReader().find(ParentEntity.class, id1, 2).equals(ver2);
    }

    @Test
    public void testPolymorphicQuery() {
        ChildPrimaryKeyJoinEntity childVer1 = new ChildPrimaryKeyJoinEntity(id1, "x", 1L);
        assert getAuditReader().createQuery().forEntitiesAtRevision(ChildPrimaryKeyJoinEntity.class, 1).getSingleResult().equals(childVer1);
        assert getAuditReader().createQuery().forEntitiesAtRevision(ParentEntity.class, 1).getSingleResult().equals(childVer1);
    }

    @Test
    public void testChildIdColumnName() {
        Assert.assertEquals("other_id", getName());
    }
}

