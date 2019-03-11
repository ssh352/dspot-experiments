/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.tests.e2e.server.validation.validateonexecution;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.executable.ExecutableType;
import javax.validation.executable.ValidateOnExecution;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.junit.Test;


/**
 *
 *
 * @author Michal Gajdos
 */
// @RunWith(ConcurrentRunner.class)
public class ValidateOnExecutionInheritanceTest extends ValidateOnExecutionAbstractTest {
    /**
     * On METHOD.
     */
    /**
     * {@link ValidateOnExecution} annotations from this interface should be considered during validating phase.
     */
    @SuppressWarnings({ "UnusedDeclaration", "JavaDoc" })
    public static interface ValidateExecutableOnMethodsValidation {
        @Min(0)
        @ValidateOnExecution
        public Integer validateExecutableDefault(@Max(10)
        final Integer value);

        @Min(0)
        @ValidateOnExecution(type = ExecutableType.NON_GETTER_METHODS)
        public Integer validateExecutableMatch(@Max(10)
        final Integer value);

        @Min(0)
        @ValidateOnExecution(type = ExecutableType.CONSTRUCTORS)
        public Integer validateExecutableMiss(@Max(10)
        final Integer value);

        @Min(0)
        @ValidateOnExecution(type = ExecutableType.NONE)
        public Integer validateExecutableNone(@Max(10)
        final Integer value);
    }

    @ValidateOnExecution(type = ExecutableType.ALL)
    public static interface ValidateExecutableOnMethodsJaxRs extends ValidateOnExecutionInheritanceTest.ValidateExecutableOnMethodsValidation {
        @POST
        @Path("validateExecutableDefault")
        @ValidateOnExecution(type = ExecutableType.CONSTRUCTORS)
        Integer validateExecutableDefault(final Integer value);

        @POST
        @Path("validateExecutableMatch")
        @ValidateOnExecution(type = ExecutableType.GETTER_METHODS)
        Integer validateExecutableMatch(final Integer value);

        @POST
        @Path("validateExecutableMiss")
        @ValidateOnExecution(type = ExecutableType.NON_GETTER_METHODS)
        Integer validateExecutableMiss(final Integer value);

        @POST
        @Path("validateExecutableNone")
        @ValidateOnExecution(type = ExecutableType.ALL)
        Integer validateExecutableNone(final Integer value);
    }

    public abstract static class ValidateExecutableOnMethodsAbstractResource implements ValidateOnExecutionInheritanceTest.ValidateExecutableOnMethodsJaxRs {
        @ValidateOnExecution(type = ExecutableType.NONE)
        public abstract Integer validateExecutableDefault(final Integer value);

        @ValidateOnExecution(type = ExecutableType.CONSTRUCTORS)
        public abstract Integer validateExecutableMatch(final Integer value);

        @ValidateOnExecution(type = ExecutableType.ALL)
        public abstract Integer validateExecutableMiss(final Integer value);

        @ValidateOnExecution(type = ExecutableType.NON_GETTER_METHODS)
        public abstract Integer validateExecutableNone(final Integer value);
    }

    @Path("on-method")
    public static class ValidateExecutableOnMethodsResource extends ValidateOnExecutionInheritanceTest.ValidateExecutableOnMethodsAbstractResource {
        public Integer validateExecutableDefault(final Integer value) {
            return value;
        }

        public Integer validateExecutableMatch(final Integer value) {
            return value;
        }

        public Integer validateExecutableMiss(final Integer value) {
            return value;
        }

        public Integer validateExecutableNone(final Integer value) {
            return value;
        }
    }

    /**
     * On TYPE.
     */
    @SuppressWarnings("JavaDoc")
    public static interface ValidateExecutableOnType {
        @POST
        @Min(0)
        public Integer validateExecutable(@Max(10)
        final Integer value);
    }

    @ValidateOnExecution
    public static interface ValidateExecutableOnTypeDefault extends ValidateOnExecutionInheritanceTest.ValidateExecutableOnType {}

    /**
     * This {@link ValidateOnExecution} annotation should be considered during validating phase.
     */
    @ValidateOnExecution(type = ExecutableType.GETTER_METHODS)
    public abstract static class ValidateExecutableOnTypeDefaultAbstractResource implements ValidateOnExecutionInheritanceTest.ValidateExecutableOnTypeDefault {
        public Integer validateExecutable(final Integer value) {
            return value;
        }
    }

    @Path("on-type-default")
    @ValidateOnExecution(type = ExecutableType.CONSTRUCTORS)
    public static class ValidateExecutableOnTypeDefaultResource extends ValidateOnExecutionInheritanceTest.ValidateExecutableOnTypeDefaultAbstractResource {}

    /**
     * This {@link ValidateOnExecution} annotation should be considered during validating phase.
     */
    @ValidateOnExecution(type = ExecutableType.NON_GETTER_METHODS)
    public static interface ValidateExecutableOnTypeMatch extends ValidateOnExecutionInheritanceTest.ValidateExecutableOnType {}

    @ValidateOnExecution(type = ExecutableType.GETTER_METHODS)
    public abstract static class ValidateExecutableOnTypeMatchAbstractResource implements ValidateOnExecutionInheritanceTest.ValidateExecutableOnTypeMatch {
        public Integer validateExecutable(final Integer value) {
            return value;
        }
    }

    @Path("on-type-match")
    @ValidateOnExecution(type = ExecutableType.NONE)
    public static class ValidateExecutableOnTypeMatchResource extends ValidateOnExecutionInheritanceTest.ValidateExecutableOnTypeMatchAbstractResource {}

    /**
     * This {@link ValidateOnExecution} annotation should be considered during validating phase.
     */
    @ValidateOnExecution(type = ExecutableType.CONSTRUCTORS)
    public static interface ValidateExecutableOnTypeMiss extends ValidateOnExecutionInheritanceTest.ValidateExecutableOnType {}

    @ValidateOnExecution(type = ExecutableType.NON_GETTER_METHODS)
    public abstract static class ValidateExecutableOnTypeMissAbstractResource implements ValidateOnExecutionInheritanceTest.ValidateExecutableOnTypeMiss {
        public Integer validateExecutable(final Integer value) {
            return value;
        }
    }

    @Path("on-type-miss")
    @ValidateOnExecution
    public static class ValidateExecutableOnTypeMissResource extends ValidateOnExecutionInheritanceTest.ValidateExecutableOnTypeMissAbstractResource {}

    /**
     * This {@link ValidateOnExecution} annotation should be considered during validating phase.
     */
    @ValidateOnExecution(type = ExecutableType.NONE)
    public static interface ValidateExecutableOnTypeNone extends ValidateOnExecutionInheritanceTest.ValidateExecutableOnType {}

    @ValidateOnExecution(type = ExecutableType.ALL)
    public abstract static class ValidateExecutableOnTypeNoneAbstractResource implements ValidateOnExecutionInheritanceTest.ValidateExecutableOnTypeNone {
        public Integer validateExecutable(final Integer value) {
            return value;
        }
    }

    @Path("on-type-none")
    @ValidateOnExecution(type = { ExecutableType.CONSTRUCTORS, ExecutableType.NON_GETTER_METHODS })
    public static class ValidateExecutableOnTypeNoneResource extends ValidateOnExecutionInheritanceTest.ValidateExecutableOnTypeNoneAbstractResource {}

    /**
     * MIXED.
     */
    @ValidateOnExecution(type = ExecutableType.NONE)
    public static interface ValidateExecutableMixedDefault {
        @Min(0)
        @ValidateOnExecution
        public Integer validateExecutable(@Max(10)
        final Integer value);
    }

    @Path("mixed-default")
    public static class ValidateExecutableMixedDefaultResource implements ValidateOnExecutionInheritanceTest.ValidateExecutableMixedDefault {
        @POST
        @ValidateOnExecution(type = ExecutableType.CONSTRUCTORS)
        public Integer validateExecutable(final Integer value) {
            return value;
        }
    }

    @ValidateOnExecution
    public static interface ValidateExecutableMixedNone {
        @Min(0)
        @ValidateOnExecution(type = ExecutableType.NONE)
        public Integer validateExecutable(@Max(10)
        final Integer value);
    }

    @Path("mixed-none")
    public static class ValidateExecutableMixedNoneResource implements ValidateOnExecutionInheritanceTest.ValidateExecutableMixedNone {
        @POST
        @ValidateOnExecution(type = ExecutableType.ALL)
        public Integer validateExecutable(final Integer value) {
            return value;
        }
    }

    @ValidateOnExecution
    public static interface ValidateExecutableMixedClassDefault {
        @Min(0)
        public Integer validateExecutable(@Max(10)
        final Integer value);
    }

    @Path("mixed-class-default")
    @ValidateOnExecution(type = ExecutableType.NONE)
    public static class ValidateExecutableMixedClassDefaultResource implements ValidateOnExecutionInheritanceTest.ValidateExecutableMixedClassDefault {
        @POST
        @ValidateOnExecution(type = ExecutableType.CONSTRUCTORS)
        public Integer validateExecutable(final Integer value) {
            return value;
        }
    }

    @ValidateOnExecution(type = ExecutableType.NONE)
    public static interface ValidateExecutableMixedClassNone {
        @Min(0)
        public Integer validateExecutable(@Max(10)
        final Integer value);
    }

    @Path("mixed-class-none")
    @ValidateOnExecution(type = ExecutableType.NON_GETTER_METHODS)
    public static class ValidateExecutableMixedClassNoneResource implements ValidateOnExecutionInheritanceTest.ValidateExecutableMixedClassNone {
        @POST
        @ValidateOnExecution(type = ExecutableType.ALL)
        public Integer validateExecutable(final Integer value) {
            return value;
        }
    }

    @Test
    public void testOnTypeValidateInputPassValidateExecutableDefault() throws Exception {
        _testOnType("default", 15, 200);
    }

    @Test
    public void testOnTypeValidateResultPassNoValidateExecutableDefault() throws Exception {
        _testOnType("default", (-15), 200);
    }

    @Test
    public void testMixedClassValidatePassDefault() throws Exception {
        _test("mixed-class-default", 0, 200);
    }

    @Test
    public void testMixedClassValidateInputPassValidateDefault() throws Exception {
        _test("mixed-class-default", 15, 200);
    }

    @Test
    public void testMixedClassValidateResultPassNoValidateDefault() throws Exception {
        _test("mixed-class-default", (-15), 200);
    }

    @Test
    public void testMixedClassValidatePassNone() throws Exception {
        _test("mixed-class-none", 0, 200);
    }

    @Test
    public void testMixedClassValidateInputPassNone() throws Exception {
        _test("mixed-class-none", 15, 200);
    }

    @Test
    public void testMixedClassValidateResultPassNone() throws Exception {
        _test("mixed-class-none", (-15), 200);
    }
}

