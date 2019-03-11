/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.display;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.Test;


public class AngularObjectRegistryTest {
    @Test
    public void testBasic() throws TException {
        final AtomicInteger onAdd = new AtomicInteger(0);
        final AtomicInteger onUpdate = new AtomicInteger(0);
        final AtomicInteger onRemove = new AtomicInteger(0);
        AngularObjectRegistry registry = new AngularObjectRegistry("intpId", new AngularObjectRegistryListener() {
            @Override
            public void onAdd(String interpreterGroupId, AngularObject object) {
                onAdd.incrementAndGet();
            }

            @Override
            public void onUpdate(String interpreterGroupId, AngularObject object) {
                onUpdate.incrementAndGet();
            }

            @Override
            public void onRemove(String interpreterGroupId, String name, String noteId, String paragraphId) {
                onRemove.incrementAndGet();
            }
        });
        registry.add("name1", "value1", "note1", null);
        Assert.assertEquals(1, registry.getAll("note1", null).size());
        Assert.assertEquals(1, onAdd.get());
        Assert.assertEquals(0, onUpdate.get());
        registry.get("name1", "note1", null).set("newValue");
        Assert.assertEquals(1, onUpdate.get());
        registry.remove("name1", "note1", null);
        Assert.assertEquals(0, registry.getAll("note1", null).size());
        Assert.assertEquals(1, onRemove.get());
        Assert.assertEquals(null, registry.get("name1", "note1", null));
        // namespace
        registry.add("name1", "value11", "note2", null);
        Assert.assertEquals("value11", registry.get("name1", "note2", null).get());
        Assert.assertEquals(null, registry.get("name1", "note1", null));
        // null namespace
        registry.add("name1", "global1", null, null);
        Assert.assertEquals("global1", registry.get("name1", null, null).get());
    }

    @Test
    public void testGetDependOnScope() throws TException {
        AngularObjectRegistry registry = new AngularObjectRegistry("intpId", null);
        AngularObject ao1 = registry.add("name1", "o1", "noteId1", "paragraphId1");
        AngularObject ao2 = registry.add("name2", "o2", "noteId1", "paragraphId1");
        AngularObject ao3 = registry.add("name2", "o3", "noteId1", "paragraphId2");
        AngularObject ao4 = registry.add("name3", "o4", "noteId1", null);
        AngularObject ao5 = registry.add("name4", "o5", null, null);
        Assert.assertNull(registry.get("name3", "noteId1", "paragraphId1"));
        Assert.assertNull(registry.get("name1", "noteId2", null));
        Assert.assertEquals("o1", registry.get("name1", "noteId1", "paragraphId1").get());
        Assert.assertEquals("o2", registry.get("name2", "noteId1", "paragraphId1").get());
        Assert.assertEquals("o3", registry.get("name2", "noteId1", "paragraphId2").get());
        Assert.assertEquals("o4", registry.get("name3", "noteId1", null).get());
        Assert.assertEquals("o5", registry.get("name4", null, null).get());
    }

    @Test
    public void testGetAllDependOnScope() throws TException {
        AngularObjectRegistry registry = new AngularObjectRegistry("intpId", null);
        AngularObject ao1 = registry.add("name1", "o", "noteId1", "paragraphId1");
        AngularObject ao2 = registry.add("name2", "o", "noteId1", "paragraphId1");
        AngularObject ao3 = registry.add("name2", "o", "noteId1", "paragraphId2");
        AngularObject ao4 = registry.add("name3", "o", "noteId1", null);
        AngularObject ao5 = registry.add("name4", "o", null, null);
        Assert.assertEquals(2, registry.getAll("noteId1", "paragraphId1").size());
        Assert.assertEquals(1, registry.getAll("noteId1", "paragraphId2").size());
        Assert.assertEquals(1, registry.getAll("noteId1", null).size());
        Assert.assertEquals(1, registry.getAll(null, null).size());
        Assert.assertEquals(5, registry.getAllWithGlobal("noteId1").size());
    }
}

