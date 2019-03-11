/**
 * Copyright (c) 2015, Cloudera and Intel, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.common.lang;


import com.cloudera.oryx.common.OryxTest;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.junit.Assert;
import org.junit.Test;


public final class AutoReadWriteLockTest extends OryxTest {
    @Test
    public void testDefault() throws Exception {
        AutoReadWriteLock al = new AutoReadWriteLock();
        Assert.assertNotNull(al.toString());
        Assert.assertNotNull(al.readLock());
        Assert.assertNotNull(al.writeLock());
    }

    @Test
    public void testReadLock() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        AutoReadWriteLock al = new AutoReadWriteLock(lock);
        Assert.assertEquals(0, lock.getReadLockCount());
        try (AutoLock al2 = al.autoReadLock()) {
            Assert.assertEquals(1, lock.getReadLockCount());
        }
        Assert.assertEquals(0, lock.getReadLockCount());
    }

    @Test
    public void testWriteLock() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        AutoReadWriteLock al = new AutoReadWriteLock(lock);
        Assert.assertFalse(lock.isWriteLocked());
        try (AutoLock al2 = al.autoWriteLock()) {
            Assert.assertTrue(lock.isWriteLocked());
        }
        Assert.assertFalse(lock.isWriteLocked());
    }
}

