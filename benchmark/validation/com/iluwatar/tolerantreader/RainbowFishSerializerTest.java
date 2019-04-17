/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.tolerantreader;


import java.io.File;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;


/**
 * Date: 12/30/15 - 18:39 PM
 *
 * @author Jeroen Meulemeester
 */
@EnableRuleMigrationSupport
public class RainbowFishSerializerTest {
    /**
     * Create a temporary folder, used to generate files in during this test
     */
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder();

    /**
     * Rainbow fish version 1 used during the tests
     */
    private static final RainbowFish V1 = new RainbowFish("version1", 1, 2, 3);

    /**
     * Rainbow fish version 2 used during the tests
     */
    private static final RainbowFishV2 V2 = new RainbowFishV2("version2", 4, 5, 6, true, false, true);

    /**
     * Verify if a fish, written as version 1 can be read back as version 1
     */
    @Test
    public void testWriteV1ReadV1() throws Exception {
        final File outputFile = this.testFolder.newFile();
        RainbowFishSerializer.writeV1(RainbowFishSerializerTest.V1, outputFile.getPath());
        final RainbowFish fish = RainbowFishSerializer.readV1(outputFile.getPath());
        Assertions.assertNotSame(RainbowFishSerializerTest.V1, fish);
        Assertions.assertEquals(RainbowFishSerializerTest.V1.getName(), fish.getName());
        Assertions.assertEquals(RainbowFishSerializerTest.V1.getAge(), fish.getAge());
        Assertions.assertEquals(RainbowFishSerializerTest.V1.getLengthMeters(), fish.getLengthMeters());
        Assertions.assertEquals(RainbowFishSerializerTest.V1.getWeightTons(), fish.getWeightTons());
    }

    /**
     * Verify if a fish, written as version 2 can be read back as version 1
     */
    @Test
    public void testWriteV2ReadV1() throws Exception {
        final File outputFile = this.testFolder.newFile();
        RainbowFishSerializer.writeV2(RainbowFishSerializerTest.V2, outputFile.getPath());
        final RainbowFish fish = RainbowFishSerializer.readV1(outputFile.getPath());
        Assertions.assertNotSame(RainbowFishSerializerTest.V2, fish);
        Assertions.assertEquals(RainbowFishSerializerTest.V2.getName(), fish.getName());
        Assertions.assertEquals(RainbowFishSerializerTest.V2.getAge(), fish.getAge());
        Assertions.assertEquals(RainbowFishSerializerTest.V2.getLengthMeters(), fish.getLengthMeters());
        Assertions.assertEquals(RainbowFishSerializerTest.V2.getWeightTons(), fish.getWeightTons());
    }
}
