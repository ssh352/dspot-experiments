/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.util;


import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Mikhail Kalinin
 * @since 15.10.2015
 */
public class BIUtilTest {
    @Test
    public void testIsIn20PercentRange() {
        Assert.assertTrue(isIn20PercentRange(BigInteger.valueOf(20000), BigInteger.valueOf(24000)));
        Assert.assertTrue(isIn20PercentRange(BigInteger.valueOf(24000), BigInteger.valueOf(20000)));
        Assert.assertFalse(isIn20PercentRange(BigInteger.valueOf(20000), BigInteger.valueOf(25000)));
        Assert.assertTrue(isIn20PercentRange(BigInteger.valueOf(20), BigInteger.valueOf(24)));
        Assert.assertTrue(isIn20PercentRange(BigInteger.valueOf(24), BigInteger.valueOf(20)));
        Assert.assertFalse(isIn20PercentRange(BigInteger.valueOf(20), BigInteger.valueOf(25)));
        Assert.assertTrue(isIn20PercentRange(BigInteger.ZERO, BigInteger.ZERO));
        Assert.assertFalse(isIn20PercentRange(BigInteger.ZERO, BigInteger.ONE));
        Assert.assertTrue(isIn20PercentRange(BigInteger.ONE, BigInteger.ZERO));
    }

    // test isIn20PercentRange
    @Test
    public void test1() {
        Assert.assertFalse(isIn20PercentRange(BigInteger.ONE, BigInteger.valueOf(5)));
        Assert.assertTrue(isIn20PercentRange(BigInteger.valueOf(5), BigInteger.ONE));
        Assert.assertTrue(isIn20PercentRange(BigInteger.valueOf(5), BigInteger.valueOf(6)));
        Assert.assertFalse(isIn20PercentRange(BigInteger.valueOf(5), BigInteger.valueOf(7)));
    }
}
