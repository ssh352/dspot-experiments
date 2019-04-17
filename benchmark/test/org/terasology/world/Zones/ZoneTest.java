/**
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.world.Zones;


import org.junit.Assert;
import org.junit.Test;
import org.terasology.world.zones.Zone;


public class ZoneTest {
    private Zone zone;

    @Test
    public void testGetChildZones() {
        Assert.assertTrue(zone.getChildZones().isEmpty());
        Zone child = new Zone("Child", () -> false);
        zone.addZone(child);
        Assert.assertFalse(zone.getChildZones().isEmpty());
        Assert.assertTrue(zone.getChildZones().contains(child));
        try {
            zone.getChildZone("Invalid name");
            Assert.fail();
        } catch (Exception e) {
        }
        Assert.assertEquals(child, zone.getChildZone("Child"));
    }
}
