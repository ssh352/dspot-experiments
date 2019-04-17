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
package smile.manifold;


import org.junit.Assert;
import org.junit.Test;
import smile.data.AttributeDataset;
import smile.data.parser.DelimitedTextParser;
import smile.data.parser.IOUtils;


/**
 *
 *
 * @author Haifeng Li
 */
public class LLETest {
    AttributeDataset swissroll;

    public LLETest() {
        DelimitedTextParser parser = new DelimitedTextParser();
        parser.setDelimiter("\t");
        try {
            swissroll = parser.parse("Swissroll", IOUtils.getTestDataFile("manifold/swissroll.txt"));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Test of learn method, of class LLE.
     */
    @Test
    public void testLearn() {
        System.out.println("learn");
        double[][] points = new double[][]{ new double[]{ 0.0099, -0.0189 }, new double[]{ -0.0169, -0.0219 }, new double[]{ 0.0498, 0.0159 }, new double[]{ 0.0297, -0.0144 }, new double[]{ -0.0106, -0.0222 }, new double[]{ 0.0043, -0.0374 }, new double[]{ -0.0155, -0.0284 }, new double[]{ 0.0354, 0.0177 }, new double[]{ 0.008, 0.0151 }, new double[]{ 0.051, 0.0401 }, new double[]{ 0.0368, -0.029 }, new double[]{ -0.0334, 0.0123 }, new double[]{ 0.028, -0.0459 }, new double[]{ -0.0358, -0.0052 }, new double[]{ -0.0198, -0.0402 }, new double[]{ -0.0196, 0.0012 }, new double[]{ -0.022, -0.0257 }, new double[]{ 0.0342, 0.0045 }, new double[]{ -0.0531, 0.0324 }, new double[]{ -0.0362, -0.0207 }, new double[]{ -0.0079, 0.0054 }, new double[]{ -0.0357, -0.0249 }, new double[]{ -0.0366, -0.0362 }, new double[]{ -0.0259, 0.0097 }, new double[]{ 0.0178, -0.0183 }, new double[]{ 0.0326, -0.0414 }, new double[]{ -0.0327, 0.0449 }, new double[]{ -0.0159, -0.0335 }, new double[]{ -0.0187, -0.0106 }, new double[]{ 0.0259, -0.0474 }, new double[]{ 0.0236, -0.0329 }, new double[]{ 0.0288, -0.0069 }, new double[]{ -0.0182, -0.0214 }, new double[]{ 0.0303, -0.0162 }, new double[]{ -0.0487, 0.0066 }, new double[]{ 0.0543, 0.0439 }, new double[]{ 0.0351, 0.0305 }, new double[]{ 0.0214, 0.0222 }, new double[]{ 0.0165, -0.0348 }, new double[]{ -0.042, 0.0551 }, new double[]{ -0.0099, 0.0526 }, new double[]{ -0.0082, 0.0135 }, new double[]{ -0.0328, -0.0058 }, new double[]{ 0.0136, 0.0046 }, new double[]{ 0.046, 0.0397 }, new double[]{ -0.0108, 0.0295 }, new double[]{ -1.0E-4, 0.0146 }, new double[]{ -0.037, -0.0325 }, new double[]{ 0.0198, -0.0119 }, new double[]{ -0.0377, -0.0152 }, new double[]{ -0.0198, -0.0068 }, new double[]{ -0.0126, -0.0037 }, new double[]{ 0.0157, 0.0352 }, new double[]{ -0.0332, -0.0019 }, new double[]{ 0.0101, -0.0027 }, new double[]{ -0.0236, 0.0194 }, new double[]{ 0.0179, -0.043 }, new double[]{ 0.0337, -0.0477 }, new double[]{ 0.0135, -0.0293 }, new double[]{ -0.0315, 0.0034 }, new double[]{ -0.0194, 0.0481 }, new double[]{ -0.0379, -0.0225 }, new double[]{ -0.0425, -0.0184 }, new double[]{ -0.0277, 0.0087 }, new double[]{ -0.0157, -0.0153 }, new double[]{ 0.0331, 0.042 }, new double[]{ -0.0382, -0.0297 }, new double[]{ 0.0516, 0.0471 }, new double[]{ 0.0278, 0.0415 }, new double[]{ 0.01, -0.0269 }, new double[]{ -0.0092, -0.0315 }, new double[]{ -0.0012, -0.0348 }, new double[]{ -0.012, -0.0178 }, new double[]{ 0.0373, -0.0275 }, new double[]{ 0.0028, -0.0357 }, new double[]{ 0.0429, -0.0028 }, new double[]{ 0.0383, -0.0084 }, new double[]{ -0.0336, -0.0013 }, new double[]{ 0.0457, 0.0347 }, new double[]{ -0.0487, 0.0388 }, new double[]{ 0.0142, -0.0467 }, new double[]{ 0.0303, 0.0131 }, new double[]{ 0.02, -0.063 }, new double[]{ 0.0153, 0.0345 }, new double[]{ -0.0093, 0.0136 }, new double[]{ 0.0278, 0.0125 }, new double[]{ -0.0248, 0.0038 }, new double[]{ -0.0206, 0.0502 }, new double[]{ 0.0401, 0.0156 }, new double[]{ 0.0286, -0.0118 }, new double[]{ -0.0512, 0.0303 }, new double[]{ -0.0118, 0.004 }, new double[]{ 0.0288, -0.0173 }, new double[]{ 0.0069, -0.0076 }, new double[]{ 0.0447, 0.0077 }, new double[]{ 0.0281, -0.0604 }, new double[]{ -0.0391, -0.0027 }, new double[]{ 0.0227, 0.0239 }, new double[]{ -0.0371, -0.0361 }, new double[]{ 0.0167, -0.0293 }, new double[]{ 0.0481, 0.0146 }, new double[]{ -0.0177, 0.0328 }, new double[]{ -0.0384, -0.033 }, new double[]{ -0.0149, -0.0379 }, new double[]{ -0.031, -0.0248 }, new double[]{ -0.0194, -0.0354 }, new double[]{ -0.0147, -0.0264 }, new double[]{ 0.0215, 0.0291 }, new double[]{ 0.0483, 0.0364 }, new double[]{ -0.0212, -0.0135 }, new double[]{ -0.0298, 0.0027 }, new double[]{ 0.0481, 0.0018 }, new double[]{ -0.0149, 0.0211 }, new double[]{ 0.0307, 0.0072 }, new double[]{ 0.008, -0.0191 }, new double[]{ -0.0229, 0.0317 }, new double[]{ 0.0018, -0.0455 }, new double[]{ -0.0306, 0.0312 }, new double[]{ 0.0242, 0.0104 }, new double[]{ 0.0511, 0.0344 }, new double[]{ 0.0371, 0.0325 }, new double[]{ 0.0547, 0.0571 }, new double[]{ 0.0239, -0.0321 }, new double[]{ 0.0193, -0.0577 }, new double[]{ -0.0151, 0.0195 }, new double[]{ -0.0084, -0.0219 }, new double[]{ 0.0171, 0.0141 }, new double[]{ 0.0117, -0.0295 }, new double[]{ 0.0366, -0.0204 }, new double[]{ -0.0455, 0.0278 }, new double[]{ 0.0138, -0.0569 }, new double[]{ 0.039, -0.0566 }, new double[]{ 1.0E-4, 0.0342 }, new double[]{ 0.0119, -0.0591 }, new double[]{ 0.0124, -0.001 }, new double[]{ 0.0259, -0.0298 }, new double[]{ 0.0559, 0.056 }, new double[]{ 0.0541, 0.0349 }, new double[]{ -0.0305, 0.0279 }, new double[]{ 0.0578, 0.0487 }, new double[]{ -0.01, 0.0208 }, new double[]{ -0.032, -0.0253 }, new double[]{ -0.0473, 0.0293 }, new double[]{ 1.0E-4, -0.0477 }, new double[]{ 0.0146, 0.046 }, new double[]{ -0.0384, -0.0366 }, new double[]{ -0.0078, -0.0183 }, new double[]{ -0.035, 0.0412 }, new double[]{ 0.0073, 0.0376 }, new double[]{ 0.0424, 0.0038 }, new double[]{ -0.0439, -3.0E-4 }, new double[]{ 0.0164, 0.0266 }, new double[]{ 0.0498, 0.0062 }, new double[]{ 0.0079, -0.0331 }, new double[]{ 0.0236, 0.0134 }, new double[]{ -0.0268, -0.0193 }, new double[]{ 0.0337, -0.0044 }, new double[]{ 0.0205, -0.0537 }, new double[]{ 0.0519, 0.0397 }, new double[]{ -0.002, -0.0208 }, new double[]{ -0.039, 0.0105 }, new double[]{ 0.0553, 0.0556 }, new double[]{ -0.0402, -0.0101 }, new double[]{ 0.0163, -0.0385 }, new double[]{ 0.0175, -0.0584 }, new double[]{ -0.0146, -0.025 }, new double[]{ 0.0155, -0.0207 }, new double[]{ -0.0463, 0.0228 }, new double[]{ -0.0275, -0.0032 }, new double[]{ 0.0327, -0.0602 }, new double[]{ -0.0465, 0.0528 }, new double[]{ -0.0083, -0.0288 }, new double[]{ -0.02, -0.0338 }, new double[]{ -0.0437, 0.0317 }, new double[]{ 0.0392, -0.0031 }, new double[]{ 0.0403, 0.0076 }, new double[]{ 0.0015, -0.0306 }, new double[]{ 6.0E-4, 0.0417 }, new double[]{ -0.0292, -0.0135 }, new double[]{ 2.0E-4, -0.0359 }, new double[]{ -0.011, -0.0175 }, new double[]{ -0.0366, 0.0191 }, new double[]{ -0.008, 0.0197 }, new double[]{ 0.0141, -0.0455 }, new double[]{ 0.0157, 0.0057 }, new double[]{ 0.0309, -0.0265 }, new double[]{ -0.0275, 0.0378 }, new double[]{ 0.0526, 0.0198 }, new double[]{ -0.0472, 0.0212 }, new double[]{ -0.0443, 0.0582 }, new double[]{ 0.0394, -0.0261 }, new double[]{ 9.0E-4, -2.0E-4 }, new double[]{ 0.0234, -0.0261 }, new double[]{ -0.0411, 0.0529 }, new double[]{ 0.0308, -0.0509 }, new double[]{ 0.0416, 0.0192 }, new double[]{ 0.0388, -0.0329 }, new double[]{ 0.0057, -0.0308 }, new double[]{ 3.0E-4, -0.0274 }, new double[]{ 0.0173, -0.0555 }, new double[]{ 0.0197, -0.0498 }, new double[]{ -0.0304, -0.0402 }, new double[]{ 0.0084, -0.0088 }, new double[]{ -0.042, -0.0428 }, new double[]{ 0.0585, 0.0509 }, new double[]{ 0.0506, 0.0388 }, new double[]{ -0.0038, 0.003 }, new double[]{ -0.0294, 0.034 }, new double[]{ -0.0031, -0.0348 }, new double[]{ -0.0306, 0.0429 }, new double[]{ -0.0244, 0.0072 }, new double[]{ -0.0458, -0.0164 }, new double[]{ -0.0337, 0.0139 }, new double[]{ -0.0448, -0.0151 }, new double[]{ 0.0421, 0.033 }, new double[]{ -0.0513, 0.0274 }, new double[]{ -0.0362, 0.0288 }, new double[]{ -0.0236, -0.0106 }, new double[]{ -0.051, 0.0433 }, new double[]{ 0.0458, 0.0229 }, new double[]{ -0.0062, 0.0448 }, new double[]{ 0.0434, -0.0144 }, new double[]{ 0.0031, 0.0164 }, new double[]{ 0.0468, -0.0046 }, new double[]{ 6.0E-4, 0.0426 }, new double[]{ -0.0078, 0.0212 }, new double[]{ 0.0511, 0.0204 }, new double[]{ -0.0368, -0.0223 }, new double[]{ -0.0262, -0.0337 }, new double[]{ 0.0227, 0.0333 }, new double[]{ 0.0079, 0.0352 }, new double[]{ -0.0068, -0.0397 }, new double[]{ 0.0167, 0.0276 }, new double[]{ 0.0472, 0.0053 }, new double[]{ 0.0346, -0.0575 }, new double[]{ 0.0398, -0.0019 }, new double[]{ 0.0252, 0.0453 }, new double[]{ 0.0403, 0.0499 }, new double[]{ -0.0388, 0.0208 }, new double[]{ 0.0285, -0.0366 }, new double[]{ 0.0133, 0.0018 }, new double[]{ 0.0504, 0.0151 }, new double[]{ 0.0297, -0.0242 }, new double[]{ 0.0135, -0.0041 }, new double[]{ -0.0229, -0.0203 }, new double[]{ 0.0446, 0.0091 }, new double[]{ -0.031, -0.017 }, new double[]{ -0.0206, -0.008 }, new double[]{ 0.0347, -0.0434 }, new double[]{ -0.0328, -0.0349 }, new double[]{ 0.0368, -0.0419 }, new double[]{ 0.0018, -0.0321 }, new double[]{ -0.0073, -1.0E-4 }, new double[]{ -0.0507, 0.023 }, new double[]{ -0.0345, -0.0355 }, new double[]{ 0.0299, 0.0371 }, new double[]{ -0.025, 0.0385 }, new double[]{ -0.038, -0.0103 }, new double[]{ -0.0017, -0.0319 }, new double[]{ -0.0385, -0.0257 }, new double[]{ 0.0374, -0.0027 }, new double[]{ 0.0506, 0.0179 }, new double[]{ -0.0325, 0.0561 }, new double[]{ -0.0377, -0.0195 }, new double[]{ 0.0406, 0.0352 }, new double[]{ 0.0123, -0.0113 }, new double[]{ -0.0456, 0.0263 }, new double[]{ 0.0344, -0.0624 }, new double[]{ -0.0381, 0.0448 }, new double[]{ -0.0276, -0.0164 }, new double[]{ -4.0E-4, -0.0435 }, new double[]{ 0.0475, 0.0281 }, new double[]{ -0.0431, -0.0347 }, new double[]{ 0.0585, 0.0505 }, new double[]{ 0.0416, 0.0373 }, new double[]{ 0.0549, 0.0514 }, new double[]{ 0.0041, -0.0057 }, new double[]{ -0.0325, 0.0311 }, new double[]{ -0.0273, 0.023 }, new double[]{ -0.0459, 0.0129 }, new double[]{ 0.0091, -0.0089 }, new double[]{ 0.0403, -0.0368 }, new double[]{ -0.032, 0.0059 }, new double[]{ 0.0245, -0.0165 }, new double[]{ -0.0199, 3.0E-4 }, new double[]{ -0.04, -0.0275 }, new double[]{ 0.0206, -0.0151 }, new double[]{ 0.03, -0.0677 }, new double[]{ 0.0201, -0.0178 }, new double[]{ 0.0109, -0.0581 }, new double[]{ 0.0173, 0.0109 }, new double[]{ -0.0152, -0.0141 }, new double[]{ -0.0402, 0.0189 }, new double[]{ -5.0E-4, 0.0344 }, new double[]{ -0.015, -3.0E-4 }, new double[]{ 0.0105, -0.0414 }, new double[]{ -0.0095, -0.0033 }, new double[]{ -0.0245, -0.0213 }, new double[]{ 0.0117, -0.0588 }, new double[]{ 0.0258, -0.0383 }, new double[]{ -0.0167, -0.0384 }, new double[]{ 0.0308, 0.0379 }, new double[]{ 0.0081, 0.0066 }, new double[]{ -0.0179, -0.0403 }, new double[]{ -0.0045, -0.0477 }, new double[]{ -0.0362, -0.0197 }, new double[]{ -0.0434, 0.0316 }, new double[]{ -0.0339, -0.0353 }, new double[]{ 0.0449, 0.0296 }, new double[]{ 0.0043, -0.0336 }, new double[]{ 0.0059, -0.0234 }, new double[]{ 0.0163, -0.0622 }, new double[]{ -0.0193, 0.0523 }, new double[]{ -0.031, -0.0173 }, new double[]{ 0.0475, -0.0026 }, new double[]{ 0.0024, 0.0202 }, new double[]{ 0.031, 0.0502 }, new double[]{ 0.0185, -0.0286 }, new double[]{ -0.0375, 0.0087 }, new double[]{ 0.0366, 0.0106 }, new double[]{ 0.0044, 0.0215 }, new double[]{ 0.0177, 0.0256 }, new double[]{ -0.0477, 0.0309 }, new double[]{ 0.0075, -0.0494 }, new double[]{ 0.011, -0.0342 }, new double[]{ 0.0356, 0.0104 }, new double[]{ -0.0211, -0.038 }, new double[]{ -0.0373, -0.0119 }, new double[]{ 0.0205, -0.0424 }, new double[]{ -0.0414, 0.0522 }, new double[]{ 0.0033, 0.0441 }, new double[]{ -0.0459, 0.0222 }, new double[]{ 0.0517, 0.0329 }, new double[]{ 0.0276, -0.044 }, new double[]{ -0.0139, 0.0451 }, new double[]{ 0.0504, 0.0326 }, new double[]{ -0.018, 0.0274 }, new double[]{ -0.018, 0.0077 }, new double[]{ -0.0498, 0.0122 }, new double[]{ 0.0206, 0.0406 }, new double[]{ -0.0198, -0.033 }, new double[]{ -0.0355, -0.0159 }, new double[]{ -0.0043, 0.0261 }, new double[]{ 0.0274, -0.0242 }, new double[]{ 0.0538, 0.0533 }, new double[]{ 0.0094, -0.0163 }, new double[]{ -0.0167, 0.0517 }, new double[]{ 0.02, 0.0347 }, new double[]{ -0.024, 0.0132 }, new double[]{ 2.0E-4, 0.0348 }, new double[]{ -0.0122, 0.0411 }, new double[]{ -0.0481, 0.0088 }, new double[]{ 0.0428, 0.0438 }, new double[]{ -0.02, 0.0336 }, new double[]{ 0.0151, -0.0363 }, new double[]{ 0.041, -0.0616 }, new double[]{ -0.003, 0.0071 }, new double[]{ -0.0024, -0.0163 }, new double[]{ 0.0182, -0.0386 }, new double[]{ 0.0123, 0.0508 }, new double[]{ 0.0257, 0.0308 }, new double[]{ -0.0398, -0.0208 }, new double[]{ -0.0139, -0.0149 }, new double[]{ -0.0046, 0.0102 }, new double[]{ -0.0314, 0.007 }, new double[]{ -0.0412, 0.0551 }, new double[]{ -0.0129, 0.0197 }, new double[]{ -0.0357, -0.0422 }, new double[]{ -0.04, -0.0137 }, new double[]{ -0.0373, -0.0252 }, new double[]{ -0.0408, -0.0386 }, new double[]{ 0.0023, -0.0333 }, new double[]{ -0.0252, 0.0327 }, new double[]{ -0.0539, 0.0375 }, new double[]{ -0.0368, 0.0031 }, new double[]{ -0.0022, -0.0294 }, new double[]{ 0.0219, -0.0336 }, new double[]{ 0.0304, 0.0286 }, new double[]{ 0.0549, 0.0407 }, new double[]{ 0.0401, 0.0339 }, new double[]{ 0.0432, -0.0505 }, new double[]{ 0.0094, -1.0E-4 }, new double[]{ 0.0391, -0.0231 }, new double[]{ 0.0191, 0.0411 }, new double[]{ -0.0183, -0.018 }, new double[]{ 0.0446, -0.0392 }, new double[]{ 0.0061, -0.0285 }, new double[]{ -0.0329, -0.014 }, new double[]{ 0.0304, 0.0434 }, new double[]{ 0.0288, -0.0254 }, new double[]{ -0.0389, 0.048 }, new double[]{ 0.0147, -0.0384 }, new double[]{ -0.0275, -0.0391 }, new double[]{ 0.0493, 0.0102 }, new double[]{ 0.0341, -0.031 }, new double[]{ -0.0278, -0.016 }, new double[]{ 0.0288, 0.0167 }, new double[]{ -0.0041, -0.0147 }, new double[]{ 0.0338, -0.0636 }, new double[]{ -0.0172, -0.0439 }, new double[]{ -0.0333, 0.0124 }, new double[]{ 0.0043, -0.0435 }, new double[]{ 0.0145, 9.0E-4 }, new double[]{ 0.0428, 0.0184 }, new double[]{ 0.05, 0.049 }, new double[]{ -0.0232, -0.0163 }, new double[]{ -0.0447, 0.0476 }, new double[]{ -0.041, 0.0503 }, new double[]{ -0.0245, 0.0376 }, new double[]{ -0.024, -0.0181 }, new double[]{ -0.0216, -0.0405 }, new double[]{ 0.0245, -0.0061 }, new double[]{ -0.042, 0.0107 }, new double[]{ -0.017, 0.0476 }, new double[]{ 0.0082, -0.0145 }, new double[]{ -0.0171, 0.0431 }, new double[]{ 0.0511, 0.049 }, new double[]{ -0.0322, -0.0333 }, new double[]{ 0.0047, -0.0359 }, new double[]{ 0.024, 0.0067 }, new double[]{ -0.0207, 0.0255 }, new double[]{ -0.0396, 0.0064 }, new double[]{ 5.0E-4, -0.0431 }, new double[]{ 0.0278, -6.0E-4 }, new double[]{ 0.019, 0.0074 }, new double[]{ 0.0315, -0.0586 }, new double[]{ -0.0193, 0.0311 }, new double[]{ -0.0452, 0.0555 }, new double[]{ -0.0075, -0.0057 }, new double[]{ -0.0067, -0.0078 }, new double[]{ 0.0152, -0.0379 }, new double[]{ 0.0504, 0.0437 }, new double[]{ -0.0406, -0.0287 }, new double[]{ -0.044, 0.0397 }, new double[]{ 0.0042, 0.0297 }, new double[]{ -0.0154, -0.0359 }, new double[]{ 0.0166, 0.0201 }, new double[]{ -0.0511, 0.0172 }, new double[]{ -0.0172, 0.0492 }, new double[]{ -0.0124, -0.0374 }, new double[]{ -0.0207, 0.0444 }, new double[]{ -0.0408, -0.0392 }, new double[]{ -0.0222, -0.0345 }, new double[]{ 0.0411, -0.0353 }, new double[]{ -0.0352, 0.0522 }, new double[]{ 0.046, -0.0099 }, new double[]{ -0.031, 0.0331 }, new double[]{ -0.0077, 0.0379 }, new double[]{ 0.0224, 0.0305 }, new double[]{ 0.0577, 0.0483 }, new double[]{ 0.0452, -0.0092 }, new double[]{ -0.0076, 0.0144 }, new double[]{ -0.0487, 0.0508 }, new double[]{ -0.0471, 0.0153 }, new double[]{ -0.041, 0.0198 }, new double[]{ -0.0457, 0.041 }, new double[]{ -0.0239, -0.0293 }, new double[]{ 0.0171, 0.0112 }, new double[]{ -0.0235, 0.0077 }, new double[]{ 0.021, -0.0371 }, new double[]{ 0.0526, 0.0312 }, new double[]{ 0.0234, -0.0254 }, new double[]{ 0.0149, -0.0425 }, new double[]{ -0.0288, 0.0508 }, new double[]{ -0.0297, 1.0E-4 }, new double[]{ -0.0249, -0.0021 }, new double[]{ 0.0478, 0.0404 }, new double[]{ 0.0173, 0.0149 }, new double[]{ -0.0121, -0.0292 }, new double[]{ -0.043, 0.0528 }, new double[]{ 0.0261, 0.0277 }, new double[]{ 0.0584, 0.0609 }, new double[]{ 0.0156, -0.0188 }, new double[]{ 0.0213, 0.0246 }, new double[]{ 0.0429, 0.0349 }, new double[]{ 0.0362, -0.0313 }, new double[]{ -0.0023, -0.0114 }, new double[]{ 0.0227, 0.0282 }, new double[]{ -0.02, -0.012 }, new double[]{ -0.0215, -0.0146 }, new double[]{ 0.0233, -0.0306 }, new double[]{ -0.0428, 0.0261 }, new double[]{ 0.0589, 0.0614 }, new double[]{ -5.0E-4, -0.009 }, new double[]{ -0.0107, -0.0053 }, new double[]{ 0.0243, -0.0336 }, new double[]{ 0.0397, -0.0545 }, new double[]{ -0.0347, -0.0064 }, new double[]{ 0.0427, -0.0112 }, new double[]{ -0.0123, -0.0339 }, new double[]{ 0.0217, -0.0355 }, new double[]{ 8.0E-4, -0.0096 }, new double[]{ -0.016, -0.0243 }, new double[]{ -0.0264, 0.0448 }, new double[]{ -0.0091, 0.0264 }, new double[]{ 0.0012, -0.0412 }, new double[]{ -0.0363, 0.0064 }, new double[]{ -0.0284, -0.0152 }, new double[]{ 0.0389, 0.0206 }, new double[]{ 0.0218, 0.0433 }, new double[]{ -0.0437, 0.0149 }, new double[]{ -0.0516, 0.0384 }, new double[]{ 0.016, 0.0375 }, new double[]{ -0.0029, 0.0106 }, new double[]{ 0.0433, -0.0052 }, new double[]{ 0.0378, -0.041 }, new double[]{ 0.0222, -0.0222 }, new double[]{ 0.0442, -0.0077 }, new double[]{ 0.0266, -0.0075 }, new double[]{ -0.0381, -0.0362 }, new double[]{ 0.0164, -0.0066 }, new double[]{ -0.0032, 0.034 }, new double[]{ -0.0118, -0.0347 }, new double[]{ 0.0183, 0.0506 }, new double[]{ 0.0071, 0.039 }, new double[]{ -0.0039, 0.05 }, new double[]{ -0.0488, 0.0481 }, new double[]{ -0.0522, 0.0416 }, new double[]{ 0.021, -0.0645 }, new double[]{ 0.029, -0.022 }, new double[]{ 0.0486, 0.0069 }, new double[]{ 0.0189, -0.0185 }, new double[]{ 0.0408, -0.0621 }, new double[]{ -0.0084, -0.0081 }, new double[]{ -0.042, -0.0206 }, new double[]{ 0.0349, 0.0446 }, new double[]{ -0.0344, -0.0236 }, new double[]{ -0.0294, 0.0038 }, new double[]{ -0.0334, -0.0197 }, new double[]{ -0.0457, 0.0544 }, new double[]{ 0.0503, 0.0113 }, new double[]{ 0.0122, 0.037 }, new double[]{ -0.0262, -0.0393 }, new double[]{ 0.0491, 0.0521 }, new double[]{ 0.05, 0.0286 }, new double[]{ -0.0404, 0.0013 }, new double[]{ 0.0616, 0.0655 }, new double[]{ -0.0386, 0.0206 }, new double[]{ -0.0346, 0.0256 }, new double[]{ 0.004, 0.0129 }, new double[]{ -0.0466, -0.0035 }, new double[]{ -0.023, -0.0095 }, new double[]{ -0.0509, 0.0261 }, new double[]{ -0.0255, 0.0211 }, new double[]{ 0.0365, -0.0251 }, new double[]{ -0.0104, 0.0075 }, new double[]{ -0.0271, -0.0267 }, new double[]{ -0.0386, -0.0305 }, new double[]{ -0.02, -0.029 }, new double[]{ 0.0555, 0.0512 }, new double[]{ -0.0164, -7.0E-4 }, new double[]{ -0.0382, -0.0025 }, new double[]{ 0.0479, 0.0295 }, new double[]{ -0.037, -0.0221 }, new double[]{ 0.0251, 0.0055 }, new double[]{ -0.0433, -0.0308 }, new double[]{ -0.0456, 0.0446 }, new double[]{ -0.0405, -0.0375 }, new double[]{ -0.0104, -0.0421 }, new double[]{ -0.0434, -0.0232 }, new double[]{ 0.0494, 0.0404 }, new double[]{ -0.048, 0.0235 }, new double[]{ -0.0057, -0.0192 }, new double[]{ -0.049, 0.0394 }, new double[]{ 0.0394, -0.0125 }, new double[]{ -0.039, -0.0189 }, new double[]{ -0.001, -0.0339 }, new double[]{ -3.0E-4, -0.0256 }, new double[]{ 0.0562, 0.0381 }, new double[]{ -0.0081, 0.015 }, new double[]{ -0.0033, -8.0E-4 }, new double[]{ 0.0258, 0.0234 }, new double[]{ 0.0138, -0.0221 }, new double[]{ -0.0325, -0.0338 }, new double[]{ 0.0386, -0.0522 }, new double[]{ 0.0379, -0.0374 }, new double[]{ 0.0402, 0.0317 }, new double[]{ -0.0347, 0.015 }, new double[]{ -0.0051, 0.0357 }, new double[]{ -0.0114, 0.018 }, new double[]{ -0.0097, 0.0152 }, new double[]{ -0.005, -0.0322 }, new double[]{ -0.0416, 0.0212 }, new double[]{ 0.0425, 0.0511 }, new double[]{ 0.0145, -0.0086 }, new double[]{ -0.026, -0.0203 }, new double[]{ -0.037, 0.0141 }, new double[]{ -0.0063, -0.045 }, new double[]{ -0.0344, 0.0444 }, new double[]{ 0.0511, 0.0153 }, new double[]{ -0.0485, 0.0039 }, new double[]{ 0.0163, 0.0265 }, new double[]{ -0.007, -0.0051 }, new double[]{ 0.0434, -0.0445 }, new double[]{ -0.018, 0.0287 }, new double[]{ -0.0353, -0.0429 }, new double[]{ -0.0436, -0.0083 }, new double[]{ -0.0013, -0.0466 }, new double[]{ 0.0339, -0.0196 }, new double[]{ -0.0416, 0.0156 }, new double[]{ -0.0424, -0.0263 }, new double[]{ -0.031, -0.0095 }, new double[]{ 0.0463, 7.0E-4 }, new double[]{ -0.0366, -0.0291 }, new double[]{ -0.0173, -0.0387 }, new double[]{ 0.0429, -0.0563 }, new double[]{ 0.0148, -0.0366 }, new double[]{ -0.0136, 0.0346 }, new double[]{ 0.0434, -0.0113 }, new double[]{ -0.0423, -0.0072 }, new double[]{ 0.0432, 0.0471 }, new double[]{ -0.0317, 0.0414 }, new double[]{ 0.0554, 0.0349 }, new double[]{ 0.0516, 0.0212 }, new double[]{ -0.039, 0.0188 }, new double[]{ 0.0224, -0.0288 }, new double[]{ -0.0066, 0.0231 }, new double[]{ -0.0306, -0.0214 }, new double[]{ -0.0456, 0.0168 }, new double[]{ -0.0264, -0.0026 }, new double[]{ -0.02, 0.0095 }, new double[]{ -0.0076, -0.0304 }, new double[]{ 0.0075, 0.0105 }, new double[]{ 0.001, 0.0344 }, new double[]{ 0.0034, 0.0505 }, new double[]{ 0.042, -0.0108 }, new double[]{ 0.0365, 0.0055 }, new double[]{ -0.022, -0.0113 }, new double[]{ -0.0443, 0.003 }, new double[]{ 0.0298, -0.0556 }, new double[]{ -0.0029, 0.0486 }, new double[]{ -0.0284, -0.0141 }, new double[]{ -0.0304, 0.0346 }, new double[]{ -0.0029, 0.0309 }, new double[]{ -0.0245, 0.0102 }, new double[]{ 0.0532, 0.0384 }, new double[]{ 0.0021, -0.0177 }, new double[]{ -0.0437, -0.0307 }, new double[]{ -0.0426, 0.0209 }, new double[]{ 0.0113, -0.0068 }, new double[]{ 0.0594, 0.0562 }, new double[]{ -0.0023, 0.0059 }, new double[]{ -0.0218, -0.0353 }, new double[]{ -0.0319, 0.0286 }, new double[]{ 0.0362, 0.0409 }, new double[]{ -0.0317, 0.0094 }, new double[]{ 0.039, -0.029 }, new double[]{ 0.0436, 0.0323 }, new double[]{ 0.0416, -0.0478 }, new double[]{ 0.0399, -0.0021 }, new double[]{ -0.0307, -0.0415 }, new double[]{ -0.0429, 0.0323 }, new double[]{ 0.0084, -0.0487 }, new double[]{ -0.0515, 0.0383 }, new double[]{ 0.044, -0.0069 }, new double[]{ 0.0264, -0.0037 }, new double[]{ -0.0129, -0.0333 }, new double[]{ -0.0477, 0.0183 }, new double[]{ 0.012, -0.0378 }, new double[]{ 0.0365, 0.0045 }, new double[]{ -0.0315, -0.0358 }, new double[]{ 0.0112, -0.0223 }, new double[]{ -0.0064, -0.0372 }, new double[]{ -0.0354, 0.0433 }, new double[]{ 0.0322, 0.0338 }, new double[]{ 0.0104, -0.0052 }, new double[]{ -0.0257, -0.0275 }, new double[]{ -0.0039, -0.0372 }, new double[]{ 0.0192, 0.0107 }, new double[]{ 0.0324, -0.0549 }, new double[]{ 0.0406, 5.0E-4 }, new double[]{ -0.0344, -0.0381 }, new double[]{ 0.0041, -0.0419 }, new double[]{ -0.0065, -0.0122 }, new double[]{ 0.0383, -0.0039 }, new double[]{ -0.0332, -0.011 }, new double[]{ 0.0407, -0.0494 }, new double[]{ -0.0198, 0.0261 }, new double[]{ -0.0238, 0.0104 }, new double[]{ 0.0511, 0.0563 }, new double[]{ 0.0534, 0.0281 }, new double[]{ -0.0408, -0.0365 }, new double[]{ 0.03, 0.0135 }, new double[]{ -0.0336, 0.0059 }, new double[]{ 0.0327, -0.064 }, new double[]{ 0.0344, -0.046 }, new double[]{ -0.042, -0.0342 }, new double[]{ -0.0011, 0.0215 }, new double[]{ -0.0532, 0.0373 }, new double[]{ 0.0136, 2.0E-4 }, new double[]{ 0.0374, -0.0487 }, new double[]{ 0.0219, -0.0146 }, new double[]{ 0.0354, 0.0064 }, new double[]{ -0.0476, 0.0433 }, new double[]{ 0.0446, -0.0395 }, new double[]{ 0.024, -0.0374 }, new double[]{ 0.0248, -0.032 }, new double[]{ -0.0355, -0.0292 }, new double[]{ 0.0394, -0.0012 }, new double[]{ 0.022, 0.022 }, new double[]{ -0.0324, 0.0282 }, new double[]{ 0.0096, 3.0E-4 }, new double[]{ 0.0279, 0.0276 }, new double[]{ 0.0056, -0.0308 }, new double[]{ 0.0452, -2.0E-4 }, new double[]{ -0.0141, 0.0268 }, new double[]{ -0.0181, -0.014 }, new double[]{ -0.0313, -0.0174 }, new double[]{ 0.0546, 0.0399 }, new double[]{ 0.0314, 0.048 }, new double[]{ -0.021, -0.0149 }, new double[]{ -0.0418, 0.0214 }, new double[]{ 0.0247, -0.0491 }, new double[]{ -0.0053, -0.0144 }, new double[]{ 0.04, -0.0411 }, new double[]{ -0.0303, 0.0459 }, new double[]{ -0.027, 0.0082 }, new double[]{ -0.0318, 0.0502 }, new double[]{ -0.0312, -0.028 }, new double[]{ -0.0343, -0.0039 }, new double[]{ -0.024, 0.016 }, new double[]{ -0.0155, 0.0308 }, new double[]{ 0.016, 0.0339 }, new double[]{ -0.0131, 0.0484 }, new double[]{ -0.048, 0.033 }, new double[]{ 0.0251, -0.0172 }, new double[]{ 0.0158, -0.0557 }, new double[]{ 0.0336, 0.0299 }, new double[]{ 0.046, 0.0307 }, new double[]{ -0.0243, 0.0505 }, new double[]{ -0.0193, -0.0357 }, new double[]{ -0.0219, -0.0026 }, new double[]{ 0.0339, -0.0472 }, new double[]{ -0.0193, 0.0328 }, new double[]{ -0.0408, -0.0407 }, new double[]{ 0.0258, -0.0095 }, new double[]{ -0.0538, 0.0347 }, new double[]{ 0.0555, 0.0568 }, new double[]{ -0.0404, 0.0236 }, new double[]{ 0.0208, -0.0103 }, new double[]{ -0.0058, 0.0467 }, new double[]{ -0.0161, -0.0345 }, new double[]{ 0.0176, 0.0285 }, new double[]{ 0.0188, -0.0281 }, new double[]{ -0.037, -0.0229 }, new double[]{ -0.0383, -0.0229 }, new double[]{ 0.0147, -0.0495 }, new double[]{ -0.0119, -0.0154 }, new double[]{ 0.031, -0.0409 }, new double[]{ 0.0446, -0.0093 }, new double[]{ 0.0399, -0.0504 }, new double[]{ 0.0441, -0.0067 }, new double[]{ 0.001, 0.0261 }, new double[]{ -0.04, 0.0037 }, new double[]{ 0.0088, -0.0194 }, new double[]{ -0.0115, -0.0424 }, new double[]{ -0.0308, 0.0051 }, new double[]{ -0.004, 0.006 }, new double[]{ 0.0326, 0.0498 }, new double[]{ 0.0228, 0.0065 }, new double[]{ 0.0228, -0.0093 }, new double[]{ 0.0339, 0.0454 }, new double[]{ 6.0E-4, 0.011 }, new double[]{ 0.0139, 0.0497 }, new double[]{ 0.0377, 0.0092 }, new double[]{ -0.0251, -0.0405 }, new double[]{ -0.0489, 0.0222 }, new double[]{ -0.0229, -0.0173 }, new double[]{ -0.0481, 0.0219 }, new double[]{ 0.0076, -0.0248 }, new double[]{ 0.0036, 0.0353 }, new double[]{ 0.059, 0.057 }, new double[]{ 0.0494, 0.0071 }, new double[]{ 0.0379, -0.0563 }, new double[]{ -0.0068, -0.0319 }, new double[]{ -0.0458, 0.029 }, new double[]{ -0.04, -0.0104 }, new double[]{ 0.039, 0.0255 }, new double[]{ 0.0226, 0.0283 }, new double[]{ 0.0434, -0.0142 }, new double[]{ -0.0088, 0.0133 }, new double[]{ 0.0271, -0.0537 }, new double[]{ 0.0614, 0.0642 }, new double[]{ -0.0316, -0.0355 }, new double[]{ -0.0373, -0.0104 }, new double[]{ 0.0238, -0.0288 }, new double[]{ 0.0336, 0.0049 }, new double[]{ 0.0323, 0.0051 }, new double[]{ -0.0442, -0.0246 }, new double[]{ -0.0371, -0.005 }, new double[]{ 0.0227, -0.0281 }, new double[]{ -0.0303, 0.0051 }, new double[]{ -0.026, -0.0132 }, new double[]{ -0.0288, 0.0401 }, new double[]{ 0.0388, 0.0244 }, new double[]{ -0.0116, -0.0183 }, new double[]{ -0.0065, 0.0021 }, new double[]{ -0.0278, -0.0265 }, new double[]{ 0.0145, -0.0364 }, new double[]{ -8.0E-4, 0.0179 }, new double[]{ -0.0079, 0.0345 }, new double[]{ 0.0084, 0.0412 }, new double[]{ -0.0026, -0.0185 }, new double[]{ 0.0412, 0.0334 }, new double[]{ -0.0431, 0.0174 }, new double[]{ -0.0478, -7.0E-4 }, new double[]{ 0.0461, 0.0099 }, new double[]{ -0.0018, -0.0125 }, new double[]{ -0.0306, 0.0265 }, new double[]{ -0.0241, -0.0219 }, new double[]{ -0.0097, 0.0533 }, new double[]{ 0.0206, 0.0396 }, new double[]{ -0.0478, 0.0023 }, new double[]{ -0.0363, 0.0131 }, new double[]{ -0.0314, -0.0162 }, new double[]{ 0.0069, 0.0316 }, new double[]{ 0.0295, -9.0E-4 }, new double[]{ -0.0127, 0.0164 }, new double[]{ 0.0033, -0.054 }, new double[]{ -0.0099, 0.0237 }, new double[]{ -0.0355, 0.0054 }, new double[]{ -0.0281, -0.0289 }, new double[]{ -0.0366, 0.0167 }, new double[]{ 0.0143, -0.0603 }, new double[]{ -0.0028, 0.0544 }, new double[]{ 0.0487, 0.0551 }, new double[]{ 0.0141, -0.0508 }, new double[]{ 0.0376, -0.0196 }, new double[]{ 0.0164, 0.0317 }, new double[]{ -0.0354, 0.0165 }, new double[]{ -0.0297, -0.0232 }, new double[]{ -0.0342, -2.0E-4 }, new double[]{ 0.005, -0.0286 }, new double[]{ 0.0231, -0.0195 }, new double[]{ -0.0098, 0.0376 }, new double[]{ -0.0347, -0.036 }, new double[]{ -0.038, 0.0245 }, new double[]{ -0.0294, -0.0307 }, new double[]{ -0.0225, -0.0406 }, new double[]{ -0.04, -0.0291 }, new double[]{ 0.0158, -0.0174 }, new double[]{ 0.0432, 0.0402 }, new double[]{ 0.0386, -0.0408 }, new double[]{ -0.0051, 0.0361 }, new double[]{ -0.0023, -0.0083 }, new double[]{ 0.0385, 0.017 }, new double[]{ 0.0322, -0.0633 }, new double[]{ -0.018, -0.0359 }, new double[]{ 0.0095, 0.0152 }, new double[]{ -0.0322, 0.0099 }, new double[]{ -0.0065, 0.0233 }, new double[]{ -0.004, -0.0322 }, new double[]{ 0.0269, -0.0134 }, new double[]{ -0.0325, 0.0063 }, new double[]{ 0.0484, -5.0E-4 }, new double[]{ 0.0581, 0.0479 }, new double[]{ -0.041, -0.025 }, new double[]{ 0.001, 0.0122 }, new double[]{ -0.0344, -0.0039 }, new double[]{ -0.0338, -0.003 }, new double[]{ -0.0419, 0.0107 }, new double[]{ -0.0133, -0.0184 }, new double[]{ -0.0262, 0.0382 }, new double[]{ 0.0188, -0.0575 }, new double[]{ -0.0225, -0.0067 }, new double[]{ -0.0148, 0.0019 }, new double[]{ -0.0251, -0.0273 }, new double[]{ 0.0355, -0.0322 }, new double[]{ 0.0156, 0.0334 }, new double[]{ -0.0494, 0.0212 }, new double[]{ -0.0231, 0.0026 }, new double[]{ -0.0517, 0.0276 }, new double[]{ 0.0198, -0.0107 }, new double[]{ -0.0207, -0.0128 }, new double[]{ -0.0246, 0.0519 }, new double[]{ 0.0245, 0.0364 }, new double[]{ 0.0497, 0.0106 }, new double[]{ -0.0186, 0.039 }, new double[]{ 0.0512, 0.0219 }, new double[]{ 0.0196, 0.0172 }, new double[]{ -0.0374, -0.0132 }, new double[]{ 0.0307, -0.0502 }, new double[]{ -0.0034, 0.0128 }, new double[]{ 0.0309, -0.0086 }, new double[]{ 0.0257, -0.0373 }, new double[]{ -0.0456, -0.0081 }, new double[]{ 0.0582, 0.0518 }, new double[]{ -0.0382, -0.0066 }, new double[]{ -0.0303, -0.0285 }, new double[]{ -0.0381, -0.0191 }, new double[]{ -0.009, -0.0152 }, new double[]{ -0.031, -0.016 }, new double[]{ 0.0459, -0.0062 }, new double[]{ 0.0448, 0.0074 }, new double[]{ 0.0062, -0.0405 }, new double[]{ 0.0192, 0.0207 }, new double[]{ 0.0359, -0.0364 }, new double[]{ -0.0375, -0.0319 }, new double[]{ -0.0231, -0.005 }, new double[]{ -0.0232, 0.0358 }, new double[]{ -0.0365, -0.0258 }, new double[]{ 0.0599, 0.0615 }, new double[]{ -0.0468, -0.002 }, new double[]{ 0.012, -0.0092 }, new double[]{ -0.0058, -0.0191 }, new double[]{ 0.0175, 0.0275 }, new double[]{ -0.0433, -0.0308 }, new double[]{ 0.0299, -0.0296 }, new double[]{ -0.0331, -0.0391 }, new double[]{ 0.0236, -0.0018 }, new double[]{ 0.0019, 0.0476 }, new double[]{ 0.0482, 0.0244 }, new double[]{ 0.0407, -0.0194 }, new double[]{ 0.0269, 0.0365 }, new double[]{ -0.0308, -0.043 }, new double[]{ -0.0218, -0.0033 }, new double[]{ -0.0512, 0.039 }, new double[]{ -0.0261, -0.0203 }, new double[]{ -0.0133, -0.024 }, new double[]{ 0.0086, -0.0335 }, new double[]{ 0.0216, 0.034 }, new double[]{ 0.0344, -0.0042 }, new double[]{ -0.0161, -0.0384 }, new double[]{ 0.023, 0.0401 }, new double[]{ -0.0172, -0.0407 }, new double[]{ -0.0274, -0.0408 }, new double[]{ 0.0481, 0.012 }, new double[]{ 0.0152, 0.033 }, new double[]{ -0.0291, 0.0222 }, new double[]{ 0.0361, -0.0543 }, new double[]{ 0.0412, -0.0022 }, new double[]{ 0.0297, -0.0248 }, new double[]{ -0.0094, -0.0371 }, new double[]{ 6.0E-4, -0.0011 }, new double[]{ -0.0289, -0.0211 }, new double[]{ -0.0136, -0.0024 }, new double[]{ 0.0542, 0.0283 }, new double[]{ 0.0301, 0.0021 }, new double[]{ -0.0481, 0.0522 }, new double[]{ 0.0172, -0.0095 }, new double[]{ -5.0E-4, 0.0299 }, new double[]{ -0.034, -0.0103 }, new double[]{ 0.0112, 0.0444 }, new double[]{ 0.0402, 0.0411 }, new double[]{ 0.0092, -0.0158 }, new double[]{ -0.035, -0.0197 }, new double[]{ 0.0266, 0.0286 }, new double[]{ 0.0165, 5.0E-4 }, new double[]{ 0.0412, -0.0363 }, new double[]{ -0.0228, 0.0187 }, new double[]{ 0.0023, -0.0446 }, new double[]{ 0.0314, 0.0175 }, new double[]{ -0.013, -0.033 }, new double[]{ 0.0403, -0.0522 }, new double[]{ 0.0344, -0.0209 }, new double[]{ 0.0193, -0.0485 }, new double[]{ -0.0276, 0.0431 }, new double[]{ -0.011, -0.0329 }, new double[]{ 0.0168, -0.0083 }, new double[]{ -0.0506, 0.0456 }, new double[]{ -0.0378, -0.0376 }, new double[]{ -0.0489, 0.0252 }, new double[]{ -0.0026, -0.0489 }, new double[]{ -0.0338, 0.036 }, new double[]{ 0.0343, -0.0434 }, new double[]{ -0.0333, -0.0264 }, new double[]{ 0.011, -0.0542 }, new double[]{ -0.0426, 0.0391 }, new double[]{ -0.0456, 0.0297 }, new double[]{ -0.0255, -0.0403 }, new double[]{ 0.0287, 0.0105 }, new double[]{ -0.0159, 0.0267 }, new double[]{ 0.0391, -0.028 }, new double[]{ 0.0408, -0.0497 }, new double[]{ -0.0244, 0.0526 }, new double[]{ -0.0144, 0.0536 }, new double[]{ 0.0257, 0.0288 }, new double[]{ 0.0331, 0.0131 }, new double[]{ -0.021, -0.0152 }, new double[]{ -0.0471, 0.0228 }, new double[]{ -0.0472, 0.0436 }, new double[]{ 0.049, 0.0417 }, new double[]{ -0.0428, 6.0E-4 }, new double[]{ 0.0459, 0.0041 }, new double[]{ 0.0349, 0.0072 }, new double[]{ 0.0459, 0.0081 }, new double[]{ 0.008, 0.0301 }, new double[]{ -0.0219, 0.01 }, new double[]{ 0.0333, -0.0603 }, new double[]{ -1.0E-4, -0.0513 }, new double[]{ 0.0306, 0.0298 }, new double[]{ -0.028, 0.0545 }, new double[]{ -0.0039, 0.0498 }, new double[]{ -0.0035, -0.0436 }, new double[]{ 0.0174, 0.0272 }, new double[]{ -0.0393, -0.0011 }, new double[]{ -0.0428, -0.0163 }, new double[]{ 0.0121, 0.0407 }, new double[]{ 0.0352, 0.0324 }, new double[]{ -0.0504, 0.033 } };
        double[][] dat = swissroll.toArray(new double[swissroll.size()][]);
        double[][] data = new double[1000][];
        System.arraycopy(dat, 0, data, 0, data.length);
        LLE lle = new LLE(data, 2, 7);
        double[][] coords = lle.getCoordinates();
        for (int i = 0; i < (points.length); i++) {
            for (int j = 0; j < (points[0].length); j++) {
                Assert.assertEquals(Math.abs(points[i][j]), Math.abs(coords[i][j]), 1.0E-4);
            }
        }
    }
}
