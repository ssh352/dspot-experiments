/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.stubbing.answers;


import java.nio.charset.CharacterCodingException;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.invocation.InvocationBuilder;
import org.mockito.invocation.Invocation;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class InvocationInfoTest {
    @Test
    public void should_know_valid_throwables() throws Exception {
        // when
        Invocation invocation = new InvocationBuilder().method("canThrowException").toInvocation();
        InvocationInfo info = new InvocationInfo(invocation);
        // then
        assertThat(info.isValidException(new Exception())).isFalse();
        assertThat(info.isValidException(new CharacterCodingException())).isTrue();
    }

    @Test
    public void should_know_valid_return_types() throws Exception {
        assertThat(new InvocationInfo(new InvocationBuilder().method("integerReturningMethod").toInvocation()).isValidReturnType(Integer.class)).isTrue();
        assertThat(new InvocationInfo(new InvocationBuilder().method("integerReturningMethod").toInvocation()).isValidReturnType(int.class)).isTrue();
        assertThat(new InvocationInfo(new InvocationBuilder().method("intReturningMethod").toInvocation()).isValidReturnType(Integer.class)).isTrue();
        assertThat(new InvocationInfo(new InvocationBuilder().method("intReturningMethod").toInvocation()).isValidReturnType(int.class)).isTrue();
        assertThat(new InvocationInfo(new InvocationBuilder().method("integerReturningMethod").toInvocation()).isValidReturnType(String.class)).isFalse();
    }

    @Test
    public void should_know_when_invocation_returns_primitive() {
        assertThat(new InvocationInfo(new InvocationBuilder().method("intReturningMethod").toInvocation()).returnsPrimitive()).isTrue();
        assertThat(new InvocationInfo(new InvocationBuilder().method("integerReturningMethod").toInvocation()).returnsPrimitive()).isFalse();
    }

    @Test
    public void should_know_when_invocation_returns_void() {
        assertThat(new InvocationInfo(new InvocationBuilder().method("voidMethod").toInvocation()).isVoid()).isTrue();
        assertThat(new InvocationInfo(new InvocationBuilder().method("integerReturningMethod").toInvocation()).isVoid()).isFalse();
    }

    @Test
    public void should_read_the_method_name() {
        assertThat(new InvocationInfo(new InvocationBuilder().method("voidMethod").toInvocation()).getMethodName()).isEqualTo("voidMethod");
    }

    @Test
    public void should_read_the_method_return_name() {
        assertThat(new InvocationInfo(new InvocationBuilder().method("voidMethod").toInvocation()).printMethodReturnType()).isEqualTo("void");
        assertThat(new InvocationInfo(new InvocationBuilder().method("integerReturningMethod").toInvocation()).printMethodReturnType()).isEqualTo("Integer");
        assertThat(new InvocationInfo(new InvocationBuilder().method("intReturningMethod").toInvocation()).printMethodReturnType()).isEqualTo("int");
    }

    @Test
    public void should_know_abstract_method() throws Exception {
        // To be extended with Java 8
        assertThat(new InvocationInfo(new InvocationBuilder().method(iAmAbstract()).toInvocation()).isAbstract()).isTrue();
        assertThat(new InvocationInfo(new InvocationBuilder().method(iAmNotAbstract()).toInvocation()).isAbstract()).isFalse();
    }

    @Test
    public void should_know_method_is_declared_on_interface() throws Exception {
        assertThat(new InvocationInfo(new InvocationBuilder().method(iAmAbstract()).toInvocation()).isDeclaredOnInterface()).isFalse();
        assertThat(new InvocationInfo(new InvocationBuilder().method("voidMethod").toInvocation()).isDeclaredOnInterface()).isTrue();
    }

    @Test
    public void isVoid_invocationOnVoidMethod_returnTrue() {
        Mockito.mock(IMethods.class).voidMethod();
        InvocationInfo voidMethod = new InvocationInfo(TestBase.getLastInvocation());
        assertThat(voidMethod.isVoid()).isTrue();
    }

    @Test
    public void isVoid_invocationOnVoidReturningMethod_returnTrue() {
        Mockito.mock(IMethods.class).voidReturningMethod();
        InvocationInfo voidRetuningMethod = new InvocationInfo(TestBase.getLastInvocation());
        assertThat(voidRetuningMethod.isVoid()).isTrue();
    }

    @Test
    public void isVoid_invocationNonVoidMethod_returnFalse() {
        Mockito.mock(IMethods.class).simpleMethod();
        InvocationInfo stringReturningMethod = new InvocationInfo(TestBase.getLastInvocation());
        assertThat(stringReturningMethod.isVoid()).isFalse();
    }
}

