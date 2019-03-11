/**
 * Copyright 2013 MovingBlocks
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
package org.terasology.utilities;


import org.junit.Assert;
import org.junit.Test;
import org.terasology.math.geom.Rect2i;


public class Rect2iTest {
    @Test
    public void testOverlap() {
        Assert.assertTrue(Rect2i.createFromMinAndSize(5, 5, 472, 17).overlaps(Rect2i.createFromMinAndSize(5, 5, 1, 16)));
    }

    @Test
    public void testEncompass() {
        // encompass self
        Assert.assertTrue(Rect2i.createFromMinAndSize(5, 5, 47, 57).contains(Rect2i.createFromMinAndSize(5, 5, 47, 57)));
        Assert.assertTrue(Rect2i.createFromMinAndSize(5, 5, 47, 57).contains(Rect2i.createFromMinAndSize(45, 35, 5, 20)));
        Assert.assertTrue(Rect2i.createFromMinAndSize(5, 5, 47, 57).contains(Rect2i.createFromMinAndSize(50, 60, 2, 2)));
        Assert.assertFalse(Rect2i.createFromMinAndSize(5, 5, 47, 57).contains(Rect2i.createFromMinAndSize(50, 60, 3, 2)));
        Assert.assertFalse(Rect2i.createFromMinAndSize(5, 5, 47, 57).contains(Rect2i.createFromMinAndSize(50, 60, 2, 3)));
    }

    @Test
    public void testContains() {
        Rect2i a = Rect2i.createFromMinAndMax(0, 0, 0, 0);
        Assert.assertTrue(a.contains(0, 0));
        Assert.assertFalse(a.contains(1, 0));
        Assert.assertFalse(a.contains(0, 1));
        Assert.assertFalse(a.contains(1, 1));
        Assert.assertFalse(a.contains((-1), 0));
        Assert.assertFalse(a.contains(0, (-1)));
        Assert.assertFalse(a.contains((-1), 1));
        Assert.assertFalse(a.contains(1, (-1)));
        Assert.assertFalse(a.contains((-1), (-1)));
    }
}

