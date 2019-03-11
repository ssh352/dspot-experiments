package org.osmdroid.util;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 6.0.2
 * @author Fabrice Fontaine
 */
public class DelayTest {
    @Test
    public void testDelayOne() {
        final long millis = 500;
        final Delay delay = new Delay(millis);
        for (int i = 0; i < 5; i++) {
            check(delay, millis);
            final long next = delay.next();
            Assert.assertEquals(millis, next);
        }
    }

    @Test
    public void testDelayMulti() {
        final long[] millis = new long[]{ 500, 600, 800, 1000 };
        final long lastDuration = millis[((millis.length) - 1)];
        long next;
        final Delay delay = new Delay(millis);
        for (int i = 0; i < (millis.length); i++) {
            check(delay, millis[i]);
            next = delay.next();
            Assert.assertEquals((i < ((millis.length) - 1) ? millis[(i + 1)] : lastDuration), next);
        }
        check(delay, lastDuration);
        next = delay.next();
        Assert.assertEquals(lastDuration, next);
        check(delay, lastDuration);
    }
}

