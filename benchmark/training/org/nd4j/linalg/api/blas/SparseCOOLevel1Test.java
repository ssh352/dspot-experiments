/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.linalg.api.blas;


import DataType.FLOAT;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.BaseSparseNDArray;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author Audrey Loeffel
 */
// temporary ignored
@Ignore
@RunWith(Parameterized.class)
public class SparseCOOLevel1Test extends BaseNd4jTest {
    // vector = [1, 2, 0, 4]
    private double[] data = new double[]{ 1, 2, 4 };

    private int[][] indexes = new int[][]{ new int[]{ 0, 0 }, new int[]{ 0, 1 }, new int[]{ 0, 3 } };

    private long[] shape = new long[]{ 1, 4 };

    public SparseCOOLevel1Test(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void shouldComputeDot() {
        INDArray sparseVec = Nd4j.createSparseCOO(data, indexes, shape);
        // INDArray vec = Nd4j.create( new double[] {1 ,2, 3, 4});
        INDArray matrix = Nd4j.linspace(1, 4, 4, FLOAT).reshape(1, 4);
        INDArray vec = matrix.getRow(0);
        Assert.assertEquals(21, Nd4j.getBlasWrapper().dot(sparseVec, vec), 0.1);
    }

    @Test
    public void shouldComputeNrm2() {
        INDArray sparseVec = Nd4j.createSparseCOO(data, indexes, shape);
        Assert.assertEquals(Math.sqrt(21), Nd4j.getBlasWrapper().nrm2(sparseVec), 0.1);
    }

    @Test
    public void shouldComputeAsum() {
        INDArray sparseVec = Nd4j.createSparseCOO(data, indexes, shape);
        Assert.assertEquals(7, Nd4j.getBlasWrapper().asum(sparseVec), 0.1);
    }

    @Test
    public void shouldComputeIamax() {
        INDArray sparseVec = Nd4j.createSparseCOO(data, indexes, shape);
        Assert.assertEquals(2, Nd4j.getBlasWrapper().iamax(sparseVec), 0.1);
    }

    @Test
    public void shouldComputeIamin() {
        INDArray sparseVec = Nd4j.createSparseCOO(data, indexes, shape);
        Assert.assertEquals(0, Nd4j.getBlasWrapper().level1().iamin(sparseVec), 0.1);
    }

    @Test
    public void shouldComputeAxpy() {
        INDArray sparseVec = Nd4j.createSparseCOO(data, indexes, shape);
        INDArray vec = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        INDArray expected = Nd4j.create(new double[]{ 2, 4, 3, 8 });
        Nd4j.getBlasWrapper().level1().axpy(vec.length(), 1, sparseVec, vec);
        Assert.assertEquals(getFailureMessage(), expected, vec);
    }

    @Test
    public void shouldComputeRot() {
        // try with dense vectors to get the expected result
        INDArray temp1 = Nd4j.create(new double[]{ 1, 2, 0, 4 });
        INDArray temp2 = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        System.out.println(((("before: " + (temp1.data())) + " ") + (temp2.data())));
        Nd4j.getBlasWrapper().level1().rot(temp1.length(), temp1, temp2, 1, 2);
        System.out.println(((("after: " + (temp1.data())) + " ") + (temp2.data())));
        // before: [1.0,2.0,0.0,4.0]  [1.0,2.0,3.0,4.0]
        // after: [3.0,6.0,6.0,12.0] [-1.0,-2.0,3.0,-4.0]
        INDArray sparseVec = Nd4j.createSparseCOO(data, indexes, shape);
        INDArray vec = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        Nd4j.getBlasWrapper().level1().rot(vec.length(), sparseVec, vec, 1, 2);
        System.out.println((((sparseVec.data()) + " ") + (vec.data())));
        // System.out.println("indexes: " + ((BaseSparseNDArray) sparseVec).getVectorCoordinates().toString());
        INDArray expectedSparseVec = Nd4j.createSparseCSR(new double[]{ 3, 6, 6, 12 }, new int[]{ 0, 1, 2, 3 }, new int[]{ 0 }, new int[]{ 4 }, new long[]{ 1, 4 });
        INDArray expectedVec = Nd4j.create(new double[]{ -1, -2, 3, -4 });
        Assert.assertEquals(getFailureMessage(), expectedSparseVec.data(), sparseVec.data());
        Assert.assertEquals(getFailureMessage(), expectedVec, vec);
        // TODO FIXME
    }

    @Test
    public void shouldComputeRotWithFullVector() {
        // try with dense vectors to get the expected result
        /* INDArray temp1 = Nd4j.create( new double[] {1 ,2, 3, 4});
        INDArray temp2 = Nd4j.create( new double[] {1 ,2, 3, 4});
        System.out.println("before: " + temp1.data() + " " + temp2.data());
        Nd4j.getBlasWrapper().level1().rot(temp1.length(), temp1, temp2, 1, 2);
        System.out.println("after: " + temp1.data() + " " + temp2.data());
         */
        // before: [1.0,2.0,3.0,4.0]  [1.0,2.0,3.0,4.0]
        // after: [3.0,6.0,0.0,12.0] [-1.0,-2.0,-3.0,-4.0]
        int[] cols = new int[]{ 0, 1, 2, 3 };
        double[] values = new double[]{ 1, 2, 3, 4 };
        INDArray sparseVec = Nd4j.createSparseCOO(data, indexes, shape);
        INDArray vec = Nd4j.create(new double[]{ 1, 2, 3, 4 });
        Nd4j.getBlasWrapper().level1().rot(vec.length(), sparseVec, vec, 1, 2);
        INDArray expectedSparseVec = Nd4j.createSparseCSR(new double[]{ 3, 6, 9, 12 }, new int[]{ 0, 1, 2, 3 }, new int[]{ 0 }, new int[]{ 4 }, new long[]{ 1, 4 });
        INDArray expectedVec = Nd4j.create(new double[]{ -1, -2, -3, -4 });
        Assert.assertEquals(getFailureMessage(), expectedSparseVec.data(), sparseVec.data());
        Assert.assertEquals(getFailureMessage(), expectedVec, vec);
        if ((expectedSparseVec.isSparse()) && (sparseVec.isSparse())) {
            BaseSparseNDArray vec2 = ((BaseSparseNDArray) (expectedSparseVec));
            BaseSparseNDArray vecSparse2 = ((BaseSparseNDArray) (sparseVec));
            Assert.assertEquals(getFailureMessage(), vec2.getVectorCoordinates(), vecSparse2);
        }
    }
}

