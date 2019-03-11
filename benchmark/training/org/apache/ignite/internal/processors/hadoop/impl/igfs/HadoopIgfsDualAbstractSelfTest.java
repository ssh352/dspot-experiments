/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.hadoop.impl.igfs;


import IgfsIpcEndpointType.TCP;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Callable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.igfs.IgfsInputStream;
import org.apache.ignite.igfs.IgfsIpcEndpointConfiguration;
import org.apache.ignite.igfs.IgfsMode;
import org.apache.ignite.igfs.IgfsOutputStream;
import org.apache.ignite.igfs.IgfsPath;
import org.apache.ignite.internal.processors.hadoop.impl.fs.HadoopParameters;
import org.apache.ignite.internal.processors.igfs.IgfsBlockKey;
import org.apache.ignite.internal.processors.igfs.IgfsCommonAbstractTest;
import org.apache.ignite.internal.processors.igfs.IgfsEntryInfo;
import org.apache.ignite.internal.processors.igfs.IgfsImpl;
import org.apache.ignite.internal.processors.igfs.IgfsMetaManager;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Tests for IGFS working in mode when remote file system exists: DUAL_SYNC, DUAL_ASYNC.
 */
public abstract class HadoopIgfsDualAbstractSelfTest extends IgfsCommonAbstractTest {
    /**
     * IGFS block size.
     */
    protected static final int IGFS_BLOCK_SIZE = 512 * 1024;

    /**
     * Amount of blocks to prefetch.
     */
    protected static final int PREFETCH_BLOCKS = 1;

    /**
     * Amount of sequential block reads before prefetch is triggered.
     */
    protected static final int SEQ_READS_BEFORE_PREFETCH = 2;

    /**
     * Secondary file system URI.
     */
    protected static final String SECONDARY_URI = "igfs://igfs-secondary@127.0.0.1:11500/";

    /**
     * Secondary file system configuration path.
     */
    protected static final String SECONDARY_CFG = "modules/core/src/test/config/hadoop/core-site-loopback-secondary.xml";

    /**
     * Primary file system URI.
     */
    protected static final String PRIMARY_URI = "igfs://igfs@/";

    /**
     * Primary file system configuration path.
     */
    protected static final String PRIMARY_CFG = "modules/core/src/test/config/hadoop/core-site-loopback.xml";

    /**
     * Primary file system REST endpoint configuration map.
     */
    protected static final IgfsIpcEndpointConfiguration PRIMARY_REST_CFG;

    /**
     * Secondary file system REST endpoint configuration map.
     */
    protected static final IgfsIpcEndpointConfiguration SECONDARY_REST_CFG;

    /**
     * Directory.
     */
    protected static final IgfsPath DIR = new IgfsPath("/dir");

    /**
     * Sub-directory.
     */
    protected static final IgfsPath SUBDIR = new IgfsPath(HadoopIgfsDualAbstractSelfTest.DIR, "subdir");

    /**
     * File.
     */
    protected static final IgfsPath FILE = new IgfsPath(HadoopIgfsDualAbstractSelfTest.SUBDIR, "file");

    /**
     * Default data chunk (128 bytes).
     */
    protected static byte[] chunk;

    /**
     * Primary IGFS.
     */
    protected static IgfsImpl igfs;

    /**
     * Secondary IGFS.
     */
    protected static IgfsImpl igfsSecondary;

    /**
     * IGFS mode.
     */
    protected static IgfsMode mode;

    static {
        PRIMARY_REST_CFG = new IgfsIpcEndpointConfiguration();
        HadoopIgfsDualAbstractSelfTest.PRIMARY_REST_CFG.setType(TCP);
        HadoopIgfsDualAbstractSelfTest.PRIMARY_REST_CFG.setPort(10500);
        SECONDARY_REST_CFG = new IgfsIpcEndpointConfiguration();
        HadoopIgfsDualAbstractSelfTest.SECONDARY_REST_CFG.setType(TCP);
        HadoopIgfsDualAbstractSelfTest.SECONDARY_REST_CFG.setPort(11500);
    }

    /**
     * Constructor.
     *
     * @param mode
     * 		IGFS mode.
     */
    protected HadoopIgfsDualAbstractSelfTest(IgfsMode mode) {
        HadoopIgfsDualAbstractSelfTest.mode = mode;
        assert (mode == (DUAL_SYNC)) || (mode == (DUAL_ASYNC));
    }

    /**
     * Check how prefetch override works.
     *
     * @throws Exception
     * 		IF failed.
     */
    @Test
    public void testOpenPrefetchOverride() throws Exception {
        create(HadoopIgfsDualAbstractSelfTest.igfsSecondary, paths(HadoopIgfsDualAbstractSelfTest.DIR, HadoopIgfsDualAbstractSelfTest.SUBDIR), paths(HadoopIgfsDualAbstractSelfTest.FILE));
        // Write enough data to the secondary file system.
        final int blockSize = HadoopIgfsDualAbstractSelfTest.IGFS_BLOCK_SIZE;
        IgfsOutputStream out = HadoopIgfsDualAbstractSelfTest.igfsSecondary.append(HadoopIgfsDualAbstractSelfTest.FILE, false);
        int totalWritten = 0;
        while (totalWritten < ((blockSize * 2) + (HadoopIgfsDualAbstractSelfTest.chunk.length))) {
            out.write(HadoopIgfsDualAbstractSelfTest.chunk);
            totalWritten += HadoopIgfsDualAbstractSelfTest.chunk.length;
        } 
        out.close();
        awaitFileClose(HadoopIgfsDualAbstractSelfTest.igfsSecondary, HadoopIgfsDualAbstractSelfTest.FILE);
        // Instantiate file system with overridden "seq reads before prefetch" property.
        Configuration cfg = new Configuration();
        cfg.addResource(U.resolveIgniteUrl(HadoopIgfsDualAbstractSelfTest.PRIMARY_CFG));
        int seqReads = (HadoopIgfsDualAbstractSelfTest.SEQ_READS_BEFORE_PREFETCH) + 1;
        cfg.setInt(String.format(HadoopParameters.PARAM_IGFS_SEQ_READS_BEFORE_PREFETCH, "igfs@"), seqReads);
        FileSystem fs = FileSystem.get(new URI(HadoopIgfsDualAbstractSelfTest.PRIMARY_URI), cfg);
        // Read the first two blocks.
        Path fsHome = new Path(HadoopIgfsDualAbstractSelfTest.PRIMARY_URI);
        Path dir = new Path(fsHome, HadoopIgfsDualAbstractSelfTest.DIR.name());
        Path subdir = new Path(dir, HadoopIgfsDualAbstractSelfTest.SUBDIR.name());
        Path file = new Path(subdir, HadoopIgfsDualAbstractSelfTest.FILE.name());
        FSDataInputStream fsIn = fs.open(file);
        final byte[] readBuf = new byte[blockSize * 2];
        fsIn.readFully(0, readBuf, 0, readBuf.length);
        // Wait for a while for prefetch to finish (if any).
        IgfsMetaManager meta = HadoopIgfsDualAbstractSelfTest.igfs.context().meta();
        IgfsEntryInfo info = meta.info(meta.fileId(HadoopIgfsDualAbstractSelfTest.FILE));
        IgfsBlockKey key = new IgfsBlockKey(info.id(), info.affinityKey(), info.evictExclude(), 2);
        IgniteCache<IgfsBlockKey, byte[]> dataCache = HadoopIgfsDualAbstractSelfTest.igfs.context().kernalContext().cache().jcache(HadoopIgfsDualAbstractSelfTest.igfs.configuration().getDataCacheConfiguration().getName());
        for (int i = 0; i < 10; i++) {
            if (dataCache.containsKey(key))
                break;
            else
                U.sleep(100);

        }
        fsIn.close();
        // Remove the file from the secondary file system.
        HadoopIgfsDualAbstractSelfTest.igfsSecondary.delete(HadoopIgfsDualAbstractSelfTest.FILE, false);
        // Try reading the third block. Should fail.
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                IgfsInputStream in0 = HadoopIgfsDualAbstractSelfTest.igfs.open(HadoopIgfsDualAbstractSelfTest.FILE);
                in0.seek((blockSize * 2));
                try {
                    in0.read(readBuf);
                } finally {
                    U.closeQuiet(in0);
                }
                return null;
            }
        }, IOException.class, "Failed to read data due to secondary file system exception: /dir/subdir/file");
    }
}

