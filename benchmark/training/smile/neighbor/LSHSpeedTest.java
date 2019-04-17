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
package smile.neighbor;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import smile.data.AttributeDataset;
import smile.data.NominalAttribute;
import smile.data.parser.DelimitedTextParser;
import smile.data.parser.IOUtils;
import smile.stat.distribution.MultivariateGaussianDistribution;


/**
 *
 *
 * @author Haifeng Li
 */
public class LSHSpeedTest {
    public LSHSpeedTest() {
    }

    /**
     * Test of nearest method, of class KDTree.
     */
    @Test
    public void testToy() {
        System.out.println("toy data");
        long start = System.currentTimeMillis();
        double[] mu1 = new double[]{ 1.0, 1.0, 1.0 };
        double[][] sigma1 = new double[][]{ new double[]{ 1.0, 0.0, 0.0 }, new double[]{ 0.0, 1.0, 0.0 }, new double[]{ 0.0, 0.0, 1.0 } };
        double[] mu2 = new double[]{ -2.0, -2.0, -2.0 };
        double[][] sigma2 = new double[][]{ new double[]{ 1.0, 0.3, 0.8 }, new double[]{ 0.3, 1.0, 0.5 }, new double[]{ 0.8, 0.5, 1.0 } };
        double[] mu3 = new double[]{ 4.0, 2.0, 3.0 };
        double[][] sigma3 = new double[][]{ new double[]{ 1.0, 0.8, 0.3 }, new double[]{ 0.8, 1.0, 0.5 }, new double[]{ 0.3, 0.5, 1.0 } };
        double[] mu4 = new double[]{ 3.0, 5.0, 1.0 };
        double[][] sigma4 = new double[][]{ new double[]{ 1.0, 0.5, 0.5 }, new double[]{ 0.5, 1.0, 0.5 }, new double[]{ 0.5, 0.5, 1.0 } };
        double[][] data = new double[10000][];
        MultivariateGaussianDistribution g1 = new MultivariateGaussianDistribution(mu1, sigma1);
        for (int i = 0; i < 2000; i++) {
            data[i] = g1.rand();
        }
        MultivariateGaussianDistribution g2 = new MultivariateGaussianDistribution(mu2, sigma2);
        for (int i = 0; i < 3000; i++) {
            data[(2000 + i)] = g2.rand();
        }
        MultivariateGaussianDistribution g3 = new MultivariateGaussianDistribution(mu3, sigma3);
        for (int i = 0; i < 3000; i++) {
            data[(5000 + i)] = g3.rand();
        }
        MultivariateGaussianDistribution g4 = new MultivariateGaussianDistribution(mu4, sigma4);
        for (int i = 0; i < 2000; i++) {
            data[(8000 + i)] = g4.rand();
        }
        double time = ((System.currentTimeMillis()) - start) / 1000.0;
        System.out.format("Generating toy data (four Gaussians): %.2fs%n", time);
        start = System.currentTimeMillis();
        LSH<double[]> lsh = new LSH(3, 5, 10, 4.0);
        for (double[] x : data) {
            lsh.put(x, x);
        }
        time = ((System.currentTimeMillis()) - start) / 1000.0;
        System.out.format("Building LSH: %.2fs%n", time);
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            lsh.nearest(data[randomInt(data.length)]);
        }
        time = ((System.currentTimeMillis()) - start) / 1000.0;
        System.out.format("NN: %.2fs%n", time);
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            lsh.knn(data[randomInt(data.length)], 10);
        }
        time = ((System.currentTimeMillis()) - start) / 1000.0;
        System.out.format("10-NN: %.2fs%n", time);
        start = System.currentTimeMillis();
        List<Neighbor<double[], double[]>> n = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            lsh.range(data[randomInt(data.length)], 1.0, n);
            n.clear();
        }
        time = ((System.currentTimeMillis()) - start) / 1000.0;
        System.out.format("Range: %.2fs%n", time);
    }

    /**
     * Test of nearest method, of class KDTree.
     */
    @Test
    public void testUSPS() {
        System.out.println("USPS");
        double[][] x = null;
        double[][] testx = null;
        long start = System.currentTimeMillis();
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setResponseIndex(new NominalAttribute("class"), 0);
        try {
            AttributeDataset train = parser.parse("USPS Train", IOUtils.getTestDataFile("usps/zip.train"));
            AttributeDataset test = parser.parse("USPS Test", IOUtils.getTestDataFile("usps/zip.test"));
            x = train.toArray(new double[train.size()][]);
            testx = test.toArray(new double[test.size()][]);
        } catch (Exception ex) {
            System.err.println(ex);
        }
        double time = ((System.currentTimeMillis()) - start) / 1000.0;
        System.out.format("Loading USPS: %.2fs%n", time);
        start = System.currentTimeMillis();
        LSH<double[]> lsh = new LSH(x, x);
        /**
         * LSH<double[]> lsh = new LSH<double[]>(256, 100, 3, 4.0);
         * for (double[] xi : x) {
         * lsh.put(xi, xi);
         * }
         */
        time = ((System.currentTimeMillis()) - start) / 1000.0;
        System.out.format("Building LSH: %.2fs%n", time);
        start = System.currentTimeMillis();
        for (int i = 0; i < (testx.length); i++) {
            lsh.nearest(testx[i]);
        }
        time = ((System.currentTimeMillis()) - start) / 1000.0;
        System.out.format("NN: %.2fs%n", time);
        start = System.currentTimeMillis();
        for (int i = 0; i < (testx.length); i++) {
            lsh.knn(testx[i], 10);
        }
        time = ((System.currentTimeMillis()) - start) / 1000.0;
        System.out.format("10-NN: %.2fs%n", time);
        start = System.currentTimeMillis();
        List<Neighbor<double[], double[]>> n = new ArrayList<>();
        for (int i = 0; i < (testx.length); i++) {
            lsh.range(testx[i], 8.0, n);
            n.clear();
        }
        time = ((System.currentTimeMillis()) - start) / 1000.0;
        System.out.format("Range: %.2fs%n", time);
    }
}
