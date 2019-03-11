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
package org.apache.hadoop.hdfs.nfs.nfs3;


import COMMIT_STATUS.COMMIT_DO_SYNC;
import COMMIT_STATUS.COMMIT_FINISHED;
import COMMIT_STATUS.COMMIT_INACTIVE_CTX;
import COMMIT_STATUS.COMMIT_INACTIVE_WITH_PENDING_WRITE;
import COMMIT_STATUS.COMMIT_SPECIAL_SUCCESS;
import COMMIT_STATUS.COMMIT_SPECIAL_WAIT;
import COMMIT_STATUS.COMMIT_WAIT;
import Nfs3Status.NFS3ERR_IO;
import Nfs3Status.NFS3ERR_JUKEBOX;
import Nfs3Status.NFS3_OK;
import NfsConfigKeys.DFS_NFS_SERVER_PORT_DEFAULT;
import NfsConfigKeys.DFS_NFS_SERVER_PORT_KEY;
import NfsConfigKeys.LARGE_FILE_UPLOAD;
import WriteCtx.DataState;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentNavigableMap;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSUtilClient;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.HdfsDataOutputStream;
import org.apache.hadoop.hdfs.nfs.conf.NfsConfiguration;
import org.apache.hadoop.hdfs.nfs.nfs3.OpenFileCtx.COMMIT_STATUS;
import org.apache.hadoop.hdfs.nfs.nfs3.OpenFileCtx.CommitCtx;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.nfs.nfs3.FileHandle;
import org.apache.hadoop.nfs.nfs3.Nfs3Constant;
import org.apache.hadoop.nfs.nfs3.Nfs3Constant.WriteStableHow;
import org.apache.hadoop.nfs.nfs3.Nfs3FileAttributes;
import org.apache.hadoop.nfs.nfs3.request.CREATE3Request;
import org.apache.hadoop.nfs.nfs3.request.READ3Request;
import org.apache.hadoop.nfs.nfs3.request.SetAttr3;
import org.apache.hadoop.nfs.nfs3.request.WRITE3Request;
import org.apache.hadoop.nfs.nfs3.response.CREATE3Response;
import org.apache.hadoop.nfs.nfs3.response.READ3Response;
import org.apache.hadoop.oncrpc.XDR;
import org.apache.hadoop.oncrpc.security.SecurityHandler;
import org.apache.hadoop.security.authorize.DefaultImpersonationProvider;
import org.apache.hadoop.security.authorize.ProxyUsers;
import org.jboss.netty.channel.Channel;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static WriteCtx.INVALID_ORIGINAL_COUNT;


public class TestWrites {
    @Test
    public void testAlterWriteRequest() throws IOException {
        int len = 20;
        byte[] data = new byte[len];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        for (int i = 0; i < len; i++) {
            buffer.put(((byte) (i)));
        }
        buffer.flip();
        int originalCount = buffer.array().length;
        WRITE3Request request = new WRITE3Request(new FileHandle(), 0, data.length, WriteStableHow.UNSTABLE, buffer);
        WriteCtx writeCtx1 = new WriteCtx(request.getHandle(), request.getOffset(), request.getCount(), INVALID_ORIGINAL_COUNT, request.getStableHow(), request.getData(), null, 1, false, DataState.NO_DUMP);
        Assert.assertTrue(((writeCtx1.getData().array().length) == originalCount));
        // Now change the write request
        OpenFileCtx.alterWriteRequest(request, 12);
        WriteCtx writeCtx2 = new WriteCtx(request.getHandle(), request.getOffset(), request.getCount(), originalCount, request.getStableHow(), request.getData(), null, 2, false, DataState.NO_DUMP);
        ByteBuffer appendedData = writeCtx2.getData();
        int position = appendedData.position();
        int limit = appendedData.limit();
        Assert.assertTrue((position == 12));
        Assert.assertTrue(((limit - position) == 8));
        Assert.assertTrue(((appendedData.get(position)) == ((byte) (12))));
        Assert.assertTrue(((appendedData.get((position + 1))) == ((byte) (13))));
        Assert.assertTrue(((appendedData.get((position + 2))) == ((byte) (14))));
        Assert.assertTrue(((appendedData.get((position + 7))) == ((byte) (19))));
        // Test current file write offset is at boundaries
        buffer.position(0);
        request = new WRITE3Request(new FileHandle(), 0, data.length, WriteStableHow.UNSTABLE, buffer);
        OpenFileCtx.alterWriteRequest(request, 1);
        WriteCtx writeCtx3 = new WriteCtx(request.getHandle(), request.getOffset(), request.getCount(), originalCount, request.getStableHow(), request.getData(), null, 2, false, DataState.NO_DUMP);
        appendedData = writeCtx3.getData();
        position = appendedData.position();
        limit = appendedData.limit();
        Assert.assertTrue((position == 1));
        Assert.assertTrue(((limit - position) == 19));
        Assert.assertTrue(((appendedData.get(position)) == ((byte) (1))));
        Assert.assertTrue(((appendedData.get((position + 18))) == ((byte) (19))));
        // Reset buffer position before test another boundary
        buffer.position(0);
        request = new WRITE3Request(new FileHandle(), 0, data.length, WriteStableHow.UNSTABLE, buffer);
        OpenFileCtx.alterWriteRequest(request, 19);
        WriteCtx writeCtx4 = new WriteCtx(request.getHandle(), request.getOffset(), request.getCount(), originalCount, request.getStableHow(), request.getData(), null, 2, false, DataState.NO_DUMP);
        appendedData = writeCtx4.getData();
        position = appendedData.position();
        limit = appendedData.limit();
        Assert.assertTrue((position == 19));
        Assert.assertTrue(((limit - position) == 1));
        Assert.assertTrue(((appendedData.get(position)) == ((byte) (19))));
    }

    // Validate all the commit check return codes OpenFileCtx.COMMIT_STATUS, which
    // includes COMMIT_FINISHED, COMMIT_WAIT, COMMIT_INACTIVE_CTX,
    // COMMIT_INACTIVE_WITH_PENDING_WRITE, COMMIT_ERROR, and COMMIT_DO_SYNC.
    @Test
    public void testCheckCommit() throws IOException {
        DFSClient dfsClient = Mockito.mock(DFSClient.class);
        Nfs3FileAttributes attr = new Nfs3FileAttributes();
        HdfsDataOutputStream fos = Mockito.mock(HdfsDataOutputStream.class);
        Mockito.when(fos.getPos()).thenReturn(((long) (0)));
        NfsConfiguration conf = new NfsConfiguration();
        conf.setBoolean(LARGE_FILE_UPLOAD, false);
        OpenFileCtx ctx = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(conf), false, conf);
        COMMIT_STATUS ret;
        // Test inactive open file context
        ctx.setActiveStatusForTest(false);
        Channel ch = Mockito.mock(Channel.class);
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_INACTIVE_CTX)));
        ctx.getPendingWritesForTest().put(new OffsetRange(5, 10), new WriteCtx(null, 0, 0, 0, null, null, null, 0, false, null));
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_INACTIVE_WITH_PENDING_WRITE)));
        // Test request with non zero commit offset
        ctx.setActiveStatusForTest(true);
        Mockito.when(fos.getPos()).thenReturn(((long) (10)));
        ctx.setNextOffsetForTest(10);
        COMMIT_STATUS status = ctx.checkCommitInternal(5, null, 1, attr, false);
        Assert.assertTrue((status == (COMMIT_STATUS.COMMIT_DO_SYNC)));
        // Do_SYNC state will be updated to FINISHED after data sync
        ret = ctx.checkCommit(dfsClient, 5, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_FINISHED)));
        status = ctx.checkCommitInternal(10, ch, 1, attr, false);
        Assert.assertTrue((status == (COMMIT_STATUS.COMMIT_DO_SYNC)));
        ret = ctx.checkCommit(dfsClient, 10, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_FINISHED)));
        ConcurrentNavigableMap<Long, CommitCtx> commits = ctx.getPendingCommitsForTest();
        Assert.assertTrue(((commits.size()) == 0));
        ret = ctx.checkCommit(dfsClient, 11, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_WAIT)));
        Assert.assertTrue(((commits.size()) == 1));
        long key = commits.firstKey();
        Assert.assertTrue((key == 11));
        // Test request with zero commit offset
        commits.remove(new Long(11));
        // There is one pending write [5,10]
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_WAIT)));
        Assert.assertTrue(((commits.size()) == 1));
        key = commits.firstKey();
        Assert.assertTrue((key == 9));
        // Empty pending writes
        ctx.getPendingWritesForTest().remove(new OffsetRange(5, 10));
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_FINISHED)));
    }

    // Validate all the commit check return codes OpenFileCtx.COMMIT_STATUS with
    // large file upload option.
    @Test
    public void testCheckCommitLargeFileUpload() throws IOException {
        DFSClient dfsClient = Mockito.mock(DFSClient.class);
        Nfs3FileAttributes attr = new Nfs3FileAttributes();
        HdfsDataOutputStream fos = Mockito.mock(HdfsDataOutputStream.class);
        Mockito.when(fos.getPos()).thenReturn(((long) (0)));
        NfsConfiguration conf = new NfsConfiguration();
        conf.setBoolean(LARGE_FILE_UPLOAD, true);
        OpenFileCtx ctx = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(conf), false, conf);
        COMMIT_STATUS ret;
        // Test inactive open file context
        ctx.setActiveStatusForTest(false);
        Channel ch = Mockito.mock(Channel.class);
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_INACTIVE_CTX)));
        ctx.getPendingWritesForTest().put(new OffsetRange(10, 15), new WriteCtx(null, 0, 0, 0, null, null, null, 0, false, null));
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_INACTIVE_WITH_PENDING_WRITE)));
        // Test request with non zero commit offset
        ctx.setActiveStatusForTest(true);
        Mockito.when(fos.getPos()).thenReturn(((long) (8)));
        ctx.setNextOffsetForTest(10);
        COMMIT_STATUS status = ctx.checkCommitInternal(5, null, 1, attr, false);
        Assert.assertTrue((status == (COMMIT_STATUS.COMMIT_DO_SYNC)));
        // Do_SYNC state will be updated to FINISHED after data sync
        ret = ctx.checkCommit(dfsClient, 5, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_FINISHED)));
        // Test commit sequential writes
        status = ctx.checkCommitInternal(10, ch, 1, attr, false);
        Assert.assertTrue((status == (COMMIT_STATUS.COMMIT_SPECIAL_WAIT)));
        ret = ctx.checkCommit(dfsClient, 10, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_SPECIAL_WAIT)));
        // Test commit non-sequential writes
        ConcurrentNavigableMap<Long, CommitCtx> commits = ctx.getPendingCommitsForTest();
        Assert.assertTrue(((commits.size()) == 1));
        ret = ctx.checkCommit(dfsClient, 16, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_SPECIAL_SUCCESS)));
        Assert.assertTrue(((commits.size()) == 1));
        // Test request with zero commit offset
        commits.remove(new Long(10));
        // There is one pending write [10,15]
        ret = ctx.checkCommitInternal(0, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_SPECIAL_WAIT)));
        ret = ctx.checkCommitInternal(9, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_SPECIAL_WAIT)));
        Assert.assertTrue(((commits.size()) == 2));
        // Empty pending writes. nextOffset=10, flushed pos=8
        ctx.getPendingWritesForTest().remove(new OffsetRange(10, 15));
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_SPECIAL_WAIT)));
        // Empty pending writes
        ctx.setNextOffsetForTest(((long) (8)));// flushed pos = 8

        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, false);
        Assert.assertTrue((ret == (COMMIT_STATUS.COMMIT_FINISHED)));
    }

    @Test
    public void testCheckCommitAixCompatMode() throws IOException {
        DFSClient dfsClient = Mockito.mock(DFSClient.class);
        Nfs3FileAttributes attr = new Nfs3FileAttributes();
        HdfsDataOutputStream fos = Mockito.mock(HdfsDataOutputStream.class);
        NfsConfiguration conf = new NfsConfiguration();
        conf.setBoolean(LARGE_FILE_UPLOAD, false);
        // Enable AIX compatibility mode.
        OpenFileCtx ctx = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(new NfsConfiguration()), true, conf);
        // Test fall-through to pendingWrites check in the event that commitOffset
        // is greater than the number of bytes we've so far flushed.
        Mockito.when(fos.getPos()).thenReturn(((long) (2)));
        COMMIT_STATUS status = ctx.checkCommitInternal(5, null, 1, attr, false);
        Assert.assertTrue((status == (COMMIT_STATUS.COMMIT_FINISHED)));
        // Test the case when we actually have received more bytes than we're trying
        // to commit.
        ctx.getPendingWritesForTest().put(new OffsetRange(0, 10), new WriteCtx(null, 0, 0, 0, null, null, null, 0, false, null));
        Mockito.when(fos.getPos()).thenReturn(((long) (10)));
        ctx.setNextOffsetForTest(((long) (10)));
        status = ctx.checkCommitInternal(5, null, 1, attr, false);
        Assert.assertTrue((status == (COMMIT_STATUS.COMMIT_DO_SYNC)));
    }

    // Validate all the commit check return codes OpenFileCtx.COMMIT_STATUS, which
    // includes COMMIT_FINISHED, COMMIT_WAIT, COMMIT_INACTIVE_CTX,
    // COMMIT_INACTIVE_WITH_PENDING_WRITE, COMMIT_ERROR, and COMMIT_DO_SYNC.
    @Test
    public void testCheckCommitFromRead() throws IOException {
        DFSClient dfsClient = Mockito.mock(DFSClient.class);
        Nfs3FileAttributes attr = new Nfs3FileAttributes();
        HdfsDataOutputStream fos = Mockito.mock(HdfsDataOutputStream.class);
        Mockito.when(fos.getPos()).thenReturn(((long) (0)));
        NfsConfiguration config = new NfsConfiguration();
        config.setBoolean(LARGE_FILE_UPLOAD, false);
        OpenFileCtx ctx = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(config), false, config);
        FileHandle h = new FileHandle(1);// fake handle for "/dumpFilePath"

        COMMIT_STATUS ret;
        WriteManager wm = new WriteManager(new org.apache.hadoop.security.ShellBasedIdMapping(config), config, false);
        Assert.assertTrue(wm.addOpenFileStream(h, ctx));
        // Test inactive open file context
        ctx.setActiveStatusForTest(false);
        Channel ch = Mockito.mock(Channel.class);
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_INACTIVE_CTX, ret);
        Assert.assertEquals(NFS3_OK, wm.commitBeforeRead(dfsClient, h, 0));
        ctx.getPendingWritesForTest().put(new OffsetRange(10, 15), new WriteCtx(null, 0, 0, 0, null, null, null, 0, false, null));
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_INACTIVE_WITH_PENDING_WRITE, ret);
        Assert.assertEquals(NFS3ERR_IO, wm.commitBeforeRead(dfsClient, h, 0));
        // Test request with non zero commit offset
        ctx.setActiveStatusForTest(true);
        Mockito.when(fos.getPos()).thenReturn(((long) (10)));
        ctx.setNextOffsetForTest(((long) (10)));
        COMMIT_STATUS status = ctx.checkCommitInternal(5, ch, 1, attr, false);
        Assert.assertEquals(COMMIT_DO_SYNC, status);
        // Do_SYNC state will be updated to FINISHED after data sync
        ret = ctx.checkCommit(dfsClient, 5, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_FINISHED, ret);
        Assert.assertEquals(NFS3_OK, wm.commitBeforeRead(dfsClient, h, 5));
        status = ctx.checkCommitInternal(10, ch, 1, attr, true);
        Assert.assertTrue((status == (COMMIT_STATUS.COMMIT_DO_SYNC)));
        ret = ctx.checkCommit(dfsClient, 10, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_FINISHED, ret);
        Assert.assertEquals(NFS3_OK, wm.commitBeforeRead(dfsClient, h, 10));
        ConcurrentNavigableMap<Long, CommitCtx> commits = ctx.getPendingCommitsForTest();
        Assert.assertTrue(((commits.size()) == 0));
        ret = ctx.checkCommit(dfsClient, 11, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_WAIT, ret);
        Assert.assertEquals(0, commits.size());// commit triggered by read doesn't wait

        Assert.assertEquals(NFS3ERR_JUKEBOX, wm.commitBeforeRead(dfsClient, h, 11));
        // Test request with zero commit offset
        // There is one pending write [5,10]
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_WAIT, ret);
        Assert.assertEquals(0, commits.size());
        Assert.assertEquals(NFS3ERR_JUKEBOX, wm.commitBeforeRead(dfsClient, h, 0));
        // Empty pending writes
        ctx.getPendingWritesForTest().remove(new OffsetRange(10, 15));
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_FINISHED, ret);
        Assert.assertEquals(NFS3_OK, wm.commitBeforeRead(dfsClient, h, 0));
    }

    // Validate all the commit check return codes OpenFileCtx.COMMIT_STATUS with large file upload option
    @Test
    public void testCheckCommitFromReadLargeFileUpload() throws IOException {
        DFSClient dfsClient = Mockito.mock(DFSClient.class);
        Nfs3FileAttributes attr = new Nfs3FileAttributes();
        HdfsDataOutputStream fos = Mockito.mock(HdfsDataOutputStream.class);
        Mockito.when(fos.getPos()).thenReturn(((long) (0)));
        NfsConfiguration config = new NfsConfiguration();
        config.setBoolean(LARGE_FILE_UPLOAD, true);
        OpenFileCtx ctx = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(config), false, config);
        FileHandle h = new FileHandle(1);// fake handle for "/dumpFilePath"

        COMMIT_STATUS ret;
        WriteManager wm = new WriteManager(new org.apache.hadoop.security.ShellBasedIdMapping(config), config, false);
        Assert.assertTrue(wm.addOpenFileStream(h, ctx));
        // Test inactive open file context
        ctx.setActiveStatusForTest(false);
        Channel ch = Mockito.mock(Channel.class);
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_INACTIVE_CTX, ret);
        Assert.assertEquals(NFS3_OK, wm.commitBeforeRead(dfsClient, h, 0));
        ctx.getPendingWritesForTest().put(new OffsetRange(10, 15), new WriteCtx(null, 0, 0, 0, null, null, null, 0, false, null));
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_INACTIVE_WITH_PENDING_WRITE, ret);
        Assert.assertEquals(NFS3ERR_IO, wm.commitBeforeRead(dfsClient, h, 0));
        // Test request with non zero commit offset
        ctx.setActiveStatusForTest(true);
        Mockito.when(fos.getPos()).thenReturn(((long) (6)));
        ctx.setNextOffsetForTest(((long) (10)));
        COMMIT_STATUS status = ctx.checkCommitInternal(5, ch, 1, attr, false);
        Assert.assertEquals(COMMIT_DO_SYNC, status);
        // Do_SYNC state will be updated to FINISHED after data sync
        ret = ctx.checkCommit(dfsClient, 5, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_FINISHED, ret);
        Assert.assertEquals(NFS3_OK, wm.commitBeforeRead(dfsClient, h, 5));
        // Test request with sequential writes
        status = ctx.checkCommitInternal(9, ch, 1, attr, true);
        Assert.assertTrue((status == (COMMIT_STATUS.COMMIT_SPECIAL_WAIT)));
        ret = ctx.checkCommit(dfsClient, 9, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_SPECIAL_WAIT, ret);
        Assert.assertEquals(NFS3ERR_JUKEBOX, wm.commitBeforeRead(dfsClient, h, 9));
        // Test request with non-sequential writes
        ConcurrentNavigableMap<Long, CommitCtx> commits = ctx.getPendingCommitsForTest();
        Assert.assertTrue(((commits.size()) == 0));
        ret = ctx.checkCommit(dfsClient, 16, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_SPECIAL_SUCCESS, ret);
        Assert.assertEquals(0, commits.size());// commit triggered by read doesn't wait

        Assert.assertEquals(NFS3_OK, wm.commitBeforeRead(dfsClient, h, 16));
        // Test request with zero commit offset
        // There is one pending write [10,15]
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_SPECIAL_WAIT, ret);
        Assert.assertEquals(0, commits.size());
        Assert.assertEquals(NFS3ERR_JUKEBOX, wm.commitBeforeRead(dfsClient, h, 0));
        // Empty pending writes
        ctx.getPendingWritesForTest().remove(new OffsetRange(10, 15));
        ret = ctx.checkCommit(dfsClient, 0, ch, 1, attr, true);
        Assert.assertEquals(COMMIT_SPECIAL_WAIT, ret);
        Assert.assertEquals(NFS3ERR_JUKEBOX, wm.commitBeforeRead(dfsClient, h, 0));
    }

    @Test
    public void testWriteStableHow() throws IOException, InterruptedException {
        NfsConfiguration config = new NfsConfiguration();
        DFSClient client = null;
        MiniDFSCluster cluster = null;
        RpcProgramNfs3 nfsd;
        SecurityHandler securityHandler = Mockito.mock(SecurityHandler.class);
        Mockito.when(securityHandler.getUser()).thenReturn(System.getProperty("user.name"));
        String currentUser = System.getProperty("user.name");
        config.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(currentUser), "*");
        config.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(currentUser), "*");
        ProxyUsers.refreshSuperUserGroupsConfiguration(config);
        try {
            cluster = numDataNodes(1).build();
            cluster.waitActive();
            client = new DFSClient(DFSUtilClient.getNNAddress(config), config);
            int namenodeId = Nfs3Utils.getNamenodeId(config);
            // Use emphral port in case tests are running in parallel
            config.setInt("nfs3.mountd.port", 0);
            config.setInt("nfs3.server.port", 0);
            // Start nfs
            Nfs3 nfs3 = new Nfs3(config);
            nfs3.startServiceInternal(false);
            nfsd = ((RpcProgramNfs3) (nfs3.getRpcProgram()));
            HdfsFileStatus status = client.getFileInfo("/");
            FileHandle rootHandle = new FileHandle(status.getFileId(), namenodeId);
            // Create file1
            CREATE3Request createReq = new CREATE3Request(rootHandle, "file1", Nfs3Constant.CREATE_UNCHECKED, new SetAttr3(), 0);
            XDR createXdr = new XDR();
            createReq.serialize(createXdr);
            CREATE3Response createRsp = nfsd.create(createXdr.asReadOnlyWrap(), securityHandler, new InetSocketAddress("localhost", 1234));
            FileHandle handle = createRsp.getObjHandle();
            // Test DATA_SYNC
            byte[] buffer = new byte[10];
            for (int i = 0; i < 10; i++) {
                buffer[i] = ((byte) (i));
            }
            WRITE3Request writeReq = new WRITE3Request(handle, 0, 10, WriteStableHow.DATA_SYNC, ByteBuffer.wrap(buffer));
            XDR writeXdr = new XDR();
            writeReq.serialize(writeXdr);
            nfsd.write(writeXdr.asReadOnlyWrap(), null, 1, securityHandler, new InetSocketAddress("localhost", 1234));
            waitWrite(nfsd, handle, 60000);
            // Readback
            READ3Request readReq = new READ3Request(handle, 0, 10);
            XDR readXdr = new XDR();
            readReq.serialize(readXdr);
            READ3Response readRsp = nfsd.read(readXdr.asReadOnlyWrap(), securityHandler, new InetSocketAddress("localhost", 1234));
            Assert.assertTrue(Arrays.equals(buffer, readRsp.getData().array()));
            // Test FILE_SYNC
            // Create file2
            CREATE3Request createReq2 = new CREATE3Request(rootHandle, "file2", Nfs3Constant.CREATE_UNCHECKED, new SetAttr3(), 0);
            XDR createXdr2 = new XDR();
            createReq2.serialize(createXdr2);
            CREATE3Response createRsp2 = nfsd.create(createXdr2.asReadOnlyWrap(), securityHandler, new InetSocketAddress("localhost", 1234));
            FileHandle handle2 = createRsp2.getObjHandle();
            WRITE3Request writeReq2 = new WRITE3Request(handle2, 0, 10, WriteStableHow.FILE_SYNC, ByteBuffer.wrap(buffer));
            XDR writeXdr2 = new XDR();
            writeReq2.serialize(writeXdr2);
            nfsd.write(writeXdr2.asReadOnlyWrap(), null, 1, securityHandler, new InetSocketAddress("localhost", 1234));
            waitWrite(nfsd, handle2, 60000);
            // Readback
            READ3Request readReq2 = new READ3Request(handle2, 0, 10);
            XDR readXdr2 = new XDR();
            readReq2.serialize(readXdr2);
            READ3Response readRsp2 = nfsd.read(readXdr2.asReadOnlyWrap(), securityHandler, new InetSocketAddress("localhost", 1234));
            Assert.assertTrue(Arrays.equals(buffer, readRsp2.getData().array()));
            // FILE_SYNC should sync the file size
            status = client.getFileInfo("/file2");
            Assert.assertTrue(((status.getLen()) == 10));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testOOOWrites() throws IOException, InterruptedException {
        NfsConfiguration config = new NfsConfiguration();
        MiniDFSCluster cluster = null;
        RpcProgramNfs3 nfsd;
        final int bufSize = 32;
        final int numOOO = 3;
        SecurityHandler securityHandler = Mockito.mock(SecurityHandler.class);
        Mockito.when(securityHandler.getUser()).thenReturn(System.getProperty("user.name"));
        String currentUser = System.getProperty("user.name");
        config.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(currentUser), "*");
        config.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(currentUser), "*");
        ProxyUsers.refreshSuperUserGroupsConfiguration(config);
        // Use emphral port in case tests are running in parallel
        config.setInt("nfs3.mountd.port", 0);
        config.setInt("nfs3.server.port", 0);
        try {
            cluster = numDataNodes(1).build();
            cluster.waitActive();
            Nfs3 nfs3 = new Nfs3(config);
            nfs3.startServiceInternal(false);
            nfsd = ((RpcProgramNfs3) (nfs3.getRpcProgram()));
            DFSClient dfsClient = new DFSClient(DFSUtilClient.getNNAddress(config), config);
            int namenodeId = Nfs3Utils.getNamenodeId(config);
            HdfsFileStatus status = dfsClient.getFileInfo("/");
            FileHandle rootHandle = new FileHandle(status.getFileId(), namenodeId);
            CREATE3Request createReq = new CREATE3Request(rootHandle, ("out-of-order-write" + (System.currentTimeMillis())), Nfs3Constant.CREATE_UNCHECKED, new SetAttr3(), 0);
            XDR createXdr = new XDR();
            createReq.serialize(createXdr);
            CREATE3Response createRsp = nfsd.create(createXdr.asReadOnlyWrap(), securityHandler, new InetSocketAddress("localhost", 1234));
            FileHandle handle = createRsp.getObjHandle();
            byte[][] oooBuf = new byte[numOOO][bufSize];
            for (int i = 0; i < numOOO; i++) {
                Arrays.fill(oooBuf[i], ((byte) (i)));
            }
            for (int i = 0; i < numOOO; i++) {
                final long offset = ((numOOO - 1) - i) * bufSize;
                WRITE3Request writeReq = new WRITE3Request(handle, offset, bufSize, WriteStableHow.UNSTABLE, ByteBuffer.wrap(oooBuf[i]));
                XDR writeXdr = new XDR();
                writeReq.serialize(writeXdr);
                nfsd.write(writeXdr.asReadOnlyWrap(), null, 1, securityHandler, new InetSocketAddress("localhost", 1234));
            }
            waitWrite(nfsd, handle, 60000);
            READ3Request readReq = new READ3Request(handle, bufSize, bufSize);
            XDR readXdr = new XDR();
            readReq.serialize(readXdr);
            READ3Response readRsp = nfsd.read(readXdr.asReadOnlyWrap(), securityHandler, new InetSocketAddress("localhost", config.getInt(DFS_NFS_SERVER_PORT_KEY, DFS_NFS_SERVER_PORT_DEFAULT)));
            Assert.assertTrue(Arrays.equals(oooBuf[1], readRsp.getData().array()));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testOverlappingWrites() throws IOException, InterruptedException {
        NfsConfiguration config = new NfsConfiguration();
        MiniDFSCluster cluster = null;
        RpcProgramNfs3 nfsd;
        final int bufSize = 32;
        SecurityHandler securityHandler = Mockito.mock(SecurityHandler.class);
        Mockito.when(securityHandler.getUser()).thenReturn(System.getProperty("user.name"));
        String currentUser = System.getProperty("user.name");
        config.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserGroupConfKey(currentUser), "*");
        config.set(DefaultImpersonationProvider.getTestProvider().getProxySuperuserIpConfKey(currentUser), "*");
        ProxyUsers.refreshSuperUserGroupsConfiguration(config);
        // Use emphral port in case tests are running in parallel
        config.setInt("nfs3.mountd.port", 0);
        config.setInt("nfs3.server.port", 0);
        try {
            cluster = numDataNodes(1).build();
            cluster.waitActive();
            Nfs3 nfs3 = new Nfs3(config);
            nfs3.startServiceInternal(false);
            nfsd = ((RpcProgramNfs3) (nfs3.getRpcProgram()));
            DFSClient dfsClient = new DFSClient(DFSUtilClient.getNNAddress(config), config);
            int namenodeId = Nfs3Utils.getNamenodeId(config);
            HdfsFileStatus status = dfsClient.getFileInfo("/");
            FileHandle rootHandle = new FileHandle(status.getFileId(), namenodeId);
            CREATE3Request createReq = new CREATE3Request(rootHandle, ("overlapping-writes" + (System.currentTimeMillis())), Nfs3Constant.CREATE_UNCHECKED, new SetAttr3(), 0);
            XDR createXdr = new XDR();
            createReq.serialize(createXdr);
            CREATE3Response createRsp = nfsd.create(createXdr.asReadOnlyWrap(), securityHandler, new InetSocketAddress("localhost", 1234));
            FileHandle handle = createRsp.getObjHandle();
            byte[] buffer = new byte[bufSize];
            for (int i = 0; i < bufSize; i++) {
                buffer[i] = ((byte) (i));
            }
            int[][] ranges = new int[][]{ new int[]{ 0, 10 }, new int[]{ 5, 7 }, new int[]{ 5, 5 }, new int[]{ 10, 6 }, new int[]{ 18, 6 }, new int[]{ 20, 6 }, new int[]{ 28, 4 }, new int[]{ 16, 2 }, new int[]{ 25, 4 } };
            for (int i = 0; i < (ranges.length); i++) {
                int[] x = ranges[i];
                byte[] tbuffer = new byte[x[1]];
                for (int j = 0; j < (x[1]); j++) {
                    tbuffer[j] = buffer[((x[0]) + j)];
                }
                WRITE3Request writeReq = new WRITE3Request(handle, ((long) (x[0])), x[1], WriteStableHow.UNSTABLE, ByteBuffer.wrap(tbuffer));
                XDR writeXdr = new XDR();
                writeReq.serialize(writeXdr);
                nfsd.write(writeXdr.asReadOnlyWrap(), null, 1, securityHandler, new InetSocketAddress("localhost", 1234));
            }
            waitWrite(nfsd, handle, 60000);
            READ3Request readReq = new READ3Request(handle, 0, bufSize);
            XDR readXdr = new XDR();
            readReq.serialize(readXdr);
            READ3Response readRsp = nfsd.read(readXdr.asReadOnlyWrap(), securityHandler, new InetSocketAddress("localhost", config.getInt(DFS_NFS_SERVER_PORT_KEY, DFS_NFS_SERVER_PORT_DEFAULT)));
            Assert.assertTrue(Arrays.equals(buffer, readRsp.getData().array()));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testCheckSequential() throws IOException {
        DFSClient dfsClient = Mockito.mock(DFSClient.class);
        Nfs3FileAttributes attr = new Nfs3FileAttributes();
        HdfsDataOutputStream fos = Mockito.mock(HdfsDataOutputStream.class);
        Mockito.when(fos.getPos()).thenReturn(((long) (0)));
        NfsConfiguration config = new NfsConfiguration();
        config.setBoolean(LARGE_FILE_UPLOAD, false);
        OpenFileCtx ctx = new OpenFileCtx(fos, attr, "/dumpFilePath", dfsClient, new org.apache.hadoop.security.ShellBasedIdMapping(config), false, config);
        ctx.getPendingWritesForTest().put(new OffsetRange(5, 10), new WriteCtx(null, 0, 0, 0, null, null, null, 0, false, null));
        ctx.getPendingWritesForTest().put(new OffsetRange(10, 15), new WriteCtx(null, 0, 0, 0, null, null, null, 0, false, null));
        ctx.getPendingWritesForTest().put(new OffsetRange(20, 25), new WriteCtx(null, 0, 0, 0, null, null, null, 0, false, null));
        Assert.assertTrue((!(ctx.checkSequential(5, 4))));
        Assert.assertTrue(ctx.checkSequential(9, 5));
        Assert.assertTrue(ctx.checkSequential(10, 5));
        Assert.assertTrue(ctx.checkSequential(14, 5));
        Assert.assertTrue((!(ctx.checkSequential(15, 5))));
        Assert.assertTrue((!(ctx.checkSequential(20, 5))));
        Assert.assertTrue((!(ctx.checkSequential(25, 5))));
        Assert.assertTrue((!(ctx.checkSequential(999, 5))));
    }
}

