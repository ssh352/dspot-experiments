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


import RollCycles.TEST_DAILY;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.threads.ThreadDump;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.junit.Assert;
import org.junit.Test;


/* Created by Peter Lawrey on 15/03/16. */
public class ReadmeTest {
    private ThreadDump threadDump;

    @Test
    public void createAQueue() {
        final String basePath = ((((OS.TARGET) + "/") + (getClass().getSimpleName())) + "-") + (System.nanoTime());
        try (ChronicleQueue queue = SingleChronicleQueueBuilder.single(basePath).testBlockSize().rollCycle(TEST_DAILY).build()) {
            // Obtain an ExcerptAppender
            ExcerptAppender appender = queue.acquireAppender();
            // write - {msg: TestMessage}
            appender.writeDocument(( w) -> w.write(() -> "msg").text("TestMessage"));
            // System.out.println(queue.dump());
            // write - TestMessage
            appender.writeText("TestMessage");
            ExcerptTailer tailer = queue.createTailer();
            tailer.readDocument(( w) -> System.out.println(("msg: " + (w.read(() -> "msg").text()))));
            Assert.assertEquals("TestMessage", tailer.readText());
        }
    }
}

