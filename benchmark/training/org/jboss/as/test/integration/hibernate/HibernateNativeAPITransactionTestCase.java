/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.hibernate;


import javax.naming.InitialContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test operations including rollback using Hibernate transaction and Sessionfactory inititated from hibernate.cfg.xml and
 * properties added to Hibernate Configuration in AS7 container without any JPA assistance
 *
 * @author Madhumita Sadhukhan
 */
@RunWith(Arquillian.class)
public class HibernateNativeAPITransactionTestCase {
    private static final String ARCHIVE_NAME = "hibernate4native_transactiontest";

    public static final String hibernate_cfg = "<?xml version='1.0' encoding='utf-8'?>" + ((((((("<!DOCTYPE hibernate-configuration PUBLIC " + "\"//Hibernate/Hibernate Configuration DTD 3.0//EN\" ") + "\"http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd\">") + "<hibernate-configuration><session-factory>") + "<property name=\"show_sql\">false</property>") + "<property name=\"current_session_context_class\">thread</property>") + "<mapping resource=\"testmapping.hbm.xml\"/>") + "</session-factory></hibernate-configuration>");

    public static final String testmapping = "<?xml version=\"1.0\"?>" + ((((((((((("<!DOCTYPE hibernate-mapping PUBLIC " + "\"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" ") + "\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">") + "<hibernate-mapping package=\"org.jboss.as.test.integration.hibernate\">") + "<class name=\"org.jboss.as.test.integration.hibernate.Student\" table=\"STUDENT\">") + "<id name=\"studentId\" column=\"student_id\">") + "<generator class=\"native\"/>") + "</id>") + "<property name=\"firstName\" column=\"first_name\"/>") + "<property name=\"lastName\" column=\"last_name\"/>") + "<property name=\"address\"/>") + // + "<set name=\"courses\" table=\"student_courses\">"
    // + "<key column=\"student_id\"/>"
    // + "<many-to-many column=\"course_id\" class=\"org.jboss.as.test.integration.nonjpa.hibernate.Course\"/>"
    // + "</set>" +
    "</class></hibernate-mapping>");

    @ArquillianResource
    private static InitialContext iniCtx;

    @Test
    public void testSimpleOperation() throws Exception {
        SFSBHibernateTransaction sfsb = HibernateNativeAPITransactionTestCase.lookup("SFSBHibernateTransaction", SFSBHibernateTransaction.class);
        // setup Configuration and SessionFactory
        sfsb.setupConfig();
        try {
            Student s1 = sfsb.createStudent("MADHUMITA", "SADHUKHAN", "99 Purkynova REDHAT BRNO CZ", 1);
            Student s2 = sfsb.createStudent("REDHAT", "LINUX", "Worldwide", 3);
            Assert.assertTrue("address read from hibernate session associated with hibernate transaction is 99 Purkynova REDHAT BRNO CZ", "99 Purkynova REDHAT BRNO CZ".equals(s1.getAddress()));
            // update Student
            Student s3 = sfsb.updateStudent("REDHAT RALEIGH, NORTH CAROLINA", 1);
            Student st = sfsb.getStudentNoTx(s1.getStudentId());
            Assert.assertTrue("address read from hibernate session associated with hibernate transaction is REDHAT RALEIGH, NORTH CAROLINA", "REDHAT RALEIGH, NORTH CAROLINA".equals(st.getAddress()));
        } finally {
            sfsb.cleanup();
        }
    }

    // tests rollback
    @Test
    public void testRollBackOperation() throws Exception {
        SFSBHibernateTransaction sfsb = HibernateNativeAPITransactionTestCase.lookup("SFSBHibernateTransaction", SFSBHibernateTransaction.class);
        // setup Configuration and SessionFactory
        try {
            sfsb.setupConfig();
            Student s2 = sfsb.createStudent("REDHAT", "LINUX", "Worldwide", 3);
            // force creation of student with same Id to ensure RollBack
            Student s3 = sfsb.createStudent("Hibernate", "ORM", "JavaWorld", s2.getStudentId());
            Student st = sfsb.getStudentNoTx(s2.getStudentId());
            Assert.assertTrue("name read from hibernate session associated with hibernate transaction after rollback is REDHAT", "REDHAT".equals(st.getFirstName()));
        } finally {
            sfsb.cleanup();
        }
    }
}

