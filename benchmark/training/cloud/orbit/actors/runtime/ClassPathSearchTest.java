/**
 * Copyright (C) 2016 Electronic Arts Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2.  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
 * its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package cloud.orbit.actors.runtime;


import org.junit.Assert;
import org.junit.Test;


public class ClassPathSearchTest {
    @Test
    public void testUtilityFunctions() {
        Assert.assertEquals(0, ClassPathSearch.commonStart("BBB", "CCC"));
        Assert.assertEquals(0, ClassPathSearch.commonEnd("AAAA", "BBB"));
        Assert.assertEquals(0, ClassPathSearch.commonStart("BBB", ""));
        Assert.assertEquals(0, ClassPathSearch.commonEnd("AAAA", ""));
        Assert.assertEquals(0, ClassPathSearch.commonStart("", "A"));
        Assert.assertEquals(0, ClassPathSearch.commonEnd("", "A"));
        Assert.assertEquals(1, ClassPathSearch.commonStart("A", "A"));
        Assert.assertEquals(1, ClassPathSearch.commonStart("ssssBBB", "sCCC"));
        Assert.assertEquals(2, ClassPathSearch.commonStart("ssssBBB", "ssCCC"));
        Assert.assertEquals(3, ClassPathSearch.commonStart("ssssBBB", "sssCCC"));
        Assert.assertEquals(1, ClassPathSearch.commonStart("sBBB", "sssCCC"));
        Assert.assertEquals(2, ClassPathSearch.commonStart("ssBBB", "sssCCC"));
        Assert.assertEquals(3, ClassPathSearch.commonStart("sssBBB", "sssCCC"));
        Assert.assertEquals(1, ClassPathSearch.commonEnd("AAAAe", "BBBee"));
        Assert.assertEquals(2, ClassPathSearch.commonEnd("AAAAeeee", "BBBee"));
        Assert.assertEquals(3, ClassPathSearch.commonEnd("AAAAeeee", "BBBeee"));
    }

    public interface IHi {}

    public class Hi implements ClassPathSearchTest.IHi {}

    @Test
    public void testFind() {
        Assert.assertSame(ClassPathSearchTest.Hi.class, new ClassPathSearch().findImplementation(ClassPathSearchTest.IHi.class));
    }

    public interface IPla1 {}

    public interface IPla2 extends ClassPathSearchTest.IPla1 {}

    public interface IPla3 extends ClassPathSearchTest.IAA {}

    public interface IPla4 extends ClassPathSearchTest.IAA {}

    public class Pla1 implements ClassPathSearchTest.IPla1 {}

    public class Pla2 implements ClassPathSearchTest.IPla2 {}

    public interface IAA {}

    public class XPla4 implements ClassPathSearchTest.IPla3 {}

    public class XPla3 implements ClassPathSearchTest.IPla4 {}

    @Test
    public void testFindWithSubInterfaces() {
        ClassPathSearch classPathSearch = new ClassPathSearch();
        Assert.assertSame(ClassPathSearchTest.Pla1.class, classPathSearch.findImplementation(ClassPathSearchTest.IPla1.class));
        Assert.assertSame(ClassPathSearchTest.Pla2.class, classPathSearch.findImplementation(ClassPathSearchTest.IPla2.class));
    }

    @Test
    public void testEarlyRemovalBug() {
        ClassPathSearch classPathSearch = new ClassPathSearch(ClassPathSearchTest.IAA.class);
        Assert.assertSame(ClassPathSearchTest.XPla3.class, classPathSearch.findImplementation(ClassPathSearchTest.IPla4.class));
        Assert.assertSame(ClassPathSearchTest.XPla4.class, classPathSearch.findImplementation(ClassPathSearchTest.IPla3.class));
    }

    public interface IMul {}

    public abstract class Mul implements ClassPathSearchTest.IMul {}

    public class MulImpl extends ClassPathSearchTest.Mul {}

    @Test
    public void testFindWithAbstractClass() {
        ClassPathSearch classPathSearch = new ClassPathSearch();
        Assert.assertSame(ClassPathSearchTest.MulImpl.class, classPathSearch.findImplementation(ClassPathSearchTest.IMul.class));
    }

    public interface IFail {}

    @Test
    public void testFailedSearch() {
        ClassPathSearch classPathSearch = new ClassPathSearch();
        // without limit this would do a full classpath search...
        Assert.assertNull(classPathSearch.findImplementation(ClassPathSearchTest.IFail.class, 200));
    }
}

