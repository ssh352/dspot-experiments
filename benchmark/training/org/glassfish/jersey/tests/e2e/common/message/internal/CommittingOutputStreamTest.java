/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.tests.e2e.common.message.internal;


import CommittingOutputStream.DEFAULT_BUFFER_SIZE;
import CommonProperties.OUTBOUND_CONTENT_LENGTH_BUFFER;
import RuntimeType.CLIENT;
import RuntimeType.SERVER;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.RuntimeType;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.message.internal.CommittingOutputStream;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the {@link CommittingOutputStream}.
 *
 * @author Miroslav Fuksa
 */
public class CommittingOutputStreamTest {
    private static class Passed {
        boolean b;

        public void pass() {
            b = true;
        }
    }

    @Test
    public void testExactSizeOfBuffer() throws IOException {
        final CommittingOutputStreamTest.Passed passed = new CommittingOutputStreamTest.Passed();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
        CommittingOutputStream cos = new CommittingOutputStream();
        setupBufferedStreamProvider(passed, baos, cos, 3);
        cos.write(((byte) (1)));
        cos.write(((byte) (2)));
        cos.write(((byte) (3)));
        checkNotYetCommitted(passed, baos, cos);
        cos.commit();
        check(baos, new byte[]{ 1, 2, 3 });
        Assert.assertTrue(passed.b);
        cos.close();
    }

    @Test
    public void testExactSizeOfBufferByClose() throws IOException {
        final CommittingOutputStreamTest.Passed passed = new CommittingOutputStreamTest.Passed();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
        CommittingOutputStream cos = new CommittingOutputStream();
        setupBufferedStreamProvider(passed, baos, cos, 3);
        cos.write(((byte) (1)));
        cos.write(((byte) (2)));
        cos.write(((byte) (3)));
        checkNotYetCommitted(passed, baos, cos);
        cos.close();
        check(baos, new byte[]{ 1, 2, 3 });
        Assert.assertTrue(passed.b);
    }

    @Test
    public void testLessBytesThanLimit() throws IOException {
        final CommittingOutputStreamTest.Passed passed = new CommittingOutputStreamTest.Passed();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
        CommittingOutputStream cos = new CommittingOutputStream();
        setupBufferedStreamProvider(passed, baos, cos, 2);
        cos.write(((byte) (1)));
        cos.write(((byte) (2)));
        checkNotYetCommitted(passed, baos, cos);
        cos.commit();
        check(baos, new byte[]{ 1, 2 });
        cos.close();
        Assert.assertTrue(passed.b);
    }

    @Test
    public void testNoBytes() throws IOException {
        final CommittingOutputStreamTest.Passed passed = new CommittingOutputStreamTest.Passed();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
        CommittingOutputStream cos = new CommittingOutputStream();
        setupBufferedStreamProvider(passed, baos, cos, 0);
        checkNotYetCommitted(passed, baos, cos);
        cos.commit();
        check(baos, null);
        cos.close();
        Assert.assertTrue(passed.b);
    }

    @Test
    public void testBufferOverflow() throws IOException {
        final CommittingOutputStreamTest.Passed passed = new CommittingOutputStreamTest.Passed();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
        CommittingOutputStream cos = new CommittingOutputStream();
        setupBufferedStreamProvider(passed, baos, cos, (-1));
        cos.write(((byte) (1)));
        cos.write(((byte) (2)));
        cos.write(((byte) (3)));
        checkNotYetCommitted(passed, baos, cos);
        cos.write(((byte) (4)));
        check(baos, new byte[]{ 1, 2, 3, 4 });
        cos.write(((byte) (5)));
        check(baos, new byte[]{ 1, 2, 3, 4, 5 });
        cos.commit();
        check(baos, new byte[]{ 1, 2, 3, 4, 5 });
        cos.close();
    }

    @Test
    public void testNotBufferedOS() throws IOException {
        final CommittingOutputStreamTest.Passed passed = new CommittingOutputStreamTest.Passed();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
        CommittingOutputStream cos = new CommittingOutputStream();
        setupStreamProvider(passed, baos, cos);
        checkNotYetCommitted(passed, baos, cos);
        cos.write(((byte) (1)));
        checkCommitted(passed, cos);
        check(baos, new byte[]{ 1 });
        cos.write(((byte) (2)));
        checkCommitted(passed, cos);
        cos.write(((byte) (3)));
        cos.write(((byte) (4)));
        check(baos, new byte[]{ 1, 2, 3, 4 });
        cos.write(((byte) (5)));
        check(baos, new byte[]{ 1, 2, 3, 4, 5 });
        cos.commit();
        checkCommitted(passed, cos);
        check(baos, new byte[]{ 1, 2, 3, 4, 5 });
        cos.close();
    }

    @Test
    public void testPropertiesWithMessageContext() throws IOException {
        final int size = 20;
        Map<String, Object> properties = new HashMap<>();
        properties.put(OUTBOUND_CONTENT_LENGTH_BUFFER, size);
        final RuntimeType runtime = RuntimeType.CLIENT;
        checkBufferSize(size, properties, runtime);
    }

    @Test
    public void testPropertiesWithMessageContextVeryBigBuffer() throws IOException {
        final int size = 200000;
        Map<String, Object> properties = new HashMap<>();
        properties.put(OUTBOUND_CONTENT_LENGTH_BUFFER, size);
        final RuntimeType runtime = RuntimeType.CLIENT;
        checkBufferSize(size, properties, runtime);
    }

    @Test
    public void testPropertiesWithMessageContextMissingServerSpecific() throws IOException {
        final int size = 22;
        Map<String, Object> properties = new HashMap<>();
        properties.put(OUTBOUND_CONTENT_LENGTH_BUFFER, size);
        properties.put(((CommonProperties.OUTBOUND_CONTENT_LENGTH_BUFFER) + ".client"), (size * 2));
        checkBufferSize(size, properties, SERVER);
    }

    @Test
    public void testPropertiesWithMessageContextMissingServerAtAll() throws IOException {
        final int size = 22;
        Map<String, Object> properties = new HashMap<>();
        properties.put(PropertiesHelper.getPropertyNameForRuntime(OUTBOUND_CONTENT_LENGTH_BUFFER, CLIENT), size);
        checkBufferSize(DEFAULT_BUFFER_SIZE, properties, SERVER);
        checkBufferSize(size, properties, CLIENT);
    }

    @Test
    public void testPropertiesWithMessageContextClientOverrides() throws IOException {
        final int size = 22;
        Map<String, Object> properties = new HashMap<>();
        properties.put(OUTBOUND_CONTENT_LENGTH_BUFFER, size);
        properties.put(PropertiesHelper.getPropertyNameForRuntime(OUTBOUND_CONTENT_LENGTH_BUFFER, CLIENT), (size * 2));
        checkBufferSize((size * 2), properties, CLIENT);
        checkBufferSize(size, properties, SERVER);
    }

    @Test
    public void testPropertiesWithMessageContextDefaultNoProps() throws IOException {
        Map<String, Object> properties = new HashMap<>();
        final RuntimeType runtime = RuntimeType.CLIENT;
        checkBufferSize(DEFAULT_BUFFER_SIZE, properties, runtime);
    }

    @Test
    public void testEnableBuffering() {
        CommittingOutputStream cos = new CommittingOutputStream();
        cos.enableBuffering(500);
    }

    @Test
    public void testEnableBufferingIllegalStateException() throws IOException {
        CommittingOutputStream cos = new CommittingOutputStream();
        cos.setStreamProvider(new OutboundMessageContext.StreamProvider() {
            @Override
            public OutputStream getOutputStream(int contentLength) throws IOException {
                return null;
            }
        });
        cos.write('a');
        try {
            cos.enableBuffering(500);
            Assert.fail("should throw IllegalStateException because of late setup of enableBuffering when bytes are already written.");
        } catch (IllegalStateException e) {
            System.out.println(("this is ok - exception should be thrown: " + (e.getMessage())));
            // ok - should be thrown (bytes are already written).
        }
    }

    @Test
    public void testSetStramProviderIllegalStateException1() throws IOException {
        CommittingOutputStream cos = new CommittingOutputStream();
        cos.enableBuffering(1);
        writeAndCheckIllegalState(cos);
    }

    @Test
    public void testSetStramProviderIllegalStateException2() throws IOException {
        CommittingOutputStream cos = new CommittingOutputStream();
        writeAndCheckIllegalState(cos);
    }
}

