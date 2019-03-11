/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ternary;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class TernaryTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testTernary() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Employee bob = new Employee("Bob");
        Employee tom = new Employee("Tom");
        Employee jim = new Employee("Jim");
        Employee tim = new Employee("Tim");
        Site melb = new Site("Melbourne");
        Site geel = new Site("Geelong");
        s.persist(bob);
        s.persist(tom);
        s.persist(jim);
        s.persist(tim);
        s.persist(melb);
        s.persist(geel);
        bob.getManagerBySite().put(melb, tom);
        bob.getManagerBySite().put(geel, jim);
        tim.getManagerBySite().put(melb, tom);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        tom = ((Employee) (s.get(Employee.class, "Tom")));
        Assert.assertFalse(Hibernate.isInitialized(tom.getUnderlings()));
        Assert.assertEquals(tom.getUnderlings().size(), 2);
        bob = ((Employee) (s.get(Employee.class, "Bob")));
        Assert.assertFalse(Hibernate.isInitialized(bob.getManagerBySite()));
        Assert.assertTrue(tom.getUnderlings().contains(bob));
        melb = ((Site) (s.get(Site.class, "Melbourne")));
        Assert.assertSame(bob.getManagerBySite().get(melb), tom);
        Assert.assertTrue(melb.getEmployees().contains(bob));
        Assert.assertTrue(melb.getManagers().contains(tom));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List l = s.createQuery("from Employee e join e.managerBySite m where m.name='Bob'").list();
        Assert.assertEquals(l.size(), 0);
        l = s.createQuery("from Employee e join e.managerBySite m where m.name='Tom'").list();
        Assert.assertEquals(l.size(), 2);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        l = s.createQuery("from Employee e left join fetch e.managerBySite").list();
        Assert.assertEquals(l.size(), 5);
        Set set = new HashSet(l);
        Assert.assertEquals(set.size(), 4);
        Iterator iter = set.iterator();
        int total = 0;
        while (iter.hasNext()) {
            Map map = ((Employee) (iter.next())).getManagerBySite();
            Assert.assertTrue(Hibernate.isInitialized(map));
            total += map.size();
        } 
        Assert.assertTrue((total == 3));
        l = s.createQuery("from Employee e left join e.managerBySite m left join m.managerBySite m2").list();
        // clean up...
        l = s.createQuery("from Employee e left join fetch e.managerBySite").list();
        Iterator itr = l.iterator();
        while (itr.hasNext()) {
            Employee emp = ((Employee) (itr.next()));
            emp.setManagerBySite(new HashMap());
            s.delete(emp);
        } 
        for (Object entity : s.createQuery("from Site").list()) {
            s.delete(entity);
        }
        t.commit();
        s.close();
    }

    @Test
    public void testIndexRelatedFunctions() {
        Session session = openSession();
        session.beginTransaction();
        session.createQuery("from Employee e join e.managerBySite as m where index(m) is not null").list();
        session.createQuery("from Employee e join e.managerBySite as m where minIndex(m) is not null").list();
        session.createQuery("from Employee e join e.managerBySite as m where maxIndex(m) is not null").list();
        session.getTransaction().commit();
        session.close();
    }
}

