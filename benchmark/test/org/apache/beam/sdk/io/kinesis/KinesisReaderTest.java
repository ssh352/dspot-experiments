/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.kinesis;


import java.io.IOException;
import java.util.NoSuchElementException;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Tests {@link KinesisReader}.
 */
@RunWith(MockitoJUnitRunner.class)
public class KinesisReaderTest {
    @Mock
    private SimplifiedKinesisClient kinesis;

    @Mock
    private CheckpointGenerator generator;

    @Mock
    private ShardCheckpoint firstCheckpoint;

    @Mock
    private ShardCheckpoint secondCheckpoint;

    @Mock
    private KinesisRecord a;

    @Mock
    private KinesisRecord b;

    @Mock
    private KinesisRecord c;

    @Mock
    private KinesisRecord d;

    @Mock
    private KinesisSource kinesisSource;

    @Mock
    private ShardReadersPool shardReadersPool;

    @Spy
    private KinesisWatermark watermark = new KinesisWatermark();

    private KinesisReader reader;

    @Test
    public void startReturnsFalseIfNoDataAtTheBeginning() throws IOException {
        assertThat(reader.start()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void throwsNoSuchElementExceptionIfNoData() throws IOException {
        reader.start();
        reader.getCurrent();
    }

    @Test
    public void startReturnsTrueIfSomeDataAvailable() throws IOException {
        Mockito.when(shardReadersPool.nextRecord()).thenReturn(CustomOptional.of(a)).thenReturn(CustomOptional.absent());
        assertThat(reader.start()).isTrue();
    }

    @Test
    public void readsThroughAllDataAvailable() throws IOException {
        Mockito.when(shardReadersPool.nextRecord()).thenReturn(CustomOptional.of(c)).thenReturn(CustomOptional.absent()).thenReturn(CustomOptional.of(a)).thenReturn(CustomOptional.absent()).thenReturn(CustomOptional.of(d)).thenReturn(CustomOptional.of(b)).thenReturn(CustomOptional.absent());
        assertThat(reader.start()).isTrue();
        assertThat(reader.getCurrent()).isEqualTo(c);
        assertThat(reader.advance()).isFalse();
        assertThat(reader.advance()).isTrue();
        assertThat(reader.getCurrent()).isEqualTo(a);
        assertThat(reader.advance()).isFalse();
        assertThat(reader.advance()).isTrue();
        assertThat(reader.getCurrent()).isEqualTo(d);
        assertThat(reader.advance()).isTrue();
        assertThat(reader.getCurrent()).isEqualTo(b);
        assertThat(reader.advance()).isFalse();
    }

    @Test
    public void doesNotUpdateWatermarkWhenRecordsNotAvailable() throws IOException {
        boolean advanced = reader.start();
        assertThat(advanced).isFalse();
        Mockito.verify(watermark, Mockito.never()).update(ArgumentMatchers.any());
    }

    @Test
    public void updatesWatermarkWhenRecordsAvailable() throws IOException {
        Mockito.when(shardReadersPool.nextRecord()).thenReturn(CustomOptional.of(c)).thenReturn(CustomOptional.absent());
        boolean advanced = reader.start();
        assertThat(advanced).isTrue();
        Mockito.verify(watermark).update(c.getApproximateArrivalTimestamp());
    }

    @Test
    public void returnsCurrentWatermark() throws IOException {
        Instant expectedWatermark = new Instant(123456L);
        Mockito.doReturn(expectedWatermark).when(watermark).getCurrent(ArgumentMatchers.any());
        reader.start();
        Instant currentWatermark = reader.getWatermark();
        assertThat(currentWatermark).isEqualTo(expectedWatermark);
    }

    @Test
    public void getTotalBacklogBytesShouldReturnLastSeenValueWhenKinesisExceptionsOccur() throws IOException, TransientKinesisException {
        reader.start();
        Mockito.when(kinesisSource.getStreamName()).thenReturn("stream1");
        Mockito.when(kinesis.getBacklogBytes(ArgumentMatchers.eq("stream1"), ArgumentMatchers.any(Instant.class))).thenReturn(10L).thenThrow(TransientKinesisException.class).thenReturn(20L);
        assertThat(reader.getTotalBacklogBytes()).isEqualTo(10);
        assertThat(reader.getTotalBacklogBytes()).isEqualTo(10);
        assertThat(reader.getTotalBacklogBytes()).isEqualTo(20);
    }

    @Test
    public void getTotalBacklogBytesShouldReturnLastSeenValueWhenCalledFrequently() throws IOException, TransientKinesisException {
        KinesisReader backlogCachingReader = createReader(Duration.standardSeconds(30));
        backlogCachingReader.start();
        Mockito.when(kinesisSource.getStreamName()).thenReturn("stream1");
        Mockito.when(kinesis.getBacklogBytes(ArgumentMatchers.eq("stream1"), ArgumentMatchers.any(Instant.class))).thenReturn(10L).thenReturn(20L);
        assertThat(backlogCachingReader.getTotalBacklogBytes()).isEqualTo(10);
        assertThat(backlogCachingReader.getTotalBacklogBytes()).isEqualTo(10);
    }
}

