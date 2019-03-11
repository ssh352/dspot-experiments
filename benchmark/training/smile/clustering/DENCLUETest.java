/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.clustering;


import org.junit.Assert;
import org.junit.Test;
import smile.stat.distribution.MultivariateGaussianDistribution;
import smile.validation.AdjustedRandIndex;
import smile.validation.RandIndex;


/**
 *
 *
 * @author Haifeng Li
 */
public class DENCLUETest {
    public DENCLUETest() {
    }

    /**
     * Test of learn method, of class DENCLUE.
     */
    @Test
    public void testToy() {
        System.out.println("Toy");
        double[] mu1 = new double[]{ 1.0, 1.0, 1.0 };
        double[][] sigma1 = new double[][]{ new double[]{ 1.0, 0.0, 0.0 }, new double[]{ 0.0, 1.0, 0.0 }, new double[]{ 0.0, 0.0, 1.0 } };
        double[] mu2 = new double[]{ -2.0, -2.0, -2.0 };
        double[][] sigma2 = new double[][]{ new double[]{ 1.0, 0.3, 0.8 }, new double[]{ 0.3, 1.0, 0.5 }, new double[]{ 0.8, 0.5, 1.0 } };
        double[] mu3 = new double[]{ 4.0, 2.0, 3.0 };
        double[][] sigma3 = new double[][]{ new double[]{ 1.0, 0.8, 0.3 }, new double[]{ 0.8, 1.0, 0.5 }, new double[]{ 0.3, 0.5, 1.0 } };
        double[] mu4 = new double[]{ 3.0, 5.0, 1.0 };
        double[][] sigma4 = new double[][]{ new double[]{ 1.0, 0.5, 0.5 }, new double[]{ 0.5, 1.0, 0.5 }, new double[]{ 0.5, 0.5, 1.0 } };
        double[][] data = new double[10000][];
        int[] label = new int[10000];
        MultivariateGaussianDistribution g1 = new MultivariateGaussianDistribution(mu1, sigma1);
        for (int i = 0; i < 2000; i++) {
            data[i] = g1.rand();
            label[i] = 0;
        }
        MultivariateGaussianDistribution g2 = new MultivariateGaussianDistribution(mu2, sigma2);
        for (int i = 0; i < 3000; i++) {
            data[(2000 + i)] = g2.rand();
            label[i] = 1;
        }
        MultivariateGaussianDistribution g3 = new MultivariateGaussianDistribution(mu3, sigma3);
        for (int i = 0; i < 3000; i++) {
            data[(5000 + i)] = g3.rand();
            label[i] = 2;
        }
        MultivariateGaussianDistribution g4 = new MultivariateGaussianDistribution(mu4, sigma4);
        for (int i = 0; i < 2000; i++) {
            data[(8000 + i)] = g4.rand();
            label[i] = 3;
        }
        DENCLUE denclue = new DENCLUE(data, 0.8, 50);
        AdjustedRandIndex ari = new AdjustedRandIndex();
        RandIndex rand = new RandIndex();
        double r = rand.measure(label, denclue.getClusterLabel());
        double r2 = ari.measure(label, denclue.getClusterLabel());
        System.out.println(("The number of clusters: " + (denclue.getNumClusters())));
        System.out.format("Training rand index = %.2f%%\tadjusted rand index = %.2f%%%n", (100.0 * r), (100.0 * r2));
        Assert.assertTrue((r > 0.54));
        Assert.assertTrue((r2 > 0.2));
    }
}

