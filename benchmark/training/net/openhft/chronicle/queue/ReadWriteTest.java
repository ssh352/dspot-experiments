/**
 * Copyright 2016 higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.chronicle.queue;


import java.io.File;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Assert;
import org.junit.Test;


/* Created by Jerry Shea on 14/08/16. */
@RequiredForClient
public class ReadWriteTest {
    private static final String STR1 = "hello";

    private static final String STR2 = "hey";

    private File chroniclePath;

    @Test
    public void testReadFromReadOnlyChronicle() {
        try (ChronicleQueue out = SingleChronicleQueueBuilder.binary(chroniclePath).testBlockSize().readOnly((!(OS.isWindows()))).build()) {
            // check dump
            Assert.assertTrue(((out.dump().length()) > 1));
            // and tailer
            ExcerptTailer tailer = out.createTailer();
            Assert.assertEquals(ReadWriteTest.STR1, tailer.readText());
            try (DocumentContext dc = tailer.readingDocument()) {
                Assert.assertEquals(ReadWriteTest.STR2, dc.wire().bytes().readUtf8());
                // even though this is read-only we can still call dc.wire().bytes().write... which causes java.lang.InternalError
                // Fixing this in a type-safe manner would require on Read/WriteDocumentContext to return WireIn/WireOut
            }
        }
    }

    // Can't append to a read-only chronicle
    @Test(expected = IllegalStateException.class)
    public void testWriteToReadOnlyChronicle() {
        if (OS.isWindows()) {
            System.err.println("#460 Cannot test read only mode on windows");
            throw new IllegalStateException("not run");
        }
        try (ChronicleQueue out = SingleChronicleQueueBuilder.binary(chroniclePath).testBlockSize().readOnly(true).build()) {
            out.acquireAppender();
        }
    }

    @Test
    public void testToEndOnReadOnly() {
        try (ChronicleQueue out = SingleChronicleQueueBuilder.binary(chroniclePath).testBlockSize().readOnly(true).build()) {
            ExcerptTailer tailer = out.createTailer();
            tailer.toEnd();
            long index = tailer.index();
            Assert.assertTrue((index != 0));
        }
    }
}

