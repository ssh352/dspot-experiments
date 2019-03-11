/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.s3a;


import S3AUtils.ENDPOINT_KEY;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import org.apache.http.conn.ConnectTimeoutException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test suite covering translation of AWS SDK exceptions to S3A exceptions,
 * and retry/recovery policies.
 */
@SuppressWarnings("ThrowableNotThrown")
public class TestS3AExceptionTranslation {
    private static final ConnectTimeoutException HTTP_CONNECTION_TIMEOUT_EX = new ConnectTimeoutException("apache");

    private static final SocketTimeoutException SOCKET_TIMEOUT_EX = new SocketTimeoutException("socket");

    @Test
    public void test301ContainsEndpoint() throws Exception {
        String bucket = "bucket.s3-us-west-2.amazonaws.com";
        int sc301 = 301;
        AmazonS3Exception s3Exception = TestS3AExceptionTranslation.createS3Exception("wrong endpoint", sc301, Collections.singletonMap(ENDPOINT_KEY, bucket));
        AWSRedirectException ex = verifyTranslated(AWSRedirectException.class, s3Exception);
        assertStatusCode(sc301, ex);
        Assert.assertNotNull(ex.getMessage());
        assertContained(ex.getMessage(), bucket);
        assertContained(ex.getMessage(), ENDPOINT);
        assertExceptionContains(ENDPOINT, ex, "endpoint");
        assertExceptionContains(bucket, ex, "bucket name");
    }

    @Test
    public void test400isBad() throws Exception {
        verifyTranslated(400, AWSBadRequestException.class);
    }

    @Test
    public void test401isNotPermittedFound() throws Exception {
        verifyTranslated(401, AccessDeniedException.class);
    }

    @Test
    public void test403isNotPermittedFound() throws Exception {
        verifyTranslated(403, AccessDeniedException.class);
    }

    @Test
    public void test404isNotFound() throws Exception {
        verifyTranslated(404, FileNotFoundException.class);
    }

    @Test
    public void test410isNotFound() throws Exception {
        verifyTranslated(410, FileNotFoundException.class);
    }

    @Test
    public void test416isEOF() throws Exception {
        verifyTranslated(416, EOFException.class);
    }

    @Test
    public void testGenericS3Exception() throws Exception {
        // S3 exception of no known type
        AWSS3IOException ex = verifyTranslated(AWSS3IOException.class, TestS3AExceptionTranslation.createS3Exception(451));
        assertStatusCode(451, ex);
    }

    @Test
    public void testGenericServiceS3Exception() throws Exception {
        // service exception of no known type
        AmazonServiceException ase = new AmazonServiceException("unwind");
        ase.setStatusCode(500);
        AWSServiceIOException ex = verifyTranslated(AWSStatus500Exception.class, ase);
        assertStatusCode(500, ex);
    }

    @Test
    public void testGenericClientException() throws Exception {
        // Generic Amazon exception
        verifyTranslated(AWSClientIOException.class, new AmazonClientException(""));
    }

    @Test
    public void testInterruptExceptionDetecting() throws Throwable {
        InterruptedException interrupted = new InterruptedException("irq");
        assertContainsInterrupted(true, interrupted);
        IOException ioe = new IOException("ioe");
        assertContainsInterrupted(false, ioe);
        assertContainsInterrupted(true, ioe.initCause(interrupted));
        assertContainsInterrupted(true, new InterruptedIOException("ioirq"));
    }

    @Test(expected = InterruptedIOException.class)
    public void testExtractInterrupted() throws Throwable {
        throw extractException("", "", new ExecutionException(new AmazonClientException(new InterruptedException(""))));
    }

    @Test(expected = InterruptedIOException.class)
    public void testExtractInterruptedIO() throws Throwable {
        throw extractException("", "", new ExecutionException(new AmazonClientException(new InterruptedIOException(""))));
    }
}

