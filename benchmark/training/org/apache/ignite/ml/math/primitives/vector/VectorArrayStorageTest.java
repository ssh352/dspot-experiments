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
package org.apache.ignite.ml.math.primitives.vector;


import java.util.Arrays;
import org.apache.ignite.ml.math.primitives.MathTestConstants;
import org.apache.ignite.ml.math.primitives.vector.storage.DenseVectorStorage;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link DenseVectorStorage}.
 */
public class VectorArrayStorageTest extends VectorBaseStorageTest<DenseVectorStorage> {
    /**
     *
     */
    @Test
    public void isArrayBased() throws Exception {
        Assert.assertTrue(MathTestConstants.WRONG_ATTRIBUTE_VAL, storage.isArrayBased());
        Assert.assertTrue(MathTestConstants.WRONG_ATTRIBUTE_VAL, new DenseVectorStorage().isArrayBased());
    }

    /**
     *
     */
    @Test
    public void data() throws Exception {
        Assert.assertNotNull(MathTestConstants.NULL_DATA_STORAGE, storage.data());
        Assert.assertEquals(MathTestConstants.WRONG_DATA_SIZE, storage.data().length, MathTestConstants.STORAGE_SIZE);
        Assert.assertTrue(MathTestConstants.UNEXPECTED_DATA_VAL, Arrays.equals(storage.data(), new double[MathTestConstants.STORAGE_SIZE]));
        Assert.assertNull(MathTestConstants.UNEXPECTED_DATA_VAL, new DenseVectorStorage().data());
    }
}

