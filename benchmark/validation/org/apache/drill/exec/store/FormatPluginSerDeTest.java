/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store;


import ExecConstants.SLICE_TARGET;
import java.nio.file.Paths;
import org.apache.drill.PlanTestBase;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.store.avro.AvroTestUtil;
import org.apache.drill.exec.util.StoragePluginTestUtils;
import org.apache.drill.test.BaseTestQuery;
import org.junit.Test;


public class FormatPluginSerDeTest extends PlanTestBase {
    @Test
    public void testParquet() throws Exception {
        try {
            BaseTestQuery.setSessionOption(SLICE_TARGET, 1);
            testPhysicalPlanSubmission(String.format("select * from table(cp.`%s`(type=>'parquet'))", "parquet/alltypes_required.parquet"), String.format("select * from table(cp.`%s`(type=>'parquet', autoCorrectCorruptDates=>false))", "parquet/alltypes_required.parquet"), String.format("select * from table(cp.`%s`(type=>'parquet', autoCorrectCorruptDates=>true))", "parquet/alltypes_required.parquet"), String.format("select * from table(cp.`%s`(type=>'parquet', autoCorrectCorruptDates=>true, enableStringsSignedMinMax=>false))", "parquet/alltypes_required.parquet"), String.format("select * from table(cp.`%s`(type=>'parquet', autoCorrectCorruptDates=>false, enableStringsSignedMinMax=>true))", "parquet/alltypes_required.parquet"));
        } finally {
            BaseTestQuery.resetSessionOption(SLICE_TARGET);
        }
    }

    @Test
    public void testParquetWithMetadata() throws Exception {
        String tableName = "alltypes_required_with_metadata";
        BaseTestQuery.test("use %s", StoragePluginTestUtils.DFS_TMP_SCHEMA);
        try {
            BaseTestQuery.test("create table %s as select * from cp.`parquet/alltypes_required.parquet`", tableName);
            BaseTestQuery.test("refresh table metadata %s", tableName);
            BaseTestQuery.setSessionOption(SLICE_TARGET, 1);
            testPhysicalPlanSubmission(String.format("select * from table(`%s`(type=>'parquet'))", tableName), String.format("select * from table(`%s`(type=>'parquet', autoCorrectCorruptDates=>false))", tableName), String.format("select * from table(`%s`(type=>'parquet', autoCorrectCorruptDates=>true))", tableName));
        } finally {
            BaseTestQuery.resetSessionOption(SLICE_TARGET);
            BaseTestQuery.test("drop table if exists %s", tableName);
        }
    }

    @Test
    public void testAvro() throws Exception {
        AvroTestUtil.AvroTestRecordWriter testSetup = AvroTestUtil.generateSimplePrimitiveSchema_NoNullValues(5);
        String file = testSetup.getFileName();
        testPhysicalPlanSubmission(String.format("select * from dfs.`%s`", file), String.format("select * from table(dfs.`%s`(type=>'avro'))", file));
    }

    @Test
    public void testSequenceFile() throws Exception {
        String path = "sequencefiles/simple.seq";
        ExecTest.dirTestWatcher.copyResourceToRoot(Paths.get(path));
        testPhysicalPlanSubmission(String.format("select * from dfs.`%s`", path), String.format("select * from table(dfs.`%s`(type=>'sequencefile'))", path));
    }

    @Test
    public void testPcap() throws Exception {
        String path = "store/pcap/tcp-1.pcap";
        ExecTest.dirTestWatcher.copyResourceToRoot(Paths.get(path));
        testPhysicalPlanSubmission(String.format("select * from dfs.`%s`", path), String.format("select * from table(dfs.`%s`(type=>'pcap'))", path));
    }

    @Test
    public void testHttpd() throws Exception {
        String path = "store/httpd/dfs-bootstrap.httpd";
        ExecTest.dirTestWatcher.copyResourceToRoot(Paths.get(path));
        String logFormat = "%h %t \"%r\" %>s %b \"%{Referer}i\"";
        String timeStampFormat = "dd/MMM/yyyy:HH:mm:ss ZZ";
        testPhysicalPlanSubmission(String.format("select * from dfs.`%s`", path), String.format("select * from table(dfs.`%s`(type=>'httpd', logFormat=>'%s'))", path, logFormat), String.format("select * from table(dfs.`%s`(type=>'httpd', logFormat=>'%s', timestampFormat=>'%s'))", path, logFormat, timeStampFormat));
    }

    @Test
    public void testJson() throws Exception {
        testPhysicalPlanSubmission("select * from cp.`donuts.json`", "select * from table(cp.`donuts.json`(type=>'json'))");
    }

    @Test
    public void testText() throws Exception {
        String path = "store/text/data/regions.csv";
        ExecTest.dirTestWatcher.copyResourceToRoot(Paths.get(path));
        testPhysicalPlanSubmission(String.format("select * from table(dfs.`%s`(type => 'text'))", path), String.format("select * from table(dfs.`%s`(type => 'text', extractHeader => false, fieldDelimiter => 'A'))", path));
    }

    @Test
    public void testNamed() throws Exception {
        String path = "store/text/WithQuote.tbl";
        ExecTest.dirTestWatcher.copyResourceToRoot(Paths.get(path));
        String query = String.format("select * from table(dfs.`%s`(type=>'named', name=>'psv'))", path);
        testPhysicalPlanSubmission(query);
    }
}

