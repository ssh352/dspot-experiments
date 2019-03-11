/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.android.desugar;


import com.google.devtools.build.android.desugar.testdata.ClassCallingRequireNonNull;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


/**
 * This test case tests the desugaring feature for Objects.requireNonNull. This feature replaces any
 * call to this method with o.getClass() to check whether 'o' is null.
 */
@RunWith(JUnit4.class)
public class DesugarObjectsRequireNonNullTest {
    @Test
    public void testClassCallingRequireNonNullHasNoReferenceToRequiresNonNull() {
        try {
            ClassReader reader = new ClassReader(ClassCallingRequireNonNull.class.getName());
            AtomicInteger counterForSingleArgument = new AtomicInteger(0);
            AtomicInteger counterForString = new AtomicInteger(0);
            AtomicInteger counterForSupplier = new AtomicInteger(0);
            reader.accept(new ClassVisitor(Opcodes.ASM5) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    return new MethodVisitor(api) {
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                            if (((opcode == (Opcodes.INVOKESTATIC)) && (owner.equals("java/util/Objects"))) && (name.equals("requireNonNull"))) {
                                switch (desc) {
                                    case "(Ljava/lang/Object;)Ljava/lang/Object;" :
                                        counterForSingleArgument.incrementAndGet();
                                        break;
                                    case "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;" :
                                        counterForString.incrementAndGet();
                                        break;
                                    case "(Ljava/lang/Object;Ljava/util/function/Supplier;)Ljava/lang/Object;" :
                                        counterForSupplier.incrementAndGet();
                                        break;
                                    default :
                                        Assert.fail(("Unknown overloaded requireNonNull is found: " + desc));
                                }
                            }
                        }
                    };
                }
            }, 0);
            assertThat(counterForSingleArgument.get()).isEqualTo(0);
            // we do not desugar requireNonNull(Object, String) or requireNonNull(Object, Supplier)
            assertThat(counterForString.get()).isEqualTo(1);
            assertThat(counterForSupplier.get()).isEqualTo(1);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testInliningImplicitCallToObjectsRequireNonNull() {
        try {
            ClassCallingRequireNonNull.getStringLengthWithMethodReference(null);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
        assertThat(ClassCallingRequireNonNull.getStringLengthWithMethodReference("")).isEqualTo(0);
        assertThat(ClassCallingRequireNonNull.getStringLengthWithMethodReference("1")).isEqualTo(1);
        try {
            ClassCallingRequireNonNull.getStringLengthWithLambdaAndExplicitCallToRequireNonNull(null);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
        assertThat(ClassCallingRequireNonNull.getStringLengthWithLambdaAndExplicitCallToRequireNonNull("")).isEqualTo(0);
        assertThat(ClassCallingRequireNonNull.getStringLengthWithLambdaAndExplicitCallToRequireNonNull("1")).isEqualTo(1);
    }

    @Test
    public void testInliningExplicitCallToObjectsRequireNonNull() {
        try {
            ClassCallingRequireNonNull.getFirstCharVersionOne(null);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            ClassCallingRequireNonNull.getFirstCharVersionTwo(null);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            ClassCallingRequireNonNull.callRequireNonNullWithArgumentString(null);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            ClassCallingRequireNonNull.callRequireNonNullWithArgumentSupplier(null);
            Assert.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // Expected
        }
        assertThat(ClassCallingRequireNonNull.getFirstCharVersionOne("hello")).isEqualTo('h');
        assertThat(ClassCallingRequireNonNull.getFirstCharVersionTwo("hello")).isEqualTo('h');
        assertThat(ClassCallingRequireNonNull.callRequireNonNullWithArgumentString("hello")).isEqualTo('h');
        assertThat(ClassCallingRequireNonNull.callRequireNonNullWithArgumentSupplier("hello")).isEqualTo('h');
    }
}

