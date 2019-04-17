package org.zeromq;


import ZTimer.Handler;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZTimer.Timer;
import zmq.ZMQ;
import zmq.util.TimersTest;


public class ZTimerTest {
    private static final Timer NON_EXISTENT = new ZTimer.Timer(TimersTest.NON_EXISTENT);

    private ZTimer timers;

    private AtomicBoolean invoked = new AtomicBoolean();

    private final Handler handler = new ZTimer.Handler() {
        @Override
        public void time(Object... args) {
            AtomicBoolean invoked = ((AtomicBoolean) (args[0]));
            invoked.set(true);
        }
    };

    @Test
    public void testCancelNonExistentTimer() {
        boolean rc = timers.cancel(ZTimerTest.NON_EXISTENT);
        Assert.assertThat(rc, CoreMatchers.is(false));
    }

    @Test
    public void testSetIntervalNonExistentTimer() {
        boolean rc = timers.setInterval(ZTimerTest.NON_EXISTENT, 10);
        Assert.assertThat(rc, CoreMatchers.is(false));
    }

    @Test
    public void testResetNonExistentTimer() {
        boolean rc = timers.reset(ZTimerTest.NON_EXISTENT);
        Assert.assertThat(rc, CoreMatchers.is(false));
    }

    @Test
    public void testAddFaultyHandler() {
        Timer timer = timers.add(10, null);
        Assert.assertThat(timer, CoreMatchers.nullValue());
    }

    @Test
    public void testCancelTwice() {
        Timer timer = timers.add(10, handler);
        Assert.assertThat(timer, CoreMatchers.notNullValue());
        boolean rc = timers.cancel(timer);
        Assert.assertThat(rc, CoreMatchers.is(true));
        rc = timers.cancel(timer);
        Assert.assertThat(rc, CoreMatchers.is(false));
    }

    @Test
    public void testTimeoutNoActiveTimers() {
        long timeout = timers.timeout();
        Assert.assertThat(timeout, CoreMatchers.is((-1L)));
    }

    @Test
    public void testNotInvokedInitial() {
        long fullTimeout = 100;
        timers.add(fullTimeout, handler, invoked);
        // Timer should not have been invoked yet
        int rc = timers.execute();
        Assert.assertThat(rc, CoreMatchers.is(0));
    }

    @Test
    public void testNotInvokedHalfTime() {
        long fullTimeout = 100;
        timers.add(fullTimeout, handler, invoked);
        // Wait half the time and check again
        long timeout = timers.timeout();
        ZMQ.msleep((timeout / 2));
        int rc = timers.execute();
        Assert.assertThat(rc, CoreMatchers.is(0));
    }

    @Test
    public void testInvoked() {
        long fullTimeout = 100;
        timers.add(fullTimeout, handler, invoked);
        // Wait until the end
        timers.sleepAndExecute();
        Assert.assertThat(invoked.get(), CoreMatchers.is(true));
    }

    @Test
    public void testNotInvokedAfterHalfTimeAgain() {
        long fullTimeout = 100;
        timers.add(fullTimeout, handler, invoked);
        // Wait until the end
        timers.sleepAndExecute();
        Assert.assertThat(invoked.get(), CoreMatchers.is(true));
        // Wait half the time and check again
        long timeout = timers.timeout();
        ZMQ.msleep((timeout / 2));
        int rc = timers.execute();
        Assert.assertThat(rc, CoreMatchers.is(0));
    }

    @Test
    public void testNotInvokedAfterResetHalfTime() {
        long fullTimeout = 100;
        Timer timer = timers.add(fullTimeout, handler, invoked);
        // Wait half the time and check again
        long timeout = timers.timeout();
        ZMQ.msleep((timeout / 2));
        int rc = timers.execute();
        Assert.assertThat(rc, CoreMatchers.is(0));
        // Reset timer and wait half of the time left
        boolean ret = timers.reset(timer);
        Assert.assertThat(ret, CoreMatchers.is(true));
        ZMQ.msleep((timeout / 2));
        rc = timers.execute();
        Assert.assertThat(rc, CoreMatchers.is(0));
    }

    @Test
    public void testInvokedAfterReset() {
        testNotInvokedAfterResetHalfTime();
        // Wait until the end
        timers.sleepAndExecute();
        Assert.assertThat(invoked.get(), CoreMatchers.is(true));
    }

    @Test
    public void testReschedule() {
        long fullTimeout = 100;
        Timer timer = timers.add(fullTimeout, handler, invoked);
        // reschedule
        boolean ret = timers.setInterval(timer, 50);
        Assert.assertThat(ret, CoreMatchers.is(true));
        timers.sleepAndExecute();
        Assert.assertThat(invoked.get(), CoreMatchers.is(true));
    }

    @Test
    public void testCancel() {
        long fullTimeout = 100;
        Timer timer = timers.add(fullTimeout, handler, invoked);
        // cancel timer
        long timeout = timers.timeout();
        boolean ret = timers.cancel(timer);
        Assert.assertThat(ret, CoreMatchers.is(true));
        ZMQ.msleep((timeout * 2));
        int rc = timers.execute();
        Assert.assertThat(rc, CoreMatchers.is(0));
        Assert.assertThat(invoked.get(), CoreMatchers.is(false));
    }
}
