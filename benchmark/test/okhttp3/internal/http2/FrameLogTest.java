/**
 * Copyright (C) 2014 Square, Inc.
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
package okhttp3.internal.http2;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public final class FrameLogTest {
    /**
     * Real stream traffic applied to the log format.
     */
    @Test
    public void exampleStream() {
        Assert.assertEquals(">> 0x00000000     5 SETTINGS      ", Http2.frameLog(false, 0, 5, Http2.TYPE_SETTINGS, Http2.FLAG_NONE));
        Assert.assertEquals(">> 0x00000003   100 HEADERS       END_HEADERS", Http2.frameLog(false, 3, 100, Http2.TYPE_HEADERS, Http2.FLAG_END_HEADERS));
        Assert.assertEquals(">> 0x00000003     0 DATA          END_STREAM", Http2.frameLog(false, 3, 0, Http2.TYPE_DATA, Http2.FLAG_END_STREAM));
        Assert.assertEquals("<< 0x00000000    15 SETTINGS      ", Http2.frameLog(true, 0, 15, Http2.TYPE_SETTINGS, Http2.FLAG_NONE));
        Assert.assertEquals(">> 0x00000000     0 SETTINGS      ACK", Http2.frameLog(false, 0, 0, Http2.TYPE_SETTINGS, Http2.FLAG_ACK));
        Assert.assertEquals("<< 0x00000000     0 SETTINGS      ACK", Http2.frameLog(true, 0, 0, Http2.TYPE_SETTINGS, Http2.FLAG_ACK));
        Assert.assertEquals("<< 0x00000003    22 HEADERS       END_HEADERS", Http2.frameLog(true, 3, 22, Http2.TYPE_HEADERS, Http2.FLAG_END_HEADERS));
        Assert.assertEquals("<< 0x00000003   226 DATA          END_STREAM", Http2.frameLog(true, 3, 226, Http2.TYPE_DATA, Http2.FLAG_END_STREAM));
        Assert.assertEquals(">> 0x00000000     8 GOAWAY        ", Http2.frameLog(false, 0, 8, Http2.TYPE_GOAWAY, Http2.FLAG_NONE));
    }

    @Test
    public void flagOverlapOn0x1() {
        Assert.assertEquals("<< 0x00000000     0 SETTINGS      ACK", Http2.frameLog(true, 0, 0, Http2.TYPE_SETTINGS, ((byte) (1))));
        Assert.assertEquals("<< 0x00000000     8 PING          ACK", Http2.frameLog(true, 0, 8, Http2.TYPE_PING, ((byte) (1))));
        Assert.assertEquals("<< 0x00000003     0 HEADERS       END_STREAM", Http2.frameLog(true, 3, 0, Http2.TYPE_HEADERS, ((byte) (1))));
        Assert.assertEquals("<< 0x00000003     0 DATA          END_STREAM", Http2.frameLog(true, 3, 0, Http2.TYPE_DATA, ((byte) (1))));
    }

    @Test
    public void flagOverlapOn0x4() {
        Assert.assertEquals("<< 0x00000003 10000 HEADERS       END_HEADERS", Http2.frameLog(true, 3, 10000, Http2.TYPE_HEADERS, ((byte) (4))));
        Assert.assertEquals("<< 0x00000003 10000 CONTINUATION  END_HEADERS", Http2.frameLog(true, 3, 10000, Http2.TYPE_CONTINUATION, ((byte) (4))));
        Assert.assertEquals("<< 0x00000004 10000 PUSH_PROMISE  END_PUSH_PROMISE", Http2.frameLog(true, 4, 10000, Http2.TYPE_PUSH_PROMISE, ((byte) (4))));
    }

    @Test
    public void flagOverlapOn0x20() {
        Assert.assertEquals("<< 0x00000003 10000 HEADERS       PRIORITY", Http2.frameLog(true, 3, 10000, Http2.TYPE_HEADERS, ((byte) (32))));
        Assert.assertEquals("<< 0x00000003 10000 DATA          COMPRESSED", Http2.frameLog(true, 3, 10000, Http2.TYPE_DATA, ((byte) (32))));
    }

    /**
     * Ensures that valid flag combinations appear visually correct, and invalid show in hex.  This
     * also demonstrates how sparse the lookup table is.
     */
    @Test
    public void allFormattedFlagsWithValidBits() {
        List<String> formattedFlags = new ArrayList<>(64);// Highest valid flag is 0x20.

        for (byte i = 0; i < 64; i++)
            formattedFlags.add(Http2.formatFlags(Http2.TYPE_HEADERS, i));

        Assert.assertEquals(Arrays.asList("", "END_STREAM", "00000010", "00000011", "END_HEADERS", "END_STREAM|END_HEADERS", "00000110", "00000111", "PADDED", "END_STREAM|PADDED", "00001010", "00001011", "00001100", "END_STREAM|END_HEADERS|PADDED", "00001110", "00001111", "00010000", "00010001", "00010010", "00010011", "00010100", "00010101", "00010110", "00010111", "00011000", "00011001", "00011010", "00011011", "00011100", "00011101", "00011110", "00011111", "PRIORITY", "END_STREAM|PRIORITY", "00100010", "00100011", "END_HEADERS|PRIORITY", "END_STREAM|END_HEADERS|PRIORITY", "00100110", "00100111", "00101000", "END_STREAM|PRIORITY|PADDED", "00101010", "00101011", "00101100", "END_STREAM|END_HEADERS|PRIORITY|PADDED", "00101110", "00101111", "00110000", "00110001", "00110010", "00110011", "00110100", "00110101", "00110110", "00110111", "00111000", "00111001", "00111010", "00111011", "00111100", "00111101", "00111110", "00111111"), formattedFlags);
    }
}
