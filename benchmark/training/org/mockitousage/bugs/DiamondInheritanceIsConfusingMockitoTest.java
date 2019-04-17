/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


// see #508
public class DiamondInheritanceIsConfusingMockitoTest {
    @Test
    public void should_work() {
        DiamondInheritanceIsConfusingMockitoTest.Sub mock = Mockito.mock(DiamondInheritanceIsConfusingMockitoTest.Sub.class);
        // The following line results in
        // org.mockito.exceptions.misusing.MissingMethodInvocationException:
        // when() requires an argument which has to be 'a method call on a mock'.
        // Presumably confused by the interface/superclass signatures.
        Mockito.when(mock.getFoo()).thenReturn("Hello");
        Assert.assertEquals("Hello", mock.getFoo());
    }

    public class Super<T> {
        private T value;

        public Super(T value) {
            this.value = value;
        }

        public T getFoo() {
            return value;
        }
    }

    public class Sub extends DiamondInheritanceIsConfusingMockitoTest.Super<String> implements DiamondInheritanceIsConfusingMockitoTest.iInterface {
        public Sub(String s) {
            super(s);
        }
    }

    public interface iInterface {
        String getFoo();
    }
}
