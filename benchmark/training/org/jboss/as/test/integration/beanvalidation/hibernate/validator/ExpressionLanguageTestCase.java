/**
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.as.test.integration.beanvalidation.hibernate.validator;


import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Size;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that Unified EL expressions can be used in Bean Violation messages as supported since BV 1.1.
 *
 * @author Gunnar Morling
 */
@RunWith(Arquillian.class)
public class ExpressionLanguageTestCase {
    @Test
    public void testValidationUsingExpressionLanguage() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<ExpressionLanguageTestCase.TestBean>> violations = validator.validate(new ExpressionLanguageTestCase.TestBean());
        Assert.assertEquals(1, violations.size());
        Assert.assertEquals("'Bob' is too short, it should at least be 5 characters long.", violations.iterator().next().getMessage());
    }

    private static class TestBean {
        @Size(min = 5, message = "'${validatedValue}' is too short, it should at least be {min} characters long.")
        private final String name = "Bob";
    }
}

