/**
 * Copyright (C) 2013 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.compiler.llvm;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link DataLayout}.
 */
public class DataLayoutTest {
    @Test
    public void testGetAllocSize() {
        Assert.assertEquals(8, new DataLayout("i386-unknown-linux").getAllocSize(new StructureType(I32, I16, I8)));
        Assert.assertEquals(4, new DataLayout("i386-unknown-linux").getAllocSize(I8_PTR));
        Assert.assertEquals(8, new DataLayout("x86_64-unknown-linux").getAllocSize(I8_PTR));
    }

    @Test
    public void testGetStoreSize() {
        Assert.assertEquals(4, new DataLayout("i386-unknown-linux").getStoreSize(I8_PTR));
        Assert.assertEquals(8, new DataLayout("x86_64-unknown-linux").getStoreSize(I8_PTR));
        Assert.assertEquals(8, new DataLayout("i386-unknown-linux").getStoreSize(new StructureType(I32, I16, I8)));
    }

    @Test
    public void testGetAlignment() {
        Assert.assertEquals(4, new DataLayout("i386-unknown-linux").getAlignment(I8_PTR));
        Assert.assertEquals(8, new DataLayout("x86_64-unknown-linux").getAlignment(I8_PTR));
        Assert.assertEquals(4, new DataLayout("i386-unknown-linux").getAlignment(new StructureType(I32, I16, I8)));
        Assert.assertEquals(4, new DataLayout("i386-unknown-linux").getAlignment(new StructureType(I8, I32)));
        Assert.assertEquals(8, new DataLayout("x86_64-unknown-linux").getAlignment(new StructureType(I8, I64)));
        Assert.assertEquals(8, new DataLayout("x86_64-unknown-linux").getAlignment(DOUBLE));
        Assert.assertEquals(4, new DataLayout("i386-unknown-linux").getAlignment(I64));
        Assert.assertEquals(8, new DataLayout("x86_64-unknown-linux").getAlignment(I64));
    }
}

