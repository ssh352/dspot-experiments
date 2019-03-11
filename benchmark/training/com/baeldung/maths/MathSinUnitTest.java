package com.baeldung.maths;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class MathSinUnitTest {
    @Test
    public void givenAnAngleInDegrees_whenUsingToRadians_thenResultIsInRadians() {
        double angleInDegrees = 30;
        double sinForDegrees = Math.sin(Math.toRadians(angleInDegrees));// 0.5

        double thirtyDegreesInRadians = (((double) (1)) / 6) * (Math.PI);
        double sinForRadians = Math.sin(thirtyDegreesInRadians);// 0.5

        Assert.assertThat(sinForDegrees, CoreMatchers.is(sinForRadians));
    }
}

