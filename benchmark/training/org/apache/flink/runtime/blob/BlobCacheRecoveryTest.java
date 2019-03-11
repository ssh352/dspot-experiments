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
package org.apache.flink.runtime.blob;


import BlobServerOptions.STORAGE_DIRECTORY;
import HighAvailabilityOptions.HA_MODE;
import HighAvailabilityOptions.HA_STORAGE_PATH;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.TestLogger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the recovery of files of a {@link BlobCacheService} from a HA store.
 */
public class BlobCacheRecoveryTest extends TestLogger {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Tests that with {@link HighAvailabilityMode#ZOOKEEPER} distributed JARs are recoverable from any
     * participating BlobServer.
     */
    @Test
    public void testBlobCacheRecovery() throws Exception {
        Configuration config = new Configuration();
        config.setString(HA_MODE, "ZOOKEEPER");
        config.setString(STORAGE_DIRECTORY, temporaryFolder.newFolder().getAbsolutePath());
        config.setString(HA_STORAGE_PATH, temporaryFolder.newFolder().getPath());
        BlobStoreService blobStoreService = null;
        try {
            blobStoreService = BlobUtils.createBlobStoreFromConfig(config);
            BlobCacheRecoveryTest.testBlobCacheRecovery(config, blobStoreService);
        } finally {
            if (blobStoreService != null) {
                blobStoreService.closeAndCleanupAllData();
            }
        }
    }
}

