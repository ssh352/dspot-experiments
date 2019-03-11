/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud;


import BatchResult.Callback;
import com.google.cloud.BaseServiceException.ExceptionData;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class BatchResultTest {
    private BatchResult<Boolean, BaseServiceException> result;

    @Test
    public void testSuccess() {
        Assert.assertFalse(result.completed());
        try {
            result.get();
            Assert.fail("This was not completed yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        result.success(true);
        Assert.assertTrue(result.get());
        // test that null is allowed
        result.success(null);
    }

    @Test
    public void testError() {
        Assert.assertFalse(result.completed());
        try {
            result.get();
            Assert.fail("This was not completed yet.");
        } catch (IllegalStateException ex) {
            // expected
        }
        try {
            result.error(null);
            Assert.fail();
        } catch (NullPointerException exc) {
            // expected
        }
        BaseServiceException ex = new BaseServiceException(ExceptionData.from(0, "message", "reason", false));
        result.error(ex);
        try {
            result.get();
            Assert.fail("This is a failed operation and should have thrown a DnsException.");
        } catch (BaseServiceException real) {
            Assert.assertSame(ex, real);
        }
    }

    @Test
    public void testNotifyError() {
        final BaseServiceException ex = new BaseServiceException(ExceptionData.from(0, "message", "reason", false));
        Assert.assertFalse(result.completed());
        Callback<Boolean, BaseServiceException> callback = EasyMock.createStrictMock(Callback.class);
        callback.error(ex);
        EasyMock.replay(callback);
        result.notify(callback);
        result.error(ex);
        try {
            result.notify(callback);
            Assert.fail("The batch has been completed.");
        } catch (IllegalStateException exception) {
            // expected
        }
        EasyMock.verify(callback);
    }

    @Test
    public void testNotifySuccess() {
        Assert.assertFalse(result.completed());
        Callback<Boolean, BaseServiceException> callback = EasyMock.createStrictMock(Callback.class);
        callback.success(true);
        EasyMock.replay(callback);
        result.notify(callback);
        result.success(true);
        try {
            result.notify(callback);
            Assert.fail("The batch has been completed.");
        } catch (IllegalStateException exception) {
            // expected
        }
        EasyMock.verify(callback);
    }
}

