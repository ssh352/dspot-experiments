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
package org.apache.drill.storage;


import java.util.Collection;
import org.apache.drill.common.config.DrillConfig;
import org.apache.drill.common.logical.LogicalPlan;
import org.apache.drill.common.logical.StoragePluginConfig;
import org.apache.drill.common.scanner.ClassPathScanner;
import org.apache.drill.common.scanner.persistence.ScanResult;
import org.apache.drill.common.util.DrillFileUtils;
import org.apache.drill.test.DrillTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CheckStorageConfig extends DrillTest {
    static final Logger logger = LoggerFactory.getLogger(CheckStorageConfig.class);

    @Test
    public void ensureStorageEnginePickup() {
        DrillConfig config = DrillConfig.create();
        ScanResult scan = ClassPathScanner.fromPrescan(config);
        Collection<?> engines = scan.getImplementations(StoragePluginConfig.class);
        Assert.assertEquals(engines.size(), 1);
    }

    @Test
    public void checkPlanParsing() throws Exception {
        DrillConfig config = DrillConfig.create();
        ScanResult scan = ClassPathScanner.fromPrescan(config);
        LogicalPlan plan = LogicalPlan.parse(new org.apache.drill.common.config.LogicalPlanPersistence(config, scan), DrillFileUtils.getResourceAsString("/storage_engine_plan.json"));
    }
}

