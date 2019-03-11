/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.coordination.coordination;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingCluster;
import org.apache.druid.curator.announcement.Announcer;
import org.apache.druid.server.coordination.BatchDataSegmentAnnouncer;
import org.apache.druid.server.coordination.ChangeRequestHistory;
import org.apache.druid.server.coordination.ChangeRequestsSnapshot;
import org.apache.druid.server.coordination.DataSegmentChangeRequest;
import org.apache.druid.timeline.DataSegment;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class BatchDataSegmentAnnouncerTest {
    private static final String testBasePath = "/test";

    private static final String testSegmentsPath = "/test/segments/id";

    private static final Joiner joiner = Joiner.on("/");

    private TestingCluster testingCluster;

    private CuratorFramework cf;

    private ObjectMapper jsonMapper;

    private Announcer announcer;

    private BatchDataSegmentAnnouncerTest.SegmentReader segmentReader;

    private BatchDataSegmentAnnouncer segmentAnnouncer;

    private Set<DataSegment> testSegments;

    private final AtomicInteger maxBytesPerNode = new AtomicInteger((512 * 1024));

    private Boolean skipDimensionsAndMetrics;

    private Boolean skipLoadSpec;

    @Test
    public void testSingleAnnounce() throws Exception {
        Iterator<DataSegment> segIter = testSegments.iterator();
        DataSegment firstSegment = segIter.next();
        DataSegment secondSegment = segIter.next();
        segmentAnnouncer.announceSegment(firstSegment);
        List<String> zNodes = cf.getChildren().forPath(BatchDataSegmentAnnouncerTest.testSegmentsPath);
        for (String zNode : zNodes) {
            Set<DataSegment> segments = segmentReader.read(BatchDataSegmentAnnouncerTest.joiner.join(BatchDataSegmentAnnouncerTest.testSegmentsPath, zNode));
            Assert.assertEquals(segments.iterator().next(), firstSegment);
        }
        segmentAnnouncer.announceSegment(secondSegment);
        for (String zNode : zNodes) {
            Set<DataSegment> segments = segmentReader.read(BatchDataSegmentAnnouncerTest.joiner.join(BatchDataSegmentAnnouncerTest.testSegmentsPath, zNode));
            Assert.assertEquals(Sets.newHashSet(firstSegment, secondSegment), segments);
        }
        ChangeRequestsSnapshot<DataSegmentChangeRequest> snapshot = segmentAnnouncer.getSegmentChangesSince(new ChangeRequestHistory.Counter((-1), (-1))).get();
        Assert.assertEquals(2, snapshot.getRequests().size());
        Assert.assertEquals(2, snapshot.getCounter().getCounter());
        segmentAnnouncer.unannounceSegment(firstSegment);
        for (String zNode : zNodes) {
            Set<DataSegment> segments = segmentReader.read(BatchDataSegmentAnnouncerTest.joiner.join(BatchDataSegmentAnnouncerTest.testSegmentsPath, zNode));
            Assert.assertEquals(segments.iterator().next(), secondSegment);
        }
        segmentAnnouncer.unannounceSegment(secondSegment);
        Assert.assertTrue(cf.getChildren().forPath(BatchDataSegmentAnnouncerTest.testSegmentsPath).isEmpty());
        snapshot = segmentAnnouncer.getSegmentChangesSince(snapshot.getCounter()).get();
        Assert.assertEquals(2, snapshot.getRequests().size());
        Assert.assertEquals(4, snapshot.getCounter().getCounter());
        snapshot = segmentAnnouncer.getSegmentChangesSince(new ChangeRequestHistory.Counter((-1), (-1))).get();
        Assert.assertEquals(0, snapshot.getRequests().size());
        Assert.assertEquals(4, snapshot.getCounter().getCounter());
    }

    @Test
    public void testSkipDimensions() throws Exception {
        skipDimensionsAndMetrics = true;
        Iterator<DataSegment> segIter = testSegments.iterator();
        DataSegment firstSegment = segIter.next();
        segmentAnnouncer.announceSegment(firstSegment);
        List<String> zNodes = cf.getChildren().forPath(BatchDataSegmentAnnouncerTest.testSegmentsPath);
        for (String zNode : zNodes) {
            DataSegment announcedSegment = Iterables.getOnlyElement(segmentReader.read(BatchDataSegmentAnnouncerTest.joiner.join(BatchDataSegmentAnnouncerTest.testSegmentsPath, zNode)));
            Assert.assertEquals(announcedSegment, firstSegment);
            Assert.assertTrue(announcedSegment.getDimensions().isEmpty());
            Assert.assertTrue(announcedSegment.getMetrics().isEmpty());
        }
        segmentAnnouncer.unannounceSegment(firstSegment);
        Assert.assertTrue(cf.getChildren().forPath(BatchDataSegmentAnnouncerTest.testSegmentsPath).isEmpty());
    }

    @Test
    public void testSkipLoadSpec() throws Exception {
        skipLoadSpec = true;
        Iterator<DataSegment> segIter = testSegments.iterator();
        DataSegment firstSegment = segIter.next();
        segmentAnnouncer.announceSegment(firstSegment);
        List<String> zNodes = cf.getChildren().forPath(BatchDataSegmentAnnouncerTest.testSegmentsPath);
        for (String zNode : zNodes) {
            DataSegment announcedSegment = Iterables.getOnlyElement(segmentReader.read(BatchDataSegmentAnnouncerTest.joiner.join(BatchDataSegmentAnnouncerTest.testSegmentsPath, zNode)));
            Assert.assertEquals(announcedSegment, firstSegment);
            Assert.assertNull(announcedSegment.getLoadSpec());
        }
        segmentAnnouncer.unannounceSegment(firstSegment);
        Assert.assertTrue(cf.getChildren().forPath(BatchDataSegmentAnnouncerTest.testSegmentsPath).isEmpty());
    }

    @Test
    public void testSingleAnnounceManyTimes() throws Exception {
        int prevMax = maxBytesPerNode.get();
        maxBytesPerNode.set(2048);
        // each segment is about 348 bytes long and that makes 2048 / 348 = 5 segments included per node
        // so 100 segments makes 100 / 5 = 20 nodes
        try {
            for (DataSegment segment : testSegments) {
                segmentAnnouncer.announceSegment(segment);
            }
        } finally {
            maxBytesPerNode.set(prevMax);
        }
        List<String> zNodes = cf.getChildren().forPath(BatchDataSegmentAnnouncerTest.testSegmentsPath);
        Assert.assertEquals(20, zNodes.size());
        Set<DataSegment> segments = Sets.newHashSet(testSegments);
        for (String zNode : zNodes) {
            for (DataSegment segment : segmentReader.read(BatchDataSegmentAnnouncerTest.joiner.join(BatchDataSegmentAnnouncerTest.testSegmentsPath, zNode))) {
                Assert.assertTrue(("Invalid segment " + segment), segments.remove(segment));
            }
        }
        Assert.assertTrue(("Failed to find segments " + segments), segments.isEmpty());
    }

    @Test
    public void testBatchAnnounce() throws Exception {
        testBatchAnnounce(true);
    }

    @Test
    public void testMultipleBatchAnnounce() throws Exception {
        for (int i = 0; i < 10; i++) {
            testBatchAnnounce(false);
        }
    }

    private static class SegmentReader {
        private final CuratorFramework cf;

        private final ObjectMapper jsonMapper;

        public SegmentReader(CuratorFramework cf, ObjectMapper jsonMapper) {
            this.cf = cf;
            this.jsonMapper = jsonMapper;
        }

        public Set<DataSegment> read(String path) {
            try {
                if ((cf.checkExists().forPath(path)) != null) {
                    return jsonMapper.readValue(cf.getData().forPath(path), new TypeReference<Set<DataSegment>>() {});
                }
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
            return new HashSet<>();
        }
    }
}

