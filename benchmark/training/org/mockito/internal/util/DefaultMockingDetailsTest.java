/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.util;


import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.exceptions.misusing.NotAMockException;
import org.mockito.stubbing.Stubbing;
import org.mockitousage.IMethods;


@SuppressWarnings("unchecked")
public class DefaultMockingDetailsTest {
    @Mock
    private DefaultMockingDetailsTest.Foo foo;

    @Mock
    private DefaultMockingDetailsTest.Bar bar;

    @Mock
    private IMethods mock;

    @Spy
    private DefaultMockingDetailsTest.Gork gork;

    @Test
    public void should_provide_original_mock() throws Exception {
        // expect
        Assert.assertEquals(getMock(), foo);
        Assert.assertEquals(getMock(), null);
    }

    @Test
    public void should_know_spy() {
        Assert.assertTrue(Mockito.mockingDetails(gork).isMock());
        Assert.assertTrue(Mockito.mockingDetails(Mockito.spy(new DefaultMockingDetailsTest.Gork())).isMock());
        Assert.assertTrue(Mockito.mockingDetails(Mockito.spy(DefaultMockingDetailsTest.Gork.class)).isMock());
        Assert.assertTrue(Mockito.mockingDetails(Mockito.mock(DefaultMockingDetailsTest.Gork.class, Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS))).isMock());
    }

    @Test
    public void should_know_mock() {
        Assert.assertTrue(Mockito.mockingDetails(foo).isMock());
        Assert.assertTrue(Mockito.mockingDetails(Mockito.mock(DefaultMockingDetailsTest.Foo.class)).isMock());
        Assert.assertFalse(Mockito.mockingDetails(foo).isSpy());
        Assert.assertFalse(Mockito.mockingDetails(Mockito.mock(DefaultMockingDetailsTest.Foo.class)).isSpy());
    }

    @Test
    public void should_handle_non_mocks() {
        Assert.assertFalse(Mockito.mockingDetails("non mock").isSpy());
        Assert.assertFalse(Mockito.mockingDetails("non mock").isMock());
        Assert.assertFalse(Mockito.mockingDetails(null).isSpy());
        Assert.assertFalse(Mockito.mockingDetails(null).isMock());
    }

    @Test
    public void should_check_that_a_spy_is_also_a_mock() throws Exception {
        Assert.assertEquals(true, Mockito.mockingDetails(gork).isMock());
    }

    @Test
    public void provides_invocations() {
        // when
        mock.simpleMethod(10);
        mock.otherMethod();
        // then
        Assert.assertEquals(0, Mockito.mockingDetails(foo).getInvocations().size());
        Assert.assertEquals("[mock.simpleMethod(10);, mock.otherMethod();]", Mockito.mockingDetails(mock).getInvocations().toString());
    }

    @Test
    public void manipulating_invocations_is_safe() {
        mock.simpleMethod();
        // when we manipulate the invocations
        Mockito.mockingDetails(mock).getInvocations().clear();
        // then we didn't actually changed the invocations
        Assert.assertEquals(1, Mockito.mockingDetails(mock).getInvocations().size());
    }

    @Test
    public void provides_mock_creation_settings() {
        // smoke test some creation settings
        Assert.assertEquals(DefaultMockingDetailsTest.Foo.class, Mockito.mockingDetails(foo).getMockCreationSettings().getTypeToMock());
        Assert.assertEquals(DefaultMockingDetailsTest.Bar.class, Mockito.mockingDetails(bar).getMockCreationSettings().getTypeToMock());
        Assert.assertEquals(0, Mockito.mockingDetails(mock).getMockCreationSettings().getExtraInterfaces().size());
    }

    @Test(expected = NotAMockException.class)
    public void fails_when_getting_creation_settings_for_incorrect_input() {
        Mockito.mockingDetails(null).getMockCreationSettings();
    }

    @Test
    public void fails_when_getting_invocations_when_null() {
        try {
            // when
            Mockito.mockingDetails(null).getInvocations();
            // then
            Assert.fail();
        } catch (NotAMockException e) {
            Assert.assertEquals("Argument passed to Mockito.mockingDetails() should be a mock, but is null!", e.getMessage());
        }
    }

    @Test
    public void fails_when_getting_invocations_when_not_mock() {
        try {
            // when
            Mockito.mockingDetails(new Object()).getInvocations();
            // then
            Assert.fail();
        } catch (NotAMockException e) {
            Assert.assertEquals("Argument passed to Mockito.mockingDetails() should be a mock, but is an instance of class java.lang.Object!", e.getMessage());
        }
    }

    @Test
    public void fails_when_getting_stubbings_from_non_mock() {
        try {
            // when
            Mockito.mockingDetails(new Object()).getStubbings();
            // then
            Assert.fail();
        } catch (NotAMockException e) {
            Assert.assertEquals("Argument passed to Mockito.mockingDetails() should be a mock, but is an instance of class java.lang.Object!", e.getMessage());
        }
    }

    @Test
    public void mock_with_no_stubbings() {
        Assert.assertTrue(Mockito.mockingDetails(mock).getStubbings().isEmpty());
    }

    @Test
    public void provides_stubbings_of_mock_in_declaration_order() {
        Mockito.when(mock.simpleMethod(1)).thenReturn("1");
        Mockito.when(mock.otherMethod()).thenReturn("2");
        // when
        Collection<Stubbing> stubbings = Mockito.mockingDetails(mock).getStubbings();
        // then
        Assert.assertEquals(2, stubbings.size());
        Assert.assertEquals("[mock.simpleMethod(1); stubbed with: [Returns: 1], mock.otherMethod(); stubbed with: [Returns: 2]]", stubbings.toString());
    }

    @Test
    public void manipulating_stubbings_explicitly_is_safe() {
        Mockito.when(mock.simpleMethod(1)).thenReturn("1");
        // when somebody manipulates stubbings directly
        Mockito.mockingDetails(mock).getStubbings().clear();
        // then it does not affect stubbings of the mock
        Assert.assertEquals(1, Mockito.mockingDetails(mock).getStubbings().size());
    }

    @Test
    public void prints_invocations() throws Exception {
        // given
        BDDMockito.given(mock.simpleMethod("different arg")).willReturn("foo");
        mock.simpleMethod("arg");
        // when
        String log = Mockito.mockingDetails(mock).printInvocations();
        // then
        assertThat(log).containsIgnoringCase("unused");
        assertThat(log).containsIgnoringCase("mock.simpleMethod(\"arg\")");
        assertThat(log).containsIgnoringCase("mock.simpleMethod(\"different arg\")");
    }

    @Test
    public void fails_when_printin_invocations_from_non_mock() {
        try {
            // when
            Mockito.mockingDetails(new Object()).printInvocations();
            // then
            Assert.fail();
        } catch (NotAMockException e) {
            Assert.assertEquals("Argument passed to Mockito.mockingDetails() should be a mock, but is an instance of class java.lang.Object!", e.getMessage());
        }
    }

    public class Foo {}

    public interface Bar {}

    public static class Gork {}
}

