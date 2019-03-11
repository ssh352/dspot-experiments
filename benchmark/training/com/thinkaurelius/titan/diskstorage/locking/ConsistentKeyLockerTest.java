package com.thinkaurelius.titan.diskstorage.locking;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.thinkaurelius.titan.diskstorage.configuration.Configuration;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.KeyColumnValueStore;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreManager;
import com.thinkaurelius.titan.diskstorage.keycolumnvalue.StoreTransaction;
import com.thinkaurelius.titan.diskstorage.util.BufferUtil;
import com.thinkaurelius.titan.diskstorage.util.KeyColumn;
import com.thinkaurelius.titan.diskstorage.util.StaticBuffer;
import com.thinkaurelius.titan.diskstorage.util.time.Timer;
import com.thinkaurelius.titan.diskstorage.util.time.TimestampProvider;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;


public class ConsistentKeyLockerTest {
    // Arbitrary literals -- the exact values assigned here are not intrinsically important
    private final ConsistentKeyLockerSerializer codec = new ConsistentKeyLockerSerializer();

    private final StaticBuffer defaultDataKey = BufferUtil.getIntBuffer(2);

    private final StaticBuffer defaultDataCol = BufferUtil.getIntBuffer(4);

    private final StaticBuffer defaultLockKey = codec.toLockKey(defaultDataKey, defaultDataCol);

    private final KeyColumn defaultLockID = new KeyColumn(defaultDataKey, defaultDataCol);

    private final StaticBuffer otherDataKey = BufferUtil.getIntBuffer(8);

    private final StaticBuffer otherDataCol = BufferUtil.getIntBuffer(16);

    private final StaticBuffer otherLockKey = codec.toLockKey(otherDataKey, otherDataCol);

    private final KeyColumn otherLockID = new KeyColumn(otherDataKey, otherDataCol);

    private final StaticBuffer defaultLockRid = new StaticArrayBuffer(new byte[]{ ((byte) (32)) });

    private final StaticBuffer otherLockRid = new StaticArrayBuffer(new byte[]{ ((byte) (64)) });

    private final StaticBuffer defaultLockVal = BufferUtil.getIntBuffer(0);// maybe refactor...


    private StoreTransaction defaultTx;

    private BaseTransactionConfig defaultTxCfg;

    private Configuration defaultTxCustomOpts;

    private StoreTransaction otherTx;

    private BaseTransactionConfig otherTxCfg;

    private Configuration otherTxCustomOpts;

    private final Duration defaultWaitNS = Duration.ofNanos(((100 * 1000) * 1000));

    private final Duration defaultExpireNS = Duration.ofNanos((((30L * 1000) * 1000) * 1000));

    private final int maxTemporaryStorageExceptions = 3;

    private IMocksControl ctrl;

    private IMocksControl relaxedCtrl;

    private Instant currentTimeNS;

    private TimestampProvider times;

    private KeyColumnValueStore store;

    private StoreManager manager;

    private LocalLockMediator<StoreTransaction> mediator;

    private LockerState<ConsistentKeyLockStatus> lockState;

    private ConsistentKeyLocker locker;

    /**
     * Test a single lock using stub objects. Doesn't test unlock ("leaks" the
     * lock, but since it's backed by stubs, it doesn't matter).
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testWriteLockInSimplestCase() throws BackendException {
        // Check to see whether the lock was already written before anything else
        expect(lockState.has(defaultTx, defaultLockID)).andReturn(false);
        // Now lock it locally to block other threads in the process
        recordSuccessfulLocalLock();
        // Write a lock claim column to the store
        ConsistentKeyLockerTest.LockInfo li = recordSuccessfulLockWrite(1, ChronoUnit.NANOS, null);
        // Update the expiration timestamp of the local (thread-level) lock
        recordSuccessfulLocalLock(li.tsNS);
        // Store the taken lock's key, column, and timestamp in the lockState map
        lockState.take(eq(defaultTx), eq(defaultLockID), eq(li.stat));
        ctrl.replay();
        locker.writeLock(defaultLockID, defaultTx);// SUT

    }

    /**
     * Test locker when first attempt to write to the store takes too long (but
     * succeeds). Expected behavior is to call mutate on the store, adding a
     * column with a new timestamp and deleting the column with the old
     * (too-slow-to-write) timestamp.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testWriteLockRetriesAfterOneStoreTimeout() throws BackendException {
        expect(lockState.has(defaultTx, defaultLockID)).andReturn(false);
        recordSuccessfulLocalLock();
        StaticBuffer firstCol = recordSuccessfulLockWrite(5, ChronoUnit.SECONDS, null).col;// too slow

        ConsistentKeyLockerTest.LockInfo secondLI = recordSuccessfulLockWrite(1, ChronoUnit.NANOS, firstCol);// plenty fast

        recordSuccessfulLocalLock(secondLI.tsNS);
        lockState.take(eq(defaultTx), eq(defaultLockID), eq(secondLI.stat));
        ctrl.replay();
        locker.writeLock(defaultLockID, defaultTx);// SUT

    }

    /**
     * Test locker when all three attempts to write a lock succeed but take
     * longer than the wait limit. We expect the locker to delete all three
     * columns that it wrote and locally unlock the KeyColumn, then emit an
     * exception.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testWriteLockThrowsExceptionAfterMaxStoreTimeouts() throws BackendException {
        expect(lockState.has(defaultTx, defaultLockID)).andReturn(false);
        recordSuccessfulLocalLock();
        StaticBuffer firstCol = recordSuccessfulLockWrite(5, ChronoUnit.SECONDS, null).col;
        StaticBuffer secondCol = recordSuccessfulLockWrite(5, ChronoUnit.SECONDS, firstCol).col;
        StaticBuffer thirdCol = recordSuccessfulLockWrite(5, ChronoUnit.SECONDS, secondCol).col;
        recordSuccessfulLockDelete(1, ChronoUnit.NANOS, thirdCol);
        recordSuccessfulLocalUnlock();
        ctrl.replay();
        BackendException expected = null;
        try {
            locker.writeLock(defaultLockID, defaultTx);// SUT

        } catch (TemporaryBackendException e) {
            expected = e;
        }
        Assert.assertNotNull(expected);
    }

    /**
     * Test that the first {@link com.thinkaurelius.titan.diskstorage.PermanentBackendException} thrown by the
     * locker's store causes it to attempt to delete outstanding lock writes and
     * then emit the exception without retrying.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testWriteLockDiesOnPermanentStorageException() throws BackendException {
        PermanentBackendException errOnFire = new PermanentBackendException("Storage cluster is on fire");
        expect(lockState.has(defaultTx, defaultLockID)).andReturn(false);
        recordSuccessfulLocalLock();
        StaticBuffer lockCol = recordExceptionLockWrite(1, ChronoUnit.NANOS, null, errOnFire);
        recordSuccessfulLockDelete(1, ChronoUnit.NANOS, lockCol);
        recordSuccessfulLocalUnlock();
        ctrl.replay();
        BackendException expected = null;
        try {
            locker.writeLock(defaultLockID, defaultTx);// SUT

        } catch (PermanentLockingException e) {
            expected = e;
        }
        Assert.assertNotNull(expected);
        Assert.assertEquals(errOnFire, expected.getCause());
    }

    /**
     * Test the locker retries a lock write after the initial store mutation
     * fails with a {@link com.thinkaurelius.titan.diskstorage.TemporaryBackendException}. The retry should both
     * attempt to write the and delete the failed mutation column.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testWriteLockRetriesOnTemporaryStorageException() throws BackendException {
        TemporaryBackendException tse = new TemporaryBackendException("Storage cluster is waking up");
        expect(lockState.has(defaultTx, defaultLockID)).andReturn(false);
        recordSuccessfulLocalLock();
        StaticBuffer firstCol = recordExceptionLockWrite(1, ChronoUnit.NANOS, null, tse);
        ConsistentKeyLockerTest.LockInfo secondLI = recordSuccessfulLockWrite(1, ChronoUnit.NANOS, firstCol);
        recordSuccessfulLocalLock(secondLI.tsNS);
        lockState.take(eq(defaultTx), eq(defaultLockID), eq(secondLI.stat));
        ctrl.replay();
        locker.writeLock(defaultLockID, defaultTx);// SUT

    }

    /**
     * Test that a failure to lock locally results in a {@link TemporaryLockingException}
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testWriteLockFailsOnLocalContention() throws BackendException {
        expect(lockState.has(defaultTx, defaultLockID)).andReturn(false);
        recordFailedLocalLock();
        ctrl.replay();
        PermanentLockingException le = null;
        try {
            locker.writeLock(defaultLockID, defaultTx);// SUT

        } catch (PermanentLockingException e) {
            le = e;
        }
        Assert.assertNotNull(le);
    }

    /**
     * Claim a lock without errors using {@code defaultTx}, the check that
     * {@code otherTx} can't claim it, instead throwing a
     * TemporaryLockingException
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testWriteLockDetectsMultiTxContention() throws BackendException {
        // defaultTx
        // Check to see whether the lock was already written before anything else
        expect(lockState.has(defaultTx, defaultLockID)).andReturn(false);
        // Now lock it locally to block other threads in the process
        recordSuccessfulLocalLock();
        // Write a lock claim column to the store
        ConsistentKeyLockerTest.LockInfo li = recordSuccessfulLockWrite(1, ChronoUnit.NANOS, null);
        // Update the expiration timestamp of the local (thread-level) lock
        recordSuccessfulLocalLock(li.tsNS);
        // Store the taken lock's key, column, and timestamp in the lockState map
        lockState.take(eq(defaultTx), eq(defaultLockID), eq(li.stat));
        // otherTx
        // Check to see whether the lock was already written before anything else
        expect(lockState.has(otherTx, defaultLockID)).andReturn(false);
        // Now try to take the lock but fail because defaultTX has it
        recordFailedLocalLock(otherTx);
        ctrl.replay();
        locker.writeLock(defaultLockID, defaultTx);// SUT

        PermanentLockingException le = null;
        try {
            locker.writeLock(defaultLockID, otherTx);// SUT

        } catch (PermanentLockingException e) {
            le = e;
        }
        Assert.assertNotNull(le);
    }

    /**
     * Test that multiple calls to
     * {@link ConsistentKeyLocker#writeLock(KeyColumn, StoreTransaction)} with
     * the same arguments have no effect after the first call (until
     * {@link ConsistentKeyLocker#deleteLocks(StoreTransaction)} is called).
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testWriteLockIdempotence() throws BackendException {
        expect(lockState.has(defaultTx, defaultLockID)).andReturn(false);
        recordSuccessfulLocalLock();
        ConsistentKeyLockerTest.LockInfo li = recordSuccessfulLockWrite(1, ChronoUnit.NANOS, null);
        recordSuccessfulLocalLock(li.tsNS);
        lockState.take(eq(defaultTx), eq(defaultLockID), eq(li.stat));
        ctrl.replay();
        locker.writeLock(defaultLockID, defaultTx);
        ctrl.verify();
        ctrl.reset();
        expect(lockState.has(defaultTx, defaultLockID)).andReturn(true);
        ctrl.replay();
        locker.writeLock(defaultLockID, defaultTx);
    }

    /**
     * Test a single checking a single lock under optimal conditions (no
     * timeouts, no errors)
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     * @throws InterruptedException
     * 		shouldn't happen
     */
    @Test
    public void testCheckLocksInSimplestCase() throws BackendException, InterruptedException {
        // Fake a pre-existing lock
        final ConsistentKeyLockStatus ls = makeStatusNow();
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.of(defaultLockID, ls));
        currentTimeNS = currentTimeNS.plusSeconds(10);
        // Checker should compare the fake lock's timestamp to the current time
        expectSleepAfterWritingLock(ls);
        // Expect a store getSlice() and return the fake lock's column and value
        recordLockGetSliceAndReturnSingleEntry(StaticArrayEntry.of(codec.toLockCol(ls.getWriteTimestamp(), defaultLockRid, times), defaultLockVal));
        ctrl.replay();
        locker.checkLocks(defaultTx);
    }

    /**
     * A transaction that writes a lock, waits past expiration, and attempts
     * to check locks should receive an {@code ExpiredLockException} during
     * the check stage.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testCheckOwnExpiredLockThrowsException() throws BackendException, InterruptedException {
        // Fake a pre-existing lock that's long since expired
        final ConsistentKeyLockStatus expired = makeStatusNow();
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.of(defaultLockID, expired));
        currentTimeNS = currentTimeNS.plus(100, ChronoUnit.DAYS);// pretend a huge multiple of the expiration time has passed

        // Checker should compare the fake lock's timestamp to the current time
        expectSleepAfterWritingLock(expired);
        // Checker must slice the store; we return the single expired lock column
        recordLockGetSliceAndReturnSingleEntry(StaticArrayEntry.of(codec.toLockCol(expired.getWriteTimestamp(), defaultLockRid, times), defaultLockVal));
        ctrl.replay();
        ExpiredLockException ele = null;
        try {
            locker.checkLocks(defaultTx);
        } catch (ExpiredLockException e) {
            ele = e;
        }
        Assert.assertNotNull(ele);
    }

    /**
     * A transaction that detects expired locks from other transactions, or from
     * its own transaction but with a different timestamp than the one currently
     * stored in memory by the transaction (presumably from an earlier attempt),
     * should be ignored.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testCheckLocksIgnoresOtherExpiredLocks() throws BackendException, InterruptedException {
        // Fake a pre-existing lock from a different tx that's long since expired
        final ConsistentKeyLockStatus otherExpired = makeStatusNow();
        // Fake a pre-existing lock from our tx
        final ConsistentKeyLockStatus ownExpired = makeStatusNow();
        currentTimeNS = currentTimeNS.plus(100, ChronoUnit.DAYS);// pretend a huge multiple of the expiration time has passed

        // Create a still-valid lock belonging to the default tx
        final ConsistentKeyLockStatus recent = makeStatusNow();
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.of(defaultLockID, recent));
        currentTimeNS = currentTimeNS.plusMillis(1);
        expectSleepAfterWritingLock(recent);
        // Checker must slice the store; return both of the expired claims and the one active claim
        recordLockGetSlice(StaticArrayEntryList.of(StaticArrayEntry.of(codec.toLockCol(otherExpired.getWriteTimestamp(), otherLockRid, times), defaultLockVal), StaticArrayEntry.of(codec.toLockCol(ownExpired.getWriteTimestamp(), defaultLockRid, times), defaultLockVal), StaticArrayEntry.of(codec.toLockCol(recent.getWriteTimestamp(), defaultLockRid, times), defaultLockVal)));
        ctrl.replay();
        locker.checkLocks(defaultTx);
    }

    /**
     * Each written lock should be checked at most once. Test this by faking a
     * single previously written lock using mocks and stubs and then calling
     * checkLocks() twice. The second call should have no effect.
     *
     * @throws InterruptedException
     * 		shouldn't happen
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testCheckLocksIdempotence() throws BackendException, InterruptedException {
        // Fake a pre-existing valid lock
        final ConsistentKeyLockStatus ls = makeStatusNow();
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.of(defaultLockID, ls));
        currentTimeNS = currentTimeNS.plusSeconds(10);
        expectSleepAfterWritingLock(ls);
        final StaticBuffer lc = codec.toLockCol(ls.getWriteTimestamp(), defaultLockRid, times);
        recordLockGetSliceAndReturnSingleEntry(StaticArrayEntry.of(lc, defaultLockVal));
        ctrl.replay();
        locker.checkLocks(defaultTx);
        ctrl.verify();
        ctrl.reset();
        // Return the faked lock in a map of size 1
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.of(defaultLockID, ls));
        ctrl.replay();
        // At this point, checkLocks() should see that the single lock in the
        // map returned above has already been checked and return immediately
        locker.checkLocks(defaultTx);
    }

    /**
     * If the checker reads its own lock column preceeded by a lock column from
     * another rid with an earlier timestamp and the timestamps on both columns
     * are unexpired, then the checker must throw a TemporaryLockingException.
     *
     * @throws InterruptedException
     * 		shouldn't happen
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen (we expect a TemporaryLockingException but
     * 		we catch and swallow it)
     */
    @Test
    public void testCheckLocksFailsWithSeniorClaimsByOthers() throws BackendException, InterruptedException {
        // Make a pre-existing valid lock by some other tx (written by another process)
        StaticBuffer otherSeniorLockCol = codec.toLockCol(currentTimeNS, otherLockRid, times);
        currentTimeNS = currentTimeNS.plusNanos(1);
        // Expect checker to fetch locks for defaultTx; return just our own lock (not the other guy's)
        StaticBuffer ownJuniorLockCol = codec.toLockCol(currentTimeNS, defaultLockRid, times);
        ConsistentKeyLockStatus ownJuniorLS = makeStatusNow();
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.of(defaultLockID, ownJuniorLS));
        currentTimeNS = currentTimeNS.plusSeconds(10);
        // Return defaultTx's lock in a map when requested
        expectSleepAfterWritingLock(ownJuniorLS);
        // When the checker slices the store, return the senior lock col by a
        // foreign tx and the junior lock col by defaultTx (in that order)
        recordLockGetSlice(StaticArrayEntryList.of(StaticArrayEntry.of(otherSeniorLockCol, defaultLockVal), StaticArrayEntry.of(ownJuniorLockCol, defaultLockVal)));
        ctrl.replay();
        TemporaryLockingException tle = null;
        try {
            locker.checkLocks(defaultTx);
        } catch (TemporaryLockingException e) {
            tle = e;
        }
        Assert.assertNotNull(tle);
    }

    /**
     * When the checker retrieves its own lock column followed by a lock column
     * with a later timestamp (both with unexpired timestamps), it should
     * consider the lock successfully checked.
     *
     * @throws InterruptedException
     * 		shouldn't happen
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testCheckLocksSucceedsWithJuniorClaimsByOthers() throws BackendException, InterruptedException {
        // Expect checker to fetch locks for defaultTx; return just our own lock (not the other guy's)
        StaticBuffer ownSeniorLockCol = codec.toLockCol(currentTimeNS, defaultLockRid, times);
        ConsistentKeyLockStatus ownSeniorLS = makeStatusNow();
        currentTimeNS = currentTimeNS.plusNanos(1);
        // Make junior lock
        StaticBuffer otherJuniorLockCol = codec.toLockCol(currentTimeNS, otherLockRid, times);
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.of(defaultLockID, ownSeniorLS));
        currentTimeNS = currentTimeNS.plusSeconds(10);
        // Return defaultTx's lock in a map when requested
        expectSleepAfterWritingLock(ownSeniorLS);
        // When the checker slices the store, return the senior lock col by a
        // foreign tx and the junior lock col by defaultTx (in that order)
        recordLockGetSlice(StaticArrayEntryList.of(StaticArrayEntry.of(ownSeniorLockCol, defaultLockVal), StaticArrayEntry.of(otherJuniorLockCol, defaultLockVal)));
        ctrl.replay();
        locker.checkLocks(defaultTx);
    }

    /**
     * If the checker retrieves a timestamp-ordered list of columns, where the
     * list starts with an unbroken series of columns with the checker's rid but
     * differing timestamps, then consider the lock successfully checked if the
     * checker's expected timestamp occurs anywhere in that series of columns.
     * <p/>
     * This relaxation of the normal checking rules only triggers when either
     * writeLock(...) issued mutate calls that appeared to fail client-side but
     * which actually succeeded (e.g. hinted handoff or timeout)
     *
     * @throws InterruptedException
     * 		shouldn't happen
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testCheckLocksSucceedsWithSeniorAndJuniorClaimsBySelf() throws BackendException, InterruptedException {
        // Setup three lock columns differing only in timestamp
        StaticBuffer myFirstLockCol = codec.toLockCol(currentTimeNS, defaultLockRid, times);
        currentTimeNS = currentTimeNS.plusNanos(1);
        StaticBuffer mySecondLockCol = codec.toLockCol(currentTimeNS, defaultLockRid, times);
        ConsistentKeyLockStatus mySecondLS = makeStatusNow();
        currentTimeNS = currentTimeNS.plusNanos(1);
        StaticBuffer myThirdLockCol = codec.toLockCol(currentTimeNS, defaultLockRid, times);
        currentTimeNS = currentTimeNS.plusNanos(1);
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.of(defaultLockID, mySecondLS));
        // Return defaultTx's second lock in a map when requested
        currentTimeNS = currentTimeNS.plusSeconds(10);
        expectSleepAfterWritingLock(mySecondLS);
        // When the checker slices the store, return the senior lock col by a
        // foreign tx and the junior lock col by defaultTx (in that order)
        recordLockGetSlice(StaticArrayEntryList.of(StaticArrayEntry.of(myFirstLockCol, defaultLockVal), StaticArrayEntry.of(mySecondLockCol, defaultLockVal), StaticArrayEntry.of(myThirdLockCol, defaultLockVal)));
        ctrl.replay();
        locker.checkLocks(defaultTx);
    }

    /**
     * The checker should retry getSlice() in the face of a
     * TemporaryStorageException so long as the number of exceptional
     * getSlice()s is fewer than the lock retry count. The retry count applies
     * on a per-lock basis.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     * @throws InterruptedException
     * 		shouldn't happen
     */
    @Test
    public void testCheckLocksRetriesAfterSingleTemporaryStorageException() throws BackendException, InterruptedException {
        // Setup one lock column
        StaticBuffer lockCol = codec.toLockCol(currentTimeNS, defaultLockRid, times);
        ConsistentKeyLockStatus lockStatus = makeStatusNow();
        currentTimeNS = currentTimeNS.plusNanos(1);
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.of(defaultLockID, lockStatus));
        expectSleepAfterWritingLock(lockStatus);
        // First getSlice will fail
        TemporaryBackendException tse = new TemporaryBackendException("Storage cluster will be right back");
        recordExceptionalLockGetSlice(tse);
        // Second getSlice will succeed
        recordLockGetSliceAndReturnSingleEntry(StaticArrayEntry.of(lockCol, defaultLockVal));
        ctrl.replay();
        locker.checkLocks(defaultTx);
        // TODO run again with two locks instead of one and show that the retry count applies on a per-lock basis
    }

    /**
     * The checker will throw a TemporaryStorageException if getSlice() throws
     * fails with a TemporaryStorageException as many times as there are
     * configured lock retries.
     *
     * @throws InterruptedException
     * 		shouldn't happen
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testCheckLocksThrowsExceptionAfterMaxTemporaryStorageExceptions() throws BackendException, InterruptedException {
        // Setup a LockStatus for defaultLockID
        ConsistentKeyLockStatus lockStatus = makeStatusNow();
        currentTimeNS = currentTimeNS.plusNanos(1);
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.of(defaultLockID, lockStatus));
        expectSleepAfterWritingLock(lockStatus);
        // Three successive getSlice calls, each throwing a distinct TSE
        recordExceptionalLockGetSlice(new TemporaryBackendException("Storage cluster is having me-time"));
        recordExceptionalLockGetSlice(new TemporaryBackendException("Storage cluster is in a dissociative fugue state"));
        recordExceptionalLockGetSlice(new TemporaryBackendException("Storage cluster has gone to Prague to find itself"));
        ctrl.replay();
        TemporaryBackendException tse = null;
        try {
            locker.checkLocks(defaultTx);
        } catch (TemporaryBackendException e) {
            tse = e;
        }
        Assert.assertNotNull(tse);
    }

    /**
     * A single PermanentStorageException on getSlice() for a single lock is
     * sufficient to make the method return immediately (regardless of whether
     * other locks are waiting to be checked).
     *
     * @throws InterruptedException
     * 		shouldn't happen
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testCheckLocksDiesOnPermanentStorageException() throws BackendException, InterruptedException {
        // Setup a LockStatus for defaultLockID
        ConsistentKeyLockStatus lockStatus = makeStatusNow();
        currentTimeNS = currentTimeNS.plusNanos(1);
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.of(defaultLockID, lockStatus));
        expectSleepAfterWritingLock(lockStatus);
        // First and only getSlice call throws a PSE
        recordExceptionalLockGetSlice(new PermanentBackendException("Connection to storage cluster failed: peer is an IPv6 toaster"));
        ctrl.replay();
        PermanentBackendException pse = null;
        try {
            locker.checkLocks(defaultTx);
        } catch (PermanentBackendException e) {
            pse = e;
        }
        Assert.assertNotNull(pse);
    }

    /**
     * The lock checker should do nothing when passed a transaction for which it
     * holds no locks.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testCheckLocksDoesNothingForUnrecognizedTransaction() throws BackendException {
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.<KeyColumn, ConsistentKeyLockStatus>of());
        ctrl.replay();
        locker.checkLocks(defaultTx);
    }

    /**
     * Delete a single lock without any timeouts, errors, etc.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testDeleteLocksInSimplestCase() throws BackendException {
        // Setup a LockStatus for defaultLockID
        final ConsistentKeyLockStatus lockStatus = makeStatusNow();
        currentTimeNS = currentTimeNS.plusNanos(1);
        @SuppressWarnings("serial")
        Map<KeyColumn, ConsistentKeyLockStatus> expectedMap = new HashMap<KeyColumn, ConsistentKeyLockStatus>() {
            {
                put(defaultLockID, lockStatus);
            }
        };
        expect(lockState.getLocksForTx(defaultTx)).andReturn(expectedMap);
        List<StaticBuffer> dels = ImmutableList.of(codec.toLockCol(lockStatus.getWriteTimestamp(), defaultLockRid, times));
        expect(times.getTime()).andReturn(currentTimeNS);
        store.mutate(eq(defaultLockKey), eq(ImmutableList.<Entry>of()), eq(dels), eq(defaultTx));
        expect(mediator.unlock(defaultLockID, defaultTx)).andReturn(true);
        ctrl.replay();
        locker.deleteLocks(defaultTx);
    }

    /**
     * Delete two locks without any timeouts, errors, etc.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testDeleteLocksOnTwoLocks() throws BackendException {
        ConsistentKeyLockStatus defaultLS = makeStatusNow();
        currentTimeNS = currentTimeNS.plusNanos(1);
        ConsistentKeyLockStatus otherLS = makeStatusNow();
        currentTimeNS = currentTimeNS.plusNanos(1);
        // Expect a call for defaultTx's locks and return two
        Map<KeyColumn, ConsistentKeyLockStatus> expectedMap = Maps.newLinkedHashMap();
        expectedMap.put(defaultLockID, defaultLS);
        expectedMap.put(otherLockID, otherLS);
        expect(lockState.getLocksForTx(defaultTx)).andReturn(expectedMap);
        expectLockDeleteSuccessfully(defaultLockID, defaultLockKey, defaultLS);
        expectLockDeleteSuccessfully(otherLockID, otherLockKey, otherLS);
        ctrl.replay();
        locker.deleteLocks(defaultTx);
    }

    /**
     * Lock deletion should retry if the first store mutation throws a temporary
     * exception.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testDeleteLocksRetriesOnTemporaryStorageException() throws BackendException {
        ConsistentKeyLockStatus defaultLS = makeStatusNow();
        currentTimeNS = currentTimeNS.plusNanos(1);
        expect(lockState.getLocksForTx(defaultTx)).andReturn(Maps.newLinkedHashMap(ImmutableMap.of(defaultLockID, defaultLS)));
        expectDeleteLock(defaultLockID, defaultLockKey, defaultLS, new TemporaryBackendException("Storage cluster is backlogged"));
        ctrl.replay();
        locker.deleteLocks(defaultTx);
    }

    /**
     * If lock deletion exceeds the temporary exception retry count when trying
     * to delete a lock, it should move onto the next lock rather than returning
     * and potentially leaving the remaining locks undeleted.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testDeleteLocksSkipsToNextLockAfterMaxTemporaryStorageExceptions() throws BackendException {
        ConsistentKeyLockStatus defaultLS = makeStatusNow();
        currentTimeNS = currentTimeNS.plusNanos(1);
        expect(lockState.getLocksForTx(defaultTx)).andReturn(Maps.newLinkedHashMap(ImmutableMap.of(defaultLockID, defaultLS)));
        expectDeleteLock(defaultLockID, defaultLockKey, defaultLS, new TemporaryBackendException("Storage cluster is busy"), new TemporaryBackendException("Storage cluster is busier"), new TemporaryBackendException("Storage cluster has reached peak business"));
        ctrl.replay();
        locker.deleteLocks(defaultTx);
    }

    /**
     * Same as
     * {@link #testDeleteLocksSkipsToNextLockAfterMaxTemporaryStorageExceptions()}
     * , except instead of exceeding the temporary exception retry count on a
     * lock, that lock throws a single permanent exception.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shoudn't happen
     */
    @Test
    public void testDeleteLocksSkipsToNextLockOnPermanentStorageException() throws BackendException {
        ConsistentKeyLockStatus defaultLS = makeStatusNow();
        currentTimeNS = currentTimeNS.plusNanos(1);
        expect(lockState.getLocksForTx(defaultTx)).andReturn(Maps.newLinkedHashMap(ImmutableMap.of(defaultLockID, defaultLS)));
        expectDeleteLock(defaultLockID, defaultLockKey, defaultLS, new PermanentBackendException("Storage cluster has been destroyed by a tornado"));
        ctrl.replay();
        locker.deleteLocks(defaultTx);
    }

    /**
     * Deletion should remove previously written locks regardless of whether
     * they were ever checked; this method fakes and verifies deletion on a
     * single unchecked lock
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testDeleteLocksDeletesUncheckedLocks() throws BackendException {
        ConsistentKeyLockStatus defaultLS = makeStatusNow();
        Assert.assertFalse(defaultLS.isChecked());
        currentTimeNS = currentTimeNS.plusNanos(1);
        // Expect a call for defaultTx's locks and the checked one
        expect(lockState.getLocksForTx(defaultTx)).andReturn(Maps.newLinkedHashMap(ImmutableMap.of(defaultLockID, defaultLS)));
        expectLockDeleteSuccessfully(defaultLockID, defaultLockKey, defaultLS);
        ctrl.replay();
        locker.deleteLocks(defaultTx);
    }

    /**
     * When delete is called multiple times with no intervening write or check
     * calls, all calls after the first should have no effect.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testDeleteLocksIdempotence() throws BackendException {
        // Setup a LockStatus for defaultLockID
        ConsistentKeyLockStatus lockStatus = makeStatusNow();
        currentTimeNS = currentTimeNS.plusNanos(1);
        expect(lockState.getLocksForTx(defaultTx)).andReturn(Maps.newLinkedHashMap(ImmutableMap.of(defaultLockID, lockStatus)));
        expectLockDeleteSuccessfully(defaultLockID, defaultLockKey, lockStatus);
        ctrl.replay();
        locker.deleteLocks(defaultTx);
        ctrl.verify();
        ctrl.reset();
        expect(lockState.getLocksForTx(defaultTx)).andReturn(Maps.newLinkedHashMap(ImmutableMap.<KeyColumn, ConsistentKeyLockStatus>of()));
        ctrl.replay();
        locker.deleteLocks(defaultTx);
    }

    /**
     * Delete should do nothing when passed a transaction for which it holds no
     * locks.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testDeleteLocksDoesNothingForUnrecognizedTransaction() throws BackendException {
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.<KeyColumn, ConsistentKeyLockStatus>of());
        ctrl.replay();
        locker.deleteLocks(defaultTx);
    }

    /**
     * Checking locks when the expired lock cleaner is enabled should trigger
     * one call to the LockCleanerService.
     *
     * @throws com.thinkaurelius.titan.diskstorage.BackendException
     * 		shouldn't happen
     */
    @Test
    public void testCleanExpiredLock() throws BackendException, InterruptedException {
        LockCleanerService mockCleaner = ctrl.createMock(LockCleanerService.class);
        ctrl.replay();
        Locker altLocker = getDefaultBuilder().customCleaner(mockCleaner).build();
        ctrl.verify();
        ctrl.reset();
        final ConsistentKeyLockStatus expired = makeStatusNow();
        expect(lockState.getLocksForTx(defaultTx)).andReturn(ImmutableMap.of(defaultLockID, expired));
        currentTimeNS = currentTimeNS.plus(100, ChronoUnit.DAYS);// pretend a huge multiple of the expiration time has passed

        // Checker should compare the fake lock's timestamp to the current time
        expect(times.sleepPast(expired.getWriteTimestamp().plus(defaultWaitNS))).andReturn(currentTimeNS);
        // Checker must slice the store; we return the single expired lock column
        recordLockGetSliceAndReturnSingleEntry(StaticArrayEntry.of(codec.toLockCol(expired.getWriteTimestamp(), defaultLockRid, times), defaultLockVal));
        // Checker must attempt to cleanup expired lock
        mockCleaner.clean(eq(defaultLockID), eq(currentTimeNS.minus(defaultExpireNS)), eq(defaultTx));
        expectLastCall().once();
        ctrl.replay();
        TemporaryLockingException ple = null;
        try {
            altLocker.checkLocks(defaultTx);
        } catch (TemporaryLockingException e) {
            ple = e;
        }
        Assert.assertNotNull(ple);
    }

    /* Helpers */
    public static class LockInfo {
        private final Instant tsNS;

        private final ConsistentKeyLockStatus stat;

        private final StaticBuffer col;

        public LockInfo(Instant tsNS, ConsistentKeyLockStatus stat, StaticBuffer col) {
            this.tsNS = tsNS;
            this.stat = stat;
            this.col = col;
        }
    }

    /* This class supports partial mocking of TimestampProvider.

    It's impossible to mock Timestamps.NANO because that is an enum;
    EasyMock will fail to do it at runtime with cryptic
    "incompatible return value type" exceptions.
     */
    private static class FakeTimestampProvider implements TimestampProvider {
        @Override
        public Instant getTime() {
            throw new IllegalStateException();
        }

        @Override
        public Instant getTime(long sinceEpoch) {
            return Instant.ofEpochSecond(0, sinceEpoch);
        }

        @Override
        public ChronoUnit getUnit() {
            return ChronoUnit.NANOS;
        }

        @Override
        public Instant sleepPast(Instant futureTime) throws InterruptedException {
            throw new IllegalStateException();
        }

        @Override
        public void sleepFor(Duration duration) throws InterruptedException {
            throw new IllegalStateException();
        }

        @Override
        public Timer getTimer() {
            return new Timer(this);
        }

        @Override
        public long getTime(Instant timestamp) {
            return ((timestamp.getEpochSecond()) * 1000000000L) + (timestamp.getNano());
        }
    }
}

