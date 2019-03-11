/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.shared.utilities;


import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.Test;


public class ByteBufferUtilsTest {
    @Test
    public void readBytesFromArrayBackedByteBuffer() {
        final byte[] bytes = "FOOBAR".getBytes(StandardCharsets.US_ASCII);
        final ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
        final ByteBuffer buffer2 = ByteBuffer.wrap(bytes);
        final byte[] readBytesComplete = ByteBufferUtils.readBytes(buffer1);
        final byte[] readBytesPartial = ByteBufferUtils.readBytes(buffer2, 0, 3);
        assertThat(readBytesComplete).isEqualTo(bytes);
        assertThat(readBytesPartial).isEqualTo(Arrays.copyOf(bytes, 3));
    }

    @Test
    public void readBytesFromNonArrayBackedByteBuffer() {
        final byte[] bytes = "FOOBAR".getBytes(StandardCharsets.US_ASCII);
        final ByteBuffer buffer1 = ByteBuffer.allocateDirect(1024);
        buffer1.put(bytes).flip();
        final ByteBuffer buffer2 = ByteBuffer.allocateDirect(1024);
        buffer2.put(bytes).flip();
        final byte[] readBytesComplete = ByteBufferUtils.readBytes(buffer1);
        final byte[] readBytesPartial = ByteBufferUtils.readBytes(buffer2, 0, 3);
        assertThat(readBytesComplete).isEqualTo(bytes);
        assertThat(readBytesPartial).isEqualTo(Arrays.copyOf(bytes, 3));
    }
}

