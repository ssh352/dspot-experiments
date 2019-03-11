/**
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.nio.channels;


import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import junit.framework.TestCase;


public class PipeTest extends TestCase {
    public void test_readEmptyPipe() throws Exception {
        Pipe p = Pipe.open();
        p.source().configureBlocking(false);
        TestCase.assertEquals(0, p.source().read(ByteBuffer.allocate(1)));
    }
}

