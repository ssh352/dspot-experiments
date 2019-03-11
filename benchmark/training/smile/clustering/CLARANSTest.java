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
import smile.data.AttributeDataset;
import smile.data.NominalAttribute;
import smile.data.parser.DelimitedTextParser;
import smile.data.parser.IOUtils;
import smile.math.distance.EuclideanDistance;
import smile.validation.AdjustedRandIndex;
import smile.validation.RandIndex;


/**
 *
 *
 * @author Haifeng Li
 */
public class CLARANSTest {
    public CLARANSTest() {
    }

    /**
     * Test of learn method, of class CLARANS.
     */
    @Test
    public void testUSPS() {
        System.out.println("USPS");
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setResponseIndex(new NominalAttribute("class"), 0);
        try {
            AttributeDataset train = parser.parse("USPS Train", IOUtils.getTestDataFile("usps/zip.train"));
            AttributeDataset test = parser.parse("USPS Test", IOUtils.getTestDataFile("usps/zip.test"));
            double[][] x = train.toArray(new double[train.size()][]);
            int[] y = train.toArray(new int[train.size()]);
            double[][] testx = test.toArray(new double[test.size()][]);
            int[] testy = test.toArray(new int[test.size()]);
            AdjustedRandIndex ari = new AdjustedRandIndex();
            RandIndex rand = new RandIndex();
            CLARANS<double[]> clarans = new CLARANS(x, new EuclideanDistance(), 10, 50, 8);
            double r = rand.measure(y, clarans.getClusterLabel());
            double r2 = ari.measure(y, clarans.getClusterLabel());
            System.out.format("Training rand index = %.2f%%\tadjusted rand index = %.2f%%%n", (100.0 * r), (100.0 * r2));
            Assert.assertTrue((r > 0.8));
            Assert.assertTrue((r2 > 0.28));
            int[] p = new int[testx.length];
            for (int i = 0; i < (testx.length); i++) {
                p[i] = clarans.predict(testx[i]);
            }
            r = rand.measure(testy, p);
            r2 = ari.measure(testy, p);
            System.out.format("Testing rand index = %.2f%%\tadjusted rand index = %.2f%%%n", (100.0 * r), (100.0 * r2));
            Assert.assertTrue((r > 0.8));
            Assert.assertTrue((r2 > 0.25));
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

