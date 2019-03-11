package com.baeldung.cglib.proxy;


import com.baeldung.cglib.mixin.Class1;
import com.baeldung.cglib.mixin.Class2;
import com.baeldung.cglib.mixin.Interface1;
import com.baeldung.cglib.mixin.Interface2;
import com.baeldung.cglib.mixin.MixinInterface;
import junit.framework.TestCase;
import net.sf.cglib.proxy.Mixin;
import org.junit.Test;


public class MixinUnitTest {
    @Test
    public void givenTwoClasses_whenMixedIntoOne_thenMixinShouldHaveMethodsFromBothClasses() throws Exception {
        // when
        Mixin mixin = Mixin.create(new Class[]{ Interface1.class, Interface2.class, MixinInterface.class }, new Object[]{ new Class1(), new Class2() });
        MixinInterface mixinDelegate = ((MixinInterface) (mixin));
        // then
        TestCase.assertEquals("first behaviour", mixinDelegate.first());
        TestCase.assertEquals("second behaviour", mixinDelegate.second());
    }
}

