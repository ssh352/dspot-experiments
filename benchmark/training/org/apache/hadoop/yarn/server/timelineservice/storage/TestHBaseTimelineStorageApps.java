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
package org.apache.hadoop.yarn.server.timelineservice.storage;


import ApplicationColumn.CREATED_TIME;
import ApplicationColumn.ID;
import ApplicationColumnPrefix.CONFIG;
import ApplicationColumnPrefix.EVENT;
import ApplicationColumnPrefix.INFO;
import ApplicationColumnPrefix.IS_RELATED_TO;
import ApplicationColumnPrefix.METRIC;
import ApplicationColumnPrefix.RELATES_TO;
import Field.CONFIGS;
import Field.METRICS;
import Separator.VALUES;
import TimelineEntityType.YARN_APPLICATION;
import TimelineMetricOperation.SUM;
import TimelineReader.Field.ALL;
import Type.SINGLE_VALUE;
import Type.TIME_SERIES;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.timelineservice.ApplicationEntity;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEvent;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineMetric;
import org.apache.hadoop.yarn.server.metrics.ApplicationMetricsConstants;
import org.apache.hadoop.yarn.server.timelineservice.collector.TimelineCollectorContext;
import org.apache.hadoop.yarn.server.timelineservice.reader.TimelineDataToRetrieve;
import org.apache.hadoop.yarn.server.timelineservice.reader.TimelineEntityFilters;
import org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareOp;
import org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineFilterList;
import org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineFilterList.Operator;
import org.apache.hadoop.yarn.server.timelineservice.storage.application.ApplicationRowKey;
import org.apache.hadoop.yarn.server.timelineservice.storage.application.ApplicationTableRW;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.ColumnRWHelper;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.EventColumnName;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.EventColumnNameConverter;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.HBaseTimelineSchemaUtils;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.KeyConverter;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.StringKeyConverter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for apps stored in TimelineStorage.
 */
public class TestHBaseTimelineStorageApps {
    private static HBaseTestingUtility util;

    private HBaseTimelineReaderImpl reader;

    private static final long CURRENT_TIME = System.currentTimeMillis();

    @Test
    public void testWriteNullApplicationToHBase() throws Exception {
        TimelineEntities te = new TimelineEntities();
        ApplicationEntity entity = new ApplicationEntity();
        String appId = "application_1000178881110_2002";
        entity.setId(appId);
        long cTime = 1425016501000L;
        entity.setCreatedTime(cTime);
        // add the info map in Timeline Entity
        Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("in fo M apK  ey1", "infoMapValue1");
        infoMap.put("infoMapKey2", 10);
        entity.addInfo(infoMap);
        te.addEntity(entity);
        HBaseTimelineWriterImpl hbi = null;
        try {
            Configuration c1 = TestHBaseTimelineStorageApps.util.getConfiguration();
            hbi = new HBaseTimelineWriterImpl();
            hbi.init(c1);
            hbi.start();
            String cluster = "cluster_check_null_application";
            String user = "user1check_null_application";
            // set the flow name to null
            String flow = null;
            String flowVersion = "AB7822C10F1111";
            long runid = 1002345678919L;
            hbi.write(new TimelineCollectorContext(cluster, user, flow, flowVersion, runid, appId), te, UserGroupInformation.createRemoteUser(user));
            hbi.stop();
            // retrieve the row
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(cluster));
            scan.setStopRow(Bytes.toBytes((cluster + "1")));
            Connection conn = ConnectionFactory.createConnection(c1);
            ResultScanner resultScanner = new ApplicationTableRW().getResultScanner(c1, conn, scan);
            Assert.assertTrue((resultScanner != null));
            // try to iterate over results
            int count = 0;
            for (Result rr = resultScanner.next(); rr != null; rr = resultScanner.next()) {
                count++;
            }
            // there should be no rows written
            // no exceptions thrown during write
            Assert.assertEquals(0, count);
        } finally {
            if (hbi != null) {
                hbi.stop();
                hbi.close();
            }
        }
    }

    @Test
    public void testWriteApplicationToHBase() throws Exception {
        TimelineEntities te = new TimelineEntities();
        ApplicationEntity entity = new ApplicationEntity();
        String appId = "application_1000178881110_2002";
        entity.setId(appId);
        Long cTime = 1425016501000L;
        entity.setCreatedTime(cTime);
        // add the info map in Timeline Entity
        Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("infoMapKey1", "infoMapValue1");
        infoMap.put("infoMapKey2", 10);
        entity.addInfo(infoMap);
        // add the isRelatedToEntity info
        String key = "task";
        String value = "is_related_to_entity_id_here";
        Set<String> isRelatedToSet = new HashSet<String>();
        isRelatedToSet.add(value);
        Map<String, Set<String>> isRelatedTo = new HashMap<String, Set<String>>();
        isRelatedTo.put(key, isRelatedToSet);
        entity.setIsRelatedToEntities(isRelatedTo);
        // add the relatesTo info
        key = "container";
        value = "relates_to_entity_id_here";
        Set<String> relatesToSet = new HashSet<String>();
        relatesToSet.add(value);
        value = "relates_to_entity_id_here_Second";
        relatesToSet.add(value);
        Map<String, Set<String>> relatesTo = new HashMap<String, Set<String>>();
        relatesTo.put(key, relatesToSet);
        entity.setRelatesToEntities(relatesTo);
        // add some config entries
        Map<String, String> conf = new HashMap<String, String>();
        conf.put("config_param1", "value1");
        conf.put("config_param2", "value2");
        entity.addConfigs(conf);
        // add metrics
        Set<TimelineMetric> metrics = new HashSet<>();
        TimelineMetric m1 = new TimelineMetric();
        m1.setId("MAP_SLOT_MILLIS");
        Map<Long, Number> metricValues = new HashMap<Long, Number>();
        metricValues.put(((TestHBaseTimelineStorageApps.CURRENT_TIME) - 120000), 100000000);
        metricValues.put(((TestHBaseTimelineStorageApps.CURRENT_TIME) - 100000), 200000000);
        metricValues.put(((TestHBaseTimelineStorageApps.CURRENT_TIME) - 80000), 300000000);
        metricValues.put(((TestHBaseTimelineStorageApps.CURRENT_TIME) - 60000), 400000000);
        metricValues.put(((TestHBaseTimelineStorageApps.CURRENT_TIME) - 40000), 50000000000L);
        metricValues.put(((TestHBaseTimelineStorageApps.CURRENT_TIME) - 20000), 60000000000L);
        m1.setType(TIME_SERIES);
        m1.setValues(metricValues);
        metrics.add(m1);
        entity.addMetrics(metrics);
        // add aggregated metrics
        TimelineEntity aggEntity = new TimelineEntity();
        String type = YARN_APPLICATION.toString();
        aggEntity.setId(appId);
        aggEntity.setType(type);
        long cTime2 = 1425016502000L;
        aggEntity.setCreatedTime(cTime2);
        TimelineMetric aggMetric = new TimelineMetric();
        aggMetric.setId("MEM_USAGE");
        Map<Long, Number> aggMetricValues = new HashMap<Long, Number>();
        long aggTs = TestHBaseTimelineStorageApps.CURRENT_TIME;
        aggMetricValues.put((aggTs - 120000), 102400000L);
        aggMetric.setType(SINGLE_VALUE);
        aggMetric.setRealtimeAggregationOp(SUM);
        aggMetric.setValues(aggMetricValues);
        Set<TimelineMetric> aggMetrics = new HashSet<>();
        aggMetrics.add(aggMetric);
        entity.addMetrics(aggMetrics);
        te.addEntity(entity);
        HBaseTimelineWriterImpl hbi = null;
        try {
            Configuration c1 = TestHBaseTimelineStorageApps.util.getConfiguration();
            hbi = new HBaseTimelineWriterImpl();
            hbi.init(c1);
            hbi.start();
            String cluster = "cluster_test_write_app";
            String user = "user1";
            String flow = "s!ome_f\tlow  _n am!e";
            String flowVersion = "AB7822C10F1111";
            long runid = 1002345678919L;
            hbi.write(new TimelineCollectorContext(cluster, user, flow, flowVersion, runid, appId), te, UserGroupInformation.createRemoteUser(user));
            // Write entity again, this time without created time.
            entity = new ApplicationEntity();
            appId = "application_1000178881110_2002";
            entity.setId(appId);
            // add the info map in Timeline Entity
            Map<String, Object> infoMap1 = new HashMap<>();
            infoMap1.put("infoMapKey3", "infoMapValue1");
            entity.addInfo(infoMap1);
            te = new TimelineEntities();
            te.addEntity(entity);
            hbi.write(new TimelineCollectorContext(cluster, user, flow, flowVersion, runid, appId), te, UserGroupInformation.createRemoteUser(user));
            hbi.stop();
            infoMap.putAll(infoMap1);
            // retrieve the row
            ApplicationRowKey applicationRowKey = new ApplicationRowKey(cluster, user, flow, runid, appId);
            byte[] rowKey = applicationRowKey.getRowKey();
            Get get = new Get(rowKey);
            get.setMaxVersions(Integer.MAX_VALUE);
            Connection conn = ConnectionFactory.createConnection(c1);
            Result result = new ApplicationTableRW().getResult(c1, conn, get);
            Assert.assertTrue((result != null));
            Assert.assertEquals(17, result.size());
            // check the row key
            byte[] row1 = result.getRow();
            Assert.assertTrue(isApplicationRowKeyCorrect(row1, cluster, user, flow, runid, appId));
            // check info column family
            String id1 = ColumnRWHelper.readResult(result, ID).toString();
            Assert.assertEquals(appId, id1);
            Long cTime1 = ((Long) (ColumnRWHelper.readResult(result, CREATED_TIME)));
            Assert.assertEquals(cTime, cTime1);
            Map<String, Object> infoColumns = ColumnRWHelper.readResults(result, INFO, new StringKeyConverter());
            Assert.assertEquals(infoMap, infoColumns);
            // Remember isRelatedTo is of type Map<String, Set<String>>
            for (Map.Entry<String, Set<String>> isRelatedToEntry : isRelatedTo.entrySet()) {
                Object isRelatedToValue = ColumnRWHelper.readResult(result, IS_RELATED_TO, isRelatedToEntry.getKey());
                String compoundValue = isRelatedToValue.toString();
                // id7?id9?id6
                Set<String> isRelatedToValues = new HashSet<String>(VALUES.splitEncoded(compoundValue));
                Assert.assertEquals(isRelatedTo.get(isRelatedToEntry.getKey()).size(), isRelatedToValues.size());
                for (String v : isRelatedToEntry.getValue()) {
                    Assert.assertTrue(isRelatedToValues.contains(v));
                }
            }
            // RelatesTo
            for (Map.Entry<String, Set<String>> relatesToEntry : relatesTo.entrySet()) {
                String compoundValue = ColumnRWHelper.readResult(result, RELATES_TO, relatesToEntry.getKey()).toString();
                // id3?id4?id5
                Set<String> relatesToValues = new HashSet<String>(VALUES.splitEncoded(compoundValue));
                Assert.assertEquals(relatesTo.get(relatesToEntry.getKey()).size(), relatesToValues.size());
                for (String v : relatesToEntry.getValue()) {
                    Assert.assertTrue(relatesToValues.contains(v));
                }
            }
            KeyConverter<String> stringKeyConverter = new StringKeyConverter();
            // Configuration
            Map<String, Object> configColumns = ColumnRWHelper.readResults(result, CONFIG, stringKeyConverter);
            Assert.assertEquals(conf, configColumns);
            NavigableMap<String, NavigableMap<Long, Number>> metricsResult = ColumnRWHelper.readResultsWithTimestamps(result, METRIC, stringKeyConverter);
            NavigableMap<Long, Number> metricMap = metricsResult.get(m1.getId());
            TestHBaseTimelineStorageApps.matchMetrics(metricValues, metricMap);
            // read the timeline entity using the reader this time. In metrics limit
            // specify Integer MAX_VALUE. A TIME_SERIES will be returned(if more than
            // one value exists for a metric).
            TimelineEntity e1 = reader.getEntity(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext(cluster, user, flow, runid, appId, entity.getType(), entity.getId()), new TimelineDataToRetrieve(null, null, EnumSet.of(ALL), Integer.MAX_VALUE, null, null));
            Assert.assertNotNull(e1);
            // verify attributes
            Assert.assertEquals(appId, e1.getId());
            Assert.assertEquals(YARN_APPLICATION.toString(), e1.getType());
            Assert.assertEquals(cTime, e1.getCreatedTime());
            Map<String, Object> infoMap2 = e1.getInfo();
            // fromid key is added by storage. Remove it for comparision.
            infoMap2.remove("FROM_ID");
            Assert.assertEquals(infoMap, infoMap2);
            Map<String, Set<String>> isRelatedTo2 = e1.getIsRelatedToEntities();
            Assert.assertEquals(isRelatedTo, isRelatedTo2);
            Map<String, Set<String>> relatesTo2 = e1.getRelatesToEntities();
            Assert.assertEquals(relatesTo, relatesTo2);
            Map<String, String> conf2 = e1.getConfigs();
            Assert.assertEquals(conf, conf2);
            Set<TimelineMetric> metrics2 = e1.getMetrics();
            Assert.assertEquals(2, metrics2.size());
            for (TimelineMetric metric2 : metrics2) {
                Map<Long, Number> metricValues2 = metric2.getValues();
                Assert.assertTrue(((metric2.getId().equals("MAP_SLOT_MILLIS")) || (metric2.getId().equals("MEM_USAGE"))));
                if (metric2.getId().equals("MAP_SLOT_MILLIS")) {
                    Assert.assertEquals(6, metricValues2.size());
                    TestHBaseTimelineStorageApps.matchMetrics(metricValues, metricValues2);
                }
                if (metric2.getId().equals("MEM_USAGE")) {
                    Assert.assertEquals(1, metricValues2.size());
                    TestHBaseTimelineStorageApps.matchMetrics(aggMetricValues, metricValues2);
                }
            }
            // In metrics limit specify a value of 3. No more than 3 values for a
            // metric will be returned.
            e1 = reader.getEntity(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext(cluster, user, flow, runid, appId, entity.getType(), entity.getId()), new TimelineDataToRetrieve(null, null, EnumSet.of(ALL), 3, null, null));
            Assert.assertNotNull(e1);
            Assert.assertEquals(appId, e1.getId());
            Assert.assertEquals(YARN_APPLICATION.toString(), e1.getType());
            Assert.assertEquals(conf, e1.getConfigs());
            metrics2 = e1.getMetrics();
            Assert.assertEquals(2, metrics2.size());
            for (TimelineMetric metric2 : metrics2) {
                Map<Long, Number> metricValues2 = metric2.getValues();
                Assert.assertTrue(((metricValues2.size()) <= 3));
                Assert.assertTrue(((metric2.getId().equals("MAP_SLOT_MILLIS")) || (metric2.getId().equals("MEM_USAGE"))));
            }
            // Check if single value(latest value) instead of time series is returned
            // if metricslimit is not set(null), irrespective of number of metric
            // values.
            e1 = reader.getEntity(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext(cluster, user, flow, runid, appId, entity.getType(), entity.getId()), new TimelineDataToRetrieve(null, null, EnumSet.of(ALL), null, null, null));
            Assert.assertNotNull(e1);
            Assert.assertEquals(appId, e1.getId());
            Assert.assertEquals(YARN_APPLICATION.toString(), e1.getType());
            Assert.assertEquals(cTime, e1.getCreatedTime());
            infoMap2 = e1.getInfo();
            // fromid key is added by storage. Remove it for comparison.
            infoMap2.remove("FROM_ID");
            Assert.assertEquals(infoMap, e1.getInfo());
            Assert.assertEquals(isRelatedTo, e1.getIsRelatedToEntities());
            Assert.assertEquals(relatesTo, e1.getRelatesToEntities());
            Assert.assertEquals(conf, e1.getConfigs());
            Assert.assertEquals(2, e1.getMetrics().size());
            for (TimelineMetric metric : e1.getMetrics()) {
                Assert.assertEquals(1, metric.getValues().size());
                Assert.assertEquals(TimelineMetric.Type.SINGLE_VALUE, metric.getType());
                Assert.assertTrue(((metric.getId().equals("MAP_SLOT_MILLIS")) || (metric.getId().equals("MEM_USAGE"))));
                Assert.assertEquals(1, metric.getValues().size());
                if (metric.getId().equals("MAP_SLOT_MILLIS")) {
                    Assert.assertTrue(metric.getValues().containsKey(((TestHBaseTimelineStorageApps.CURRENT_TIME) - 20000)));
                    Assert.assertEquals(metricValues.get(((TestHBaseTimelineStorageApps.CURRENT_TIME) - 20000)), metric.getValues().get(((TestHBaseTimelineStorageApps.CURRENT_TIME) - 20000)));
                }
                if (metric.getId().equals("MEM_USAGE")) {
                    Assert.assertTrue(metric.getValues().containsKey((aggTs - 120000)));
                    Assert.assertEquals(aggMetricValues.get((aggTs - 120000)), metric.getValues().get((aggTs - 120000)));
                }
            }
        } finally {
            if (hbi != null) {
                hbi.stop();
                hbi.close();
            }
        }
    }

    @Test
    public void testEvents() throws IOException {
        TimelineEvent event = new TimelineEvent();
        String eventId = ApplicationMetricsConstants.CREATED_EVENT_TYPE;
        event.setId(eventId);
        Long expTs = 1436512802000L;
        event.setTimestamp(expTs);
        String expKey = "foo_event";
        Object expVal = "test";
        event.addInfo(expKey, expVal);
        final TimelineEntity entity = new ApplicationEntity();
        entity.setId(HBaseTimelineSchemaUtils.convertApplicationIdToString(ApplicationId.newInstance(0, 1)));
        entity.addEvent(event);
        TimelineEntities entities = new TimelineEntities();
        entities.addEntity(entity);
        HBaseTimelineWriterImpl hbi = null;
        try {
            Configuration c1 = TestHBaseTimelineStorageApps.util.getConfiguration();
            hbi = new HBaseTimelineWriterImpl();
            hbi.init(c1);
            hbi.start();
            String cluster = "cluster_test_events";
            String user = "user2";
            String flow = "other_flow_name";
            String flowVersion = "1111F01C2287BA";
            long runid = 1009876543218L;
            String appName = "application_123465899910_1001";
            hbi.write(new TimelineCollectorContext(cluster, user, flow, flowVersion, runid, appName), entities, UserGroupInformation.createRemoteUser(user));
            hbi.stop();
            // retrieve the row
            ApplicationRowKey applicationRowKey = new ApplicationRowKey(cluster, user, flow, runid, appName);
            byte[] rowKey = applicationRowKey.getRowKey();
            Get get = new Get(rowKey);
            get.setMaxVersions(Integer.MAX_VALUE);
            Connection conn = ConnectionFactory.createConnection(c1);
            Result result = new ApplicationTableRW().getResult(c1, conn, get);
            Assert.assertTrue((result != null));
            // check the row key
            byte[] row1 = result.getRow();
            Assert.assertTrue(isApplicationRowKeyCorrect(row1, cluster, user, flow, runid, appName));
            Map<EventColumnName, Object> eventsResult = ColumnRWHelper.readResults(result, EVENT, new EventColumnNameConverter());
            // there should be only one event
            Assert.assertEquals(1, eventsResult.size());
            for (Map.Entry<EventColumnName, Object> e : eventsResult.entrySet()) {
                EventColumnName eventColumnName = e.getKey();
                // the qualifier is a compound key
                // hence match individual values
                Assert.assertEquals(eventId, eventColumnName.getId());
                Assert.assertEquals(expTs, eventColumnName.getTimestamp());
                Assert.assertEquals(expKey, eventColumnName.getInfoKey());
                Object value = e.getValue();
                // there should be only one timestamp and value
                Assert.assertEquals(expVal, value.toString());
            }
            // read the timeline entity using the reader this time
            TimelineEntity e1 = reader.getEntity(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext(cluster, user, flow, runid, appName, entity.getType(), entity.getId()), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.ALL), null, null, null));
            TimelineEntity e2 = reader.getEntity(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext(cluster, user, null, null, appName, entity.getType(), entity.getId()), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.ALL), null, null, null));
            Assert.assertNotNull(e1);
            Assert.assertNotNull(e2);
            Assert.assertEquals(e1, e2);
            // check the events
            NavigableSet<TimelineEvent> events = e1.getEvents();
            // there should be only one event
            Assert.assertEquals(1, events.size());
            for (TimelineEvent e : events) {
                Assert.assertEquals(eventId, e.getId());
                Assert.assertEquals(expTs, Long.valueOf(e.getTimestamp()));
                Map<String, Object> info = e.getInfo();
                Assert.assertEquals(1, info.size());
                for (Map.Entry<String, Object> infoEntry : info.entrySet()) {
                    Assert.assertEquals(expKey, infoEntry.getKey());
                    Assert.assertEquals(expVal, infoEntry.getValue());
                }
            }
        } finally {
            if (hbi != null) {
                hbi.stop();
                hbi.close();
            }
        }
    }

    @Test
    public void testNonIntegralMetricValues() throws IOException {
        TimelineEntities teApp = new TimelineEntities();
        ApplicationEntity entityApp = new ApplicationEntity();
        String appId = "application_1000178881110_2002";
        entityApp.setId(appId);
        entityApp.setCreatedTime(1425016501000L);
        // add metrics with floating point values
        Set<TimelineMetric> metricsApp = new HashSet<>();
        TimelineMetric mApp = new TimelineMetric();
        mApp.setId("MAP_SLOT_MILLIS");
        Map<Long, Number> metricAppValues = new HashMap<Long, Number>();
        long ts = System.currentTimeMillis();
        metricAppValues.put((ts - 20), 10.5);
        metricAppValues.put((ts - 10), 20.5);
        mApp.setType(TIME_SERIES);
        mApp.setValues(metricAppValues);
        metricsApp.add(mApp);
        entityApp.addMetrics(metricsApp);
        teApp.addEntity(entityApp);
        TimelineEntities teEntity = new TimelineEntities();
        TimelineEntity entity = new TimelineEntity();
        entity.setId("hello");
        entity.setType("world");
        entity.setCreatedTime(1425016501000L);
        // add metrics with floating point values
        Set<TimelineMetric> metricsEntity = new HashSet<>();
        TimelineMetric mEntity = new TimelineMetric();
        mEntity.setId("MAP_SLOT_MILLIS");
        mEntity.addValue((ts - 20), 10.5);
        metricsEntity.add(mEntity);
        entity.addMetrics(metricsEntity);
        teEntity.addEntity(entity);
        HBaseTimelineWriterImpl hbi = null;
        try {
            Configuration c1 = TestHBaseTimelineStorageApps.util.getConfiguration();
            hbi = new HBaseTimelineWriterImpl();
            hbi.init(c1);
            hbi.start();
            // Writing application entity.
            TimelineCollectorContext context = new TimelineCollectorContext("c1", "u1", "f1", "v1", 1002345678919L, appId);
            UserGroupInformation user = UserGroupInformation.createRemoteUser("u1");
            try {
                hbi.write(context, teApp, user);
                Assert.fail("Expected an exception as metric values are non integral");
            } catch (IOException e) {
            }
            // Writing generic entity.
            try {
                hbi.write(context, teEntity, user);
                Assert.fail("Expected an exception as metric values are non integral");
            } catch (IOException e) {
            }
            hbi.stop();
        } finally {
            if (hbi != null) {
                hbi.stop();
                hbi.close();
            }
        }
    }

    @Test
    public void testReadApps() throws Exception {
        TimelineEntity entity = reader.getEntity(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, "application_1111111111_2222", YARN_APPLICATION.toString(), null), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.ALL), null, null, null));
        Assert.assertNotNull(entity);
        Assert.assertEquals(3, entity.getConfigs().size());
        Assert.assertEquals(1, entity.getIsRelatedToEntities().size());
        Set<TimelineEntity> entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.ALL), null, null, null));
        Assert.assertEquals(3, entities.size());
        int cfgCnt = 0;
        int metricCnt = 0;
        int infoCnt = 0;
        int eventCnt = 0;
        int relatesToCnt = 0;
        int isRelatedToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            cfgCnt += ((timelineEntity.getConfigs()) == null) ? 0 : timelineEntity.getConfigs().size();
            metricCnt += ((timelineEntity.getMetrics()) == null) ? 0 : timelineEntity.getMetrics().size();
            infoCnt += ((timelineEntity.getInfo()) == null) ? 0 : timelineEntity.getInfo().size();
            eventCnt += ((timelineEntity.getEvents()) == null) ? 0 : timelineEntity.getEvents().size();
            relatesToCnt += ((timelineEntity.getRelatesToEntities()) == null) ? 0 : timelineEntity.getRelatesToEntities().size();
            isRelatedToCnt += ((timelineEntity.getIsRelatedToEntities()) == null) ? 0 : timelineEntity.getIsRelatedToEntities().size();
        }
        Assert.assertEquals(5, cfgCnt);
        Assert.assertEquals(3, metricCnt);
        Assert.assertEquals(8, infoCnt);
        Assert.assertEquals(4, eventCnt);
        Assert.assertEquals(4, relatesToCnt);
        Assert.assertEquals(4, isRelatedToCnt);
    }

    @Test
    public void testFilterAppsByCreatedTime() throws Exception {
        Set<TimelineEntity> entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().createdTimeBegin(1425016502000L).createTimeEnd(1425016502040L).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(3, entities.size());
        for (TimelineEntity entity : entities) {
            if (((!(entity.getId().equals("application_1111111111_2222"))) && (!(entity.getId().equals("application_1111111111_3333")))) && (!(entity.getId().equals("application_1111111111_4444")))) {
                Assert.fail(("Entities with ids' application_1111111111_2222, " + ("application_1111111111_3333 and application_1111111111_4444" + " should be present")));
            }
        }
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().createdTimeBegin(1425016502015L).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, entities.size());
        for (TimelineEntity entity : entities) {
            if ((!(entity.getId().equals("application_1111111111_3333"))) && (!(entity.getId().equals("application_1111111111_4444")))) {
                Assert.fail(("Apps with ids' application_1111111111_3333 and" + " application_1111111111_4444 should be present"));
            }
        }
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().createTimeEnd(1425016502015L).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, entities.size());
        for (TimelineEntity entity : entities) {
            if (!(entity.getId().equals("application_1111111111_2222"))) {
                Assert.fail(("App with id application_1111111111_2222 should" + " be present"));
            }
        }
    }

    @Test
    public void testReadAppsDefaultView() throws Exception {
        TimelineEntity e1 = reader.getEntity(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, "application_1111111111_2222", YARN_APPLICATION.toString(), null), new TimelineDataToRetrieve());
        Assert.assertNotNull(e1);
        Assert.assertEquals(1, e1.getInfo().size());
        Assert.assertTrue(((((e1.getConfigs().isEmpty()) && (e1.getMetrics().isEmpty())) && (e1.getIsRelatedToEntities().isEmpty())) && (e1.getRelatesToEntities().isEmpty())));
        Set<TimelineEntity> es1 = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().build(), new TimelineDataToRetrieve());
        Assert.assertEquals(3, es1.size());
        for (TimelineEntity e : es1) {
            Assert.assertEquals(1, e1.getInfo().size());
            Assert.assertTrue(((((e.getConfigs().isEmpty()) && (e.getMetrics().isEmpty())) && (e.getIsRelatedToEntities().isEmpty())) && (e.getRelatesToEntities().isEmpty())));
        }
    }

    @Test
    public void testReadAppsByFields() throws Exception {
        TimelineEntity e1 = reader.getEntity(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, "application_1111111111_2222", YARN_APPLICATION.toString(), null), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.INFO, CONFIGS), null, null, null));
        Assert.assertNotNull(e1);
        Assert.assertEquals(3, e1.getConfigs().size());
        Assert.assertEquals(0, e1.getIsRelatedToEntities().size());
        Set<TimelineEntity> es1 = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.IS_RELATED_TO, METRICS), null, null, null));
        Assert.assertEquals(3, es1.size());
        int metricsCnt = 0;
        int isRelatedToCnt = 0;
        int infoCnt = 0;
        for (TimelineEntity entity : es1) {
            metricsCnt += entity.getMetrics().size();
            isRelatedToCnt += entity.getIsRelatedToEntities().size();
            infoCnt += entity.getInfo().size();
        }
        Assert.assertEquals(3, infoCnt);
        Assert.assertEquals(4, isRelatedToCnt);
        Assert.assertEquals(3, metricsCnt);
    }

    @Test
    public void testReadAppsIsRelatedTo() throws Exception {
        TimelineFilterList irt = new TimelineFilterList(Operator.OR);
        irt.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "task", new HashSet<Object>(Arrays.asList("relatedto1"))));
        irt.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "task2", new HashSet<Object>(Arrays.asList("relatedto4"))));
        Set<TimelineEntity> entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().isRelatedTo(irt).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.ALL), null, null, null));
        Assert.assertEquals(2, entities.size());
        int isRelatedToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            isRelatedToCnt += timelineEntity.getIsRelatedToEntities().size();
            if ((!(timelineEntity.getId().equals("application_1111111111_2222"))) && (!(timelineEntity.getId().equals("application_1111111111_3333")))) {
                Assert.fail(("Entity ids' should have been application_1111111111_2222" + " and application_1111111111_3333"));
            }
        }
        Assert.assertEquals(3, isRelatedToCnt);
        TimelineFilterList irt1 = new TimelineFilterList();
        irt1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "task1", new HashSet<Object>(Arrays.asList("relatedto3"))));
        irt1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "task1", new HashSet<Object>(Arrays.asList("relatedto5"))));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().isRelatedTo(irt1).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, entities.size());
        isRelatedToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            isRelatedToCnt += timelineEntity.getIsRelatedToEntities().size();
            if (!(timelineEntity.getId().equals("application_1111111111_4444"))) {
                Assert.fail("Entity id should have been application_1111111111_4444");
            }
        }
        Assert.assertEquals(0, isRelatedToCnt);
        TimelineFilterList irt2 = new TimelineFilterList(Operator.OR);
        irt2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "task", new HashSet<Object>(Arrays.asList("relatedto1"))));
        irt2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "task2", new HashSet<Object>(Arrays.asList("relatedto4"))));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().isRelatedTo(irt2).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, entities.size());
        isRelatedToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            isRelatedToCnt += timelineEntity.getIsRelatedToEntities().size();
            if ((!(timelineEntity.getId().equals("application_1111111111_2222"))) && (!(timelineEntity.getId().equals("application_1111111111_3333")))) {
                Assert.fail(("Entity ids' should have been application_1111111111_2222" + " and application_1111111111_3333"));
            }
        }
        Assert.assertEquals(0, isRelatedToCnt);
        TimelineFilterList irt3 = new TimelineFilterList();
        irt3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "task1", new HashSet<Object>(Arrays.asList("relatedto3", "relatedto5"))));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().isRelatedTo(irt3).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, entities.size());
        isRelatedToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            isRelatedToCnt += timelineEntity.getIsRelatedToEntities().size();
            if (!(timelineEntity.getId().equals("application_1111111111_3333"))) {
                Assert.fail("Entity id should have been application_1111111111_3333");
            }
        }
        Assert.assertEquals(0, isRelatedToCnt);
        TimelineFilterList irt4 = new TimelineFilterList();
        irt4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "task1", new HashSet<Object>(Arrays.asList("relatedto3"))));
        irt4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "dummy_task", new HashSet<Object>(Arrays.asList("relatedto5"))));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().isRelatedTo(irt4).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(0, entities.size());
        TimelineFilterList irt5 = new TimelineFilterList();
        irt5.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "task1", new HashSet<Object>(Arrays.asList("relatedto3", "relatedto7"))));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().isRelatedTo(irt5).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(0, entities.size());
        TimelineFilterList list1 = new TimelineFilterList();
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "task", new HashSet<Object>(Arrays.asList("relatedto1"))));
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "dummy_task", new HashSet<Object>(Arrays.asList("relatedto4"))));
        TimelineFilterList list2 = new TimelineFilterList();
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "task2", new HashSet<Object>(Arrays.asList("relatedto4"))));
        TimelineFilterList irt6 = new TimelineFilterList(Operator.OR, list1, list2);
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().isRelatedTo(irt6).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, entities.size());
        isRelatedToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            isRelatedToCnt += timelineEntity.getIsRelatedToEntities().size();
            if (!(timelineEntity.getId().equals("application_1111111111_3333"))) {
                Assert.fail("Entity id should have been application_1111111111_3333");
            }
        }
        Assert.assertEquals(0, isRelatedToCnt);
    }

    @Test
    public void testReadAppsRelatesTo() throws Exception {
        TimelineFilterList rt = new TimelineFilterList(Operator.OR);
        rt.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container2", new HashSet<Object>(Arrays.asList("relatesto7"))));
        rt.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container1", new HashSet<Object>(Arrays.asList("relatesto4"))));
        Set<TimelineEntity> entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().relatesTo(rt).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.ALL), null, null, null));
        Assert.assertEquals(2, entities.size());
        int relatesToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            relatesToCnt += timelineEntity.getRelatesToEntities().size();
            if ((!(timelineEntity.getId().equals("application_1111111111_2222"))) && (!(timelineEntity.getId().equals("application_1111111111_4444")))) {
                Assert.fail(("Entity ids' should have been application_1111111111_2222" + " and application_1111111111_4444"));
            }
        }
        Assert.assertEquals(3, relatesToCnt);
        TimelineFilterList rt1 = new TimelineFilterList();
        rt1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container", new HashSet<Object>(Arrays.asList("relatesto1"))));
        rt1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "container", new HashSet<Object>(Arrays.asList("relatesto3"))));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().relatesTo(rt1).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, entities.size());
        relatesToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            relatesToCnt += timelineEntity.getRelatesToEntities().size();
            if (!(timelineEntity.getId().equals("application_1111111111_3333"))) {
                Assert.fail("Entity id should have been application_1111111111_3333");
            }
        }
        Assert.assertEquals(0, relatesToCnt);
        TimelineFilterList rt2 = new TimelineFilterList(Operator.OR);
        rt2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container2", new HashSet<Object>(Arrays.asList("relatesto7"))));
        rt2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container1", new HashSet<Object>(Arrays.asList("relatesto4"))));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().relatesTo(rt2).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, entities.size());
        relatesToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            relatesToCnt += timelineEntity.getRelatesToEntities().size();
            if ((!(timelineEntity.getId().equals("application_1111111111_2222"))) && (!(timelineEntity.getId().equals("application_1111111111_4444")))) {
                Assert.fail(("Entity ids' should have been application_1111111111_2222" + " and application_1111111111_4444"));
            }
        }
        Assert.assertEquals(0, relatesToCnt);
        TimelineFilterList rt3 = new TimelineFilterList();
        rt3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container", new HashSet<Object>(Arrays.asList("relatesto1", "relatesto3"))));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().relatesTo(rt3).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, entities.size());
        relatesToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            relatesToCnt += timelineEntity.getRelatesToEntities().size();
            if (!(timelineEntity.getId().equals("application_1111111111_2222"))) {
                Assert.fail("Entity id should have been application_1111111111_2222");
            }
        }
        Assert.assertEquals(0, relatesToCnt);
        TimelineFilterList rt4 = new TimelineFilterList();
        rt4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container", new HashSet<Object>(Arrays.asList("relatesto1"))));
        rt4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "dummy_container", new HashSet<Object>(Arrays.asList("relatesto5"))));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().relatesTo(rt4).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(0, entities.size());
        TimelineFilterList rt5 = new TimelineFilterList();
        rt5.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container", new HashSet<Object>(Arrays.asList("relatedto1", "relatesto8"))));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().relatesTo(rt5).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(0, entities.size());
        TimelineFilterList list1 = new TimelineFilterList();
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container2", new HashSet<Object>(Arrays.asList("relatesto7"))));
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "dummy_container", new HashSet<Object>(Arrays.asList("relatesto4"))));
        TimelineFilterList list2 = new TimelineFilterList();
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container1", new HashSet<Object>(Arrays.asList("relatesto4"))));
        TimelineFilterList rt6 = new TimelineFilterList(Operator.OR, list1, list2);
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().relatesTo(rt6).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, entities.size());
        relatesToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            relatesToCnt += timelineEntity.getRelatesToEntities().size();
            if (!(timelineEntity.getId().equals("application_1111111111_2222"))) {
                Assert.fail("Entity id should have been application_1111111111_2222");
            }
        }
        Assert.assertEquals(0, relatesToCnt);
        TimelineFilterList list3 = new TimelineFilterList();
        list3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container", new HashSet<Object>(Arrays.asList("relatesto1"))));
        list3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container1", new HashSet<Object>(Arrays.asList("relatesto4"))));
        TimelineFilterList list4 = new TimelineFilterList();
        list4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container", new HashSet<Object>(Arrays.asList("relatesto1"))));
        list4.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container", new HashSet<Object>(Arrays.asList("relatesto2"))));
        TimelineFilterList combinedList = new TimelineFilterList(Operator.OR, list3, list4);
        TimelineFilterList rt7 = new TimelineFilterList(Operator.AND, combinedList, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "container", new HashSet<Object>(Arrays.asList("relatesto3"))));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().relatesTo(rt7).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, entities.size());
        relatesToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            relatesToCnt += timelineEntity.getRelatesToEntities().size();
            if (!(timelineEntity.getId().equals("application_1111111111_3333"))) {
                Assert.fail("Entity id should have been application_1111111111_3333");
            }
        }
        Assert.assertEquals(0, relatesToCnt);
    }

    @Test
    public void testReadAppsRelationsAndEventFiltersDefaultView() throws Exception {
        TimelineFilterList eventFilter = new TimelineFilterList();
        eventFilter.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "end_event"));
        TimelineFilterList relatesTo = new TimelineFilterList(Operator.OR);
        relatesTo.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container2", new HashSet<Object>(Arrays.asList("relatesto7"))));
        relatesTo.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "container1", new HashSet<Object>(Arrays.asList("relatesto4"))));
        TimelineFilterList isRelatedTo = new TimelineFilterList();
        isRelatedTo.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.EQUAL, "task1", new HashSet<Object>(Arrays.asList("relatedto3"))));
        isRelatedTo.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValuesFilter(TimelineCompareOp.NOT_EQUAL, "task1", new HashSet<Object>(Arrays.asList("relatedto5"))));
        Set<TimelineEntity> entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().relatesTo(relatesTo).isRelatedTo(isRelatedTo).eventFilters(eventFilter).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, entities.size());
        int eventCnt = 0;
        int isRelatedToCnt = 0;
        int relatesToCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            eventCnt += timelineEntity.getEvents().size();
            isRelatedToCnt += timelineEntity.getIsRelatedToEntities().size();
            relatesToCnt += timelineEntity.getRelatesToEntities().size();
            if (!(timelineEntity.getId().equals("application_1111111111_4444"))) {
                Assert.fail("Entity id should have been application_1111111111_4444");
            }
        }
        Assert.assertEquals(0, eventCnt);
        Assert.assertEquals(0, isRelatedToCnt);
        Assert.assertEquals(0, relatesToCnt);
    }

    @Test
    public void testReadAppsConfigFilters() throws Exception {
        TimelineFilterList list1 = new TimelineFilterList();
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "cfg_param1", "value1"));
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "cfg_param2", "value2"));
        TimelineFilterList list2 = new TimelineFilterList();
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "cfg_param1", "value3"));
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_param2", "value2"));
        TimelineFilterList confFilterList = new TimelineFilterList(Operator.OR, list1, list2);
        Set<TimelineEntity> entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().configFilters(confFilterList).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(CONFIGS), null, null, null));
        Assert.assertEquals(2, entities.size());
        int cfgCnt = 0;
        for (TimelineEntity entity : entities) {
            cfgCnt += entity.getConfigs().size();
        }
        Assert.assertEquals(5, cfgCnt);
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().configFilters(confFilterList).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.ALL), null, null, null));
        Assert.assertEquals(2, entities.size());
        cfgCnt = 0;
        for (TimelineEntity entity : entities) {
            cfgCnt += entity.getConfigs().size();
        }
        Assert.assertEquals(5, cfgCnt);
        TimelineFilterList confFilterList1 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "cfg_param1", "value1"));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().configFilters(confFilterList1).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(CONFIGS), null, null, null));
        Assert.assertEquals(1, entities.size());
        cfgCnt = 0;
        for (TimelineEntity entity : entities) {
            cfgCnt += entity.getConfigs().size();
        }
        Assert.assertEquals(3, cfgCnt);
        TimelineFilterList confFilterList2 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "cfg_param1", "value1"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "config_param2", "value2"));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().configFilters(confFilterList2).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(CONFIGS), null, null, null));
        Assert.assertEquals(0, entities.size());
        TimelineFilterList confFilterList3 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "dummy_config", "value1"));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().configFilters(confFilterList3).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(CONFIGS), null, null, null));
        Assert.assertEquals(0, entities.size());
        TimelineFilterList confFilterList4 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "dummy_config", "value1"));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().configFilters(confFilterList4).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(CONFIGS), null, null, null));
        Assert.assertEquals(0, entities.size());
        TimelineFilterList confFilterList5 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "dummy_config", "value1", false));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().configFilters(confFilterList5).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(CONFIGS), null, null, null));
        Assert.assertEquals(3, entities.size());
    }

    @Test
    public void testReadAppsEventFilters() throws Exception {
        TimelineFilterList ef = new TimelineFilterList();
        ef.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "update_event"));
        ef.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "end_event"));
        Set<TimelineEntity> entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().eventFilters(ef).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.ALL), null, null, null));
        Assert.assertEquals(1, entities.size());
        int eventCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            eventCnt += timelineEntity.getEvents().size();
            if (!(timelineEntity.getId().equals("application_1111111111_4444"))) {
                Assert.fail("Entity id should have been application_1111111111_4444");
            }
        }
        Assert.assertEquals(1, eventCnt);
        TimelineFilterList ef1 = new TimelineFilterList();
        ef1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "update_event"));
        ef1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "end_event"));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().eventFilters(ef1).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, entities.size());
        eventCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            eventCnt += timelineEntity.getEvents().size();
            if (!(timelineEntity.getId().equals("application_1111111111_4444"))) {
                Assert.fail("Entity id should have been application_1111111111_4444");
            }
        }
        Assert.assertEquals(0, eventCnt);
        TimelineFilterList ef2 = new TimelineFilterList();
        ef2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "end_event"));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().eventFilters(ef2).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(2, entities.size());
        eventCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            eventCnt += timelineEntity.getEvents().size();
            if ((!(timelineEntity.getId().equals("application_1111111111_2222"))) && (!(timelineEntity.getId().equals("application_1111111111_4444")))) {
                Assert.fail(("Entity ids' should have been application_1111111111_2222" + " and application_1111111111_4444"));
            }
        }
        Assert.assertEquals(0, eventCnt);
        TimelineFilterList ef3 = new TimelineFilterList();
        ef3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "update_event"));
        ef3.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "dummy_event"));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().eventFilters(ef3).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(0, entities.size());
        TimelineFilterList list1 = new TimelineFilterList();
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "update_event"));
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "dummy_event"));
        TimelineFilterList list2 = new TimelineFilterList();
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.EQUAL, "start_event"));
        TimelineFilterList ef4 = new TimelineFilterList(Operator.OR, list1, list2);
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().eventFilters(ef4).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, entities.size());
        eventCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            eventCnt += timelineEntity.getEvents().size();
            if (!(timelineEntity.getId().equals("application_1111111111_2222"))) {
                Assert.fail("Entity id should have been application_1111111111_2222");
            }
        }
        Assert.assertEquals(0, eventCnt);
        TimelineFilterList ef5 = new TimelineFilterList();
        ef5.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "update_event"));
        ef5.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineExistsFilter(TimelineCompareOp.NOT_EQUAL, "end_event"));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().eventFilters(ef5).build(), new TimelineDataToRetrieve());
        Assert.assertEquals(1, entities.size());
        eventCnt = 0;
        for (TimelineEntity timelineEntity : entities) {
            eventCnt += timelineEntity.getEvents().size();
            if (!(timelineEntity.getId().equals("application_1111111111_2222"))) {
                Assert.fail("Entity id should have been application_1111111111_2222");
            }
        }
        Assert.assertEquals(0, eventCnt);
    }

    @Test
    public void testReadAppsConfigPrefix() throws Exception {
        TimelineFilterList list = new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.EQUAL, "cfg_"));
        TimelineEntity e1 = reader.getEntity(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, "application_1111111111_2222", YARN_APPLICATION.toString(), null), new TimelineDataToRetrieve(list, null, null, null, null, null));
        Assert.assertNotNull(e1);
        Assert.assertEquals(1, e1.getConfigs().size());
        Set<TimelineEntity> es1 = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().build(), new TimelineDataToRetrieve(list, null, null, null, null, null));
        int cfgCnt = 0;
        for (TimelineEntity entity : es1) {
            cfgCnt += entity.getConfigs().size();
            for (String confKey : entity.getConfigs().keySet()) {
                Assert.assertTrue("Config key returned should start with cfg_", confKey.startsWith("cfg_"));
            }
        }
        Assert.assertEquals(3, cfgCnt);
    }

    @Test
    public void testReadAppsConfigFilterPrefix() throws Exception {
        TimelineFilterList confFilterList = new TimelineFilterList();
        confFilterList.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "cfg_param1", "value1"));
        TimelineFilterList list = new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.EQUAL, "cfg_"));
        Set<TimelineEntity> entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().configFilters(confFilterList).build(), new TimelineDataToRetrieve(list, null, null, null, null, null));
        Assert.assertEquals(1, entities.size());
        int cfgCnt = 0;
        for (TimelineEntity entity : entities) {
            cfgCnt += entity.getConfigs().size();
            for (String confKey : entity.getConfigs().keySet()) {
                Assert.assertTrue("Config key returned should start with cfg_", confKey.startsWith("cfg_"));
            }
        }
        Assert.assertEquals(2, cfgCnt);
        TimelineFilterList list1 = new TimelineFilterList();
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "cfg_param1", "value1"));
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "cfg_param2", "value2"));
        TimelineFilterList list2 = new TimelineFilterList();
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "cfg_param1", "value3"));
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "config_param2", "value2"));
        TimelineFilterList confsToRetrieve = new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.EQUAL, "config_"));
        TimelineFilterList confFilterList1 = new TimelineFilterList(Operator.OR, list1, list2);
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().configFilters(confFilterList1).build(), new TimelineDataToRetrieve(confsToRetrieve, null, null, null, null, null));
        Assert.assertEquals(2, entities.size());
        cfgCnt = 0;
        for (TimelineEntity entity : entities) {
            cfgCnt += entity.getConfigs().size();
            for (String confKey : entity.getConfigs().keySet()) {
                Assert.assertTrue("Config key returned should start with config_", confKey.startsWith("config_"));
            }
        }
        Assert.assertEquals(2, cfgCnt);
    }

    @Test
    public void testReadAppsMetricFilters() throws Exception {
        TimelineFilterList list1 = new TimelineFilterList();
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_OR_EQUAL, "MAP1_SLOT_MILLIS", 50000000900L));
        TimelineFilterList list2 = new TimelineFilterList();
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "MAP_SLOT_MILLIS", 80000000000L));
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.EQUAL, "MAP1_BYTES", 50));
        TimelineFilterList metricFilterList = new TimelineFilterList(Operator.OR, list1, list2);
        Set<TimelineEntity> entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(METRICS), null, null, null));
        Assert.assertEquals(2, entities.size());
        int metricCnt = 0;
        for (TimelineEntity entity : entities) {
            metricCnt += entity.getMetrics().size();
        }
        Assert.assertEquals(3, metricCnt);
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.ALL), null, null, null));
        Assert.assertEquals(2, entities.size());
        metricCnt = 0;
        for (TimelineEntity entity : entities) {
            metricCnt += entity.getMetrics().size();
        }
        Assert.assertEquals(3, metricCnt);
        TimelineFilterList metricFilterList1 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_OR_EQUAL, "MAP_SLOT_MILLIS", 80000000000L), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "MAP1_BYTES", 30));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList1).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(METRICS), null, null, null));
        Assert.assertEquals(1, entities.size());
        metricCnt = 0;
        for (TimelineEntity entity : entities) {
            metricCnt += entity.getMetrics().size();
        }
        Assert.assertEquals(2, metricCnt);
        TimelineFilterList metricFilterList2 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "MAP_SLOT_MILLIS", 40000000000L), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "MAP1_BYTES", 30));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList2).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(METRICS), null, null, null));
        Assert.assertEquals(0, entities.size());
        TimelineFilterList metricFilterList3 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.EQUAL, "dummy_metric", 5));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList3).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(METRICS), null, null, null));
        Assert.assertEquals(0, entities.size());
        TimelineFilterList metricFilterList4 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "dummy_metric", 5));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList4).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(METRICS), null, null, null));
        Assert.assertEquals(0, entities.size());
        TimelineFilterList metricFilterList5 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.NOT_EQUAL, "dummy_metric", 5, false));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList5).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(METRICS), null, null, null));
        Assert.assertEquals(3, entities.size());
    }

    @Test
    public void testReadAppsMetricPrefix() throws Exception {
        TimelineFilterList list = new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.EQUAL, "MAP1_"));
        TimelineEntity e1 = reader.getEntity(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, "application_1111111111_2222", YARN_APPLICATION.toString(), null), new TimelineDataToRetrieve(null, list, null, null, null, null));
        Assert.assertNotNull(e1);
        Assert.assertEquals(1, e1.getMetrics().size());
        Set<TimelineEntity> es1 = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().build(), new TimelineDataToRetrieve(null, list, null, null, null, null));
        int metricCnt = 0;
        for (TimelineEntity entity : es1) {
            metricCnt += entity.getMetrics().size();
            for (TimelineMetric metric : entity.getMetrics()) {
                Assert.assertTrue("Metric Id returned should start with MAP1_", metric.getId().startsWith("MAP1_"));
            }
        }
        Assert.assertEquals(2, metricCnt);
    }

    @Test
    public void testReadAppsMetricFilterPrefix() throws Exception {
        TimelineFilterList list = new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.EQUAL, "MAP1_"));
        TimelineFilterList metricFilterList = new TimelineFilterList();
        metricFilterList.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_OR_EQUAL, "MAP1_SLOT_MILLIS", 0L));
        Set<TimelineEntity> entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList).build(), new TimelineDataToRetrieve(null, list, null, null, null, null));
        int metricCnt = 0;
        Assert.assertEquals(1, entities.size());
        for (TimelineEntity entity : entities) {
            metricCnt += entity.getMetrics().size();
        }
        Assert.assertEquals(1, metricCnt);
        TimelineFilterList list1 = new TimelineFilterList();
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.GREATER_OR_EQUAL, "MAP1_SLOT_MILLIS", 50000000900L));
        TimelineFilterList list2 = new TimelineFilterList();
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.LESS_THAN, "MAP_SLOT_MILLIS", 80000000000L));
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineCompareFilter(TimelineCompareOp.EQUAL, "MAP1_BYTES", 50));
        TimelineFilterList metricsToRetrieve = new TimelineFilterList(Operator.OR, new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelinePrefixFilter(TimelineCompareOp.EQUAL, "MAP1_"));
        TimelineFilterList metricFilterList1 = new TimelineFilterList(Operator.OR, list1, list2);
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList1).build(), new TimelineDataToRetrieve(null, metricsToRetrieve, null, null, null, null));
        metricCnt = 0;
        Assert.assertEquals(2, entities.size());
        for (TimelineEntity entity : entities) {
            metricCnt += entity.getMetrics().size();
            for (TimelineMetric metric : entity.getMetrics()) {
                Assert.assertTrue("Metric Id returned should start with MAP1_", metric.getId().startsWith("MAP1_"));
            }
        }
        Assert.assertEquals(2, metricCnt);
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().metricFilters(metricFilterList1).build(), new TimelineDataToRetrieve(null, metricsToRetrieve, EnumSet.of(METRICS), Integer.MAX_VALUE, null, null));
        metricCnt = 0;
        int metricValCnt = 0;
        Assert.assertEquals(2, entities.size());
        for (TimelineEntity entity : entities) {
            metricCnt += entity.getMetrics().size();
            for (TimelineMetric metric : entity.getMetrics()) {
                metricValCnt += metric.getValues().size();
                Assert.assertTrue("Metric Id returned should start with MAP1_", metric.getId().startsWith("MAP1_"));
            }
        }
        Assert.assertEquals(2, metricCnt);
        Assert.assertEquals(7, metricValCnt);
    }

    @Test
    public void testReadAppsMetricTimeRange() throws Exception {
        Set<TimelineEntity> entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().build(), new TimelineDataToRetrieve(null, null, EnumSet.of(METRICS), 100, null, null));
        Assert.assertEquals(3, entities.size());
        int metricTimeSeriesCnt = 0;
        int metricCnt = 0;
        for (TimelineEntity entity : entities) {
            metricCnt += entity.getMetrics().size();
            for (TimelineMetric m : entity.getMetrics()) {
                metricTimeSeriesCnt += m.getValues().size();
            }
        }
        Assert.assertEquals(3, metricCnt);
        Assert.assertEquals(13, metricTimeSeriesCnt);
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().build(), new TimelineDataToRetrieve(null, null, EnumSet.of(METRICS), 100, ((TestHBaseTimelineStorageApps.CURRENT_TIME) - 40000), TestHBaseTimelineStorageApps.CURRENT_TIME));
        Assert.assertEquals(3, entities.size());
        metricCnt = 0;
        metricTimeSeriesCnt = 0;
        for (TimelineEntity entity : entities) {
            metricCnt += entity.getMetrics().size();
            for (TimelineMetric m : entity.getMetrics()) {
                for (Long ts : m.getValues().keySet()) {
                    Assert.assertTrue(((ts >= ((TestHBaseTimelineStorageApps.CURRENT_TIME) - 40000)) && (ts <= (TestHBaseTimelineStorageApps.CURRENT_TIME))));
                }
                metricTimeSeriesCnt += m.getValues().size();
            }
        }
        Assert.assertEquals(3, metricCnt);
        Assert.assertEquals(5, metricTimeSeriesCnt);
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().build(), new TimelineDataToRetrieve(null, null, EnumSet.of(METRICS), null, ((TestHBaseTimelineStorageApps.CURRENT_TIME) - 40000), TestHBaseTimelineStorageApps.CURRENT_TIME));
        Assert.assertEquals(3, entities.size());
        metricCnt = 0;
        metricTimeSeriesCnt = 0;
        for (TimelineEntity entity : entities) {
            metricCnt += entity.getMetrics().size();
            for (TimelineMetric m : entity.getMetrics()) {
                for (Long ts : m.getValues().keySet()) {
                    Assert.assertTrue(((ts >= ((TestHBaseTimelineStorageApps.CURRENT_TIME) - 40000)) && (ts <= (TestHBaseTimelineStorageApps.CURRENT_TIME))));
                }
                metricTimeSeriesCnt += m.getValues().size();
            }
        }
        Assert.assertEquals(3, metricCnt);
        Assert.assertEquals(3, metricTimeSeriesCnt);
        TimelineEntity entity = reader.getEntity(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, "application_1111111111_2222", YARN_APPLICATION.toString(), null), new TimelineDataToRetrieve(null, null, EnumSet.of(METRICS), 100, ((TestHBaseTimelineStorageApps.CURRENT_TIME) - 40000), TestHBaseTimelineStorageApps.CURRENT_TIME));
        Assert.assertNotNull(entity);
        Assert.assertEquals(2, entity.getMetrics().size());
        metricTimeSeriesCnt = 0;
        for (TimelineMetric m : entity.getMetrics()) {
            for (Long ts : m.getValues().keySet()) {
                Assert.assertTrue(((ts >= ((TestHBaseTimelineStorageApps.CURRENT_TIME) - 40000)) && (ts <= (TestHBaseTimelineStorageApps.CURRENT_TIME))));
            }
            metricTimeSeriesCnt += m.getValues().size();
        }
        Assert.assertEquals(3, metricTimeSeriesCnt);
    }

    @Test
    public void testReadAppsInfoFilters() throws Exception {
        TimelineFilterList list1 = new TimelineFilterList();
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "infoMapKey3", 85.85));
        list1.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "infoMapKey1", "infoMapValue2"));
        TimelineFilterList list2 = new TimelineFilterList();
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "infoMapKey1", "infoMapValue1"));
        list2.addFilter(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "infoMapKey2", 10));
        TimelineFilterList infoFilterList = new TimelineFilterList(Operator.OR, list1, list2);
        Set<TimelineEntity> entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().infoFilters(infoFilterList).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.INFO), null, null, null));
        Assert.assertEquals(2, entities.size());
        int infoCnt = 0;
        for (TimelineEntity entity : entities) {
            infoCnt += entity.getInfo().size();
        }
        Assert.assertEquals(7, infoCnt);
        TimelineFilterList infoFilterList1 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "infoMapKey1", "infoMapValue1"));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().infoFilters(infoFilterList1).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.INFO), null, null, null));
        Assert.assertEquals(1, entities.size());
        infoCnt = 0;
        for (TimelineEntity entity : entities) {
            infoCnt += entity.getInfo().size();
        }
        Assert.assertEquals(4, infoCnt);
        TimelineFilterList infoFilterList2 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "infoMapKey1", "infoMapValue2"), new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "infoMapKey3", 85.85));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().infoFilters(infoFilterList2).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.INFO), null, null, null));
        Assert.assertEquals(0, entities.size());
        TimelineFilterList infoFilterList3 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.EQUAL, "dummy_info", "some_value"));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().infoFilters(infoFilterList3).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.INFO), null, null, null));
        Assert.assertEquals(0, entities.size());
        TimelineFilterList infoFilterList4 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "dummy_info", "some_value"));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().infoFilters(infoFilterList4).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.INFO), null, null, null));
        Assert.assertEquals(0, entities.size());
        TimelineFilterList infoFilterList5 = new TimelineFilterList(new org.apache.hadoop.yarn.server.timelineservice.reader.filter.TimelineKeyValueFilter(TimelineCompareOp.NOT_EQUAL, "dummy_info", "some_value", false));
        entities = reader.getEntities(new org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderContext("cluster1", "user1", "some_flow_name", 1002345678919L, null, YARN_APPLICATION.toString(), null), new TimelineEntityFilters.Builder().infoFilters(infoFilterList5).build(), new TimelineDataToRetrieve(null, null, EnumSet.of(Field.INFO), null, null, null));
        Assert.assertEquals(3, entities.size());
    }
}

