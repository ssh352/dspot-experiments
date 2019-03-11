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
package org.apache.ignite.ml.inference.storage.model;


import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Base tests for all implementation of {@link ModelStorage}.
 */
public abstract class AbstractModelStorageTest {
    /**
     *
     */
    @Test
    public void testPutGetRemoveFile() {
        ModelStorage mdlStorage = getModelStorage();
        byte[] data = new byte[]{ 1, 2, 3, 4, 5 };
        mdlStorage.mkdirs("/");
        mdlStorage.putFile("/test", data);
        Assert.assertTrue(mdlStorage.exists("/test"));
        Assert.assertArrayEquals(data, mdlStorage.getFile("/test"));
        mdlStorage.remove("/test");
        Assert.assertFalse(mdlStorage.exists("/test"));
    }

    /**
     *
     */
    @Test
    public void testListDirectory() {
        ModelStorage mdlStorage = getModelStorage();
        mdlStorage.mkdirs("/a/b");
        mdlStorage.mkdirs("/a/c");
        mdlStorage.putFile("/a/test", new byte[0]);
        Set<String> aFiles = mdlStorage.listFiles("/a");
        Set<String> bFiles = mdlStorage.listFiles("/a/b");
        Set<String> cFiles = mdlStorage.listFiles("/a/c");
        Assert.assertEquals(3, aFiles.size());
        Assert.assertTrue(bFiles.isEmpty());
        Assert.assertTrue(cFiles.isEmpty());
        Assert.assertTrue(aFiles.contains("/a/b"));
        Assert.assertTrue(aFiles.contains("/a/c"));
        Assert.assertTrue(aFiles.contains("/a/test"));
    }

    /**
     *
     */
    @Test
    public void testIsDirectory() {
        ModelStorage mdlStorage = getModelStorage();
        mdlStorage.mkdirs("/a");
        Assert.assertTrue(mdlStorage.exists("/a"));
        Assert.assertTrue(mdlStorage.isDirectory("/a"));
        Assert.assertFalse(mdlStorage.isFile("/a"));
    }

    /**
     *
     */
    @Test
    public void testIsFile() {
        ModelStorage mdlStorage = getModelStorage();
        mdlStorage.mkdirs("/");
        mdlStorage.putFile("/test", new byte[0]);
        Assert.assertTrue(mdlStorage.exists("/test"));
        Assert.assertTrue(mdlStorage.isFile("/test"));
        Assert.assertFalse(mdlStorage.isDirectory("/test"));
    }

    /**
     *
     */
    @Test
    public void testRemoveDirectory() {
        ModelStorage mdlStorage = getModelStorage();
        mdlStorage.mkdirs("/a/b/c");
        mdlStorage.mkdirs("/a/b/d");
        mdlStorage.mkdirs("/a/c");
        mdlStorage.putFile("/a/b/c/test", new byte[0]);
        mdlStorage.putFile("/a/b/test", new byte[0]);
        mdlStorage.remove("/a/b");
        Assert.assertFalse(mdlStorage.exists("/a/b"));
        Assert.assertFalse(mdlStorage.exists("/a/b/c"));
        Assert.assertFalse(mdlStorage.exists("/a/b/d"));
        Assert.assertFalse(mdlStorage.exists("/a/b/test"));
        Assert.assertFalse(mdlStorage.exists("/a/b/c/test"));
        Assert.assertTrue(mdlStorage.exists("/a"));
        Assert.assertTrue(mdlStorage.exists("/a/c"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPutFileIntoNonExistingDirectory() {
        ModelStorage mdlStorage = getModelStorage();
        mdlStorage.putFile("/test", new byte[0]);
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMakeDirInNonExistingDirectory() {
        ModelStorage mdlStorage = getModelStorage();
        mdlStorage.mkdir("/test");
    }
}

