/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.annotation;


import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.exceptions.base.MockitoException;
import org.mockitoutil.TestBase;


public class WrongSetOfAnnotationsTest extends TestBase {
    @Test(expected = MockitoException.class)
    public void should_not_allow_Mock_and_Spy() throws Exception {
        MockitoAnnotations.initMocks(new Object() {
            @Mock
            @Spy
            List<?> mock;
        });
    }

    @Test
    public void should_not_allow_Spy_and_InjectMocks_on_interfaces() throws Exception {
        try {
            MockitoAnnotations.initMocks(new Object() {
                @InjectMocks
                @Spy
                List<?> mock;
            });
            Assert.fail();
        } catch (MockitoException me) {
            Assertions.assertThat(me.getMessage()).contains("'List' is an interface");
        }
    }

    @Test
    public void should_allow_Spy_and_InjectMocks() throws Exception {
        MockitoAnnotations.initMocks(new Object() {
            @InjectMocks
            @Spy
            WrongSetOfAnnotationsTest.WithDependency mock;
        });
    }

    static class WithDependency {
        List<?> list;
    }

    @Test(expected = MockitoException.class)
    public void should_not_allow_Mock_and_InjectMocks() throws Exception {
        MockitoAnnotations.initMocks(new Object() {
            @InjectMocks
            @Mock
            List<?> mock;
        });
    }

    @Test(expected = MockitoException.class)
    public void should_not_allow_Captor_and_Mock() throws Exception {
        MockitoAnnotations.initMocks(new Object() {
            @Mock
            @Captor
            ArgumentCaptor<?> captor;
        });
    }

    @Test(expected = MockitoException.class)
    public void should_not_allow_Captor_and_Spy() throws Exception {
        MockitoAnnotations.initMocks(new Object() {
            @Spy
            @Captor
            ArgumentCaptor<?> captor;
        });
    }

    @Test(expected = MockitoException.class)
    public void should_not_allow_Captor_and_InjectMocks() throws Exception {
        MockitoAnnotations.initMocks(new Object() {
            @InjectMocks
            @Captor
            ArgumentCaptor<?> captor;
        });
    }
}

