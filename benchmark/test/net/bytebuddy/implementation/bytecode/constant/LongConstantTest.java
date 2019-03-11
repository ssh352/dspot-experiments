package net.bytebuddy.implementation.bytecode.constant;


import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.MethodVisitor;


@RunWith(Parameterized.class)
public class LongConstantTest {
    private final long value;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodVisitor methodVisitor;

    @Mock
    private Implementation.Context implementationContext;

    public LongConstantTest(long value) {
        this.value = value;
    }

    @Test
    public void testBiPush() throws Exception {
        StackManipulation longConstant = LongConstant.forValue(value);
        MatcherAssert.assertThat(longConstant.isValid(), CoreMatchers.is(true));
        StackManipulation.Size size = longConstant.apply(methodVisitor, implementationContext);
        MatcherAssert.assertThat(size.getSizeImpact(), CoreMatchers.is(2));
        MatcherAssert.assertThat(size.getMaximalSize(), CoreMatchers.is(2));
        Mockito.verify(methodVisitor).visitLdcInsn(value);
        Mockito.verifyNoMoreInteractions(methodVisitor);
        Mockito.verifyZeroInteractions(implementationContext);
    }
}

