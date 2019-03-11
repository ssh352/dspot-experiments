/**
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Library (Core)
 * FILENAME      :  GpioCallbackTriggerTests.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2019 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package com.pi4j.io.gpio.trigger.test;


import PinState.HIGH;
import PinState.LOW;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.test.MockGpioProvider;
import com.pi4j.io.gpio.test.MockPin;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import org.junit.Assert;
import org.junit.Test;


public class GpioCallbackTriggerTests {
    private static MockGpioProvider provider;

    private static GpioController gpio;

    private static GpioPinDigitalInput inputPin;

    private static GpioCallbackTrigger trigger;

    private static int callbackCounter = 0;

    @Test
    public void testHasTrigger() {
        // verify that the input pin does have a trigger assigned
        Assert.assertFalse(GpioCallbackTriggerTests.inputPin.getTriggers().isEmpty());
    }

    @Test
    public void testTrigger() throws InterruptedException {
        // reset counter
        GpioCallbackTriggerTests.callbackCounter = 0;
        // update pin state
        GpioCallbackTriggerTests.provider.setMockState(MockPin.DIGITAL_INPUT_PIN, LOW);
        // wait before continuing test
        Thread.sleep(50);
        // verify that the callback counter is correct
        Assert.assertEquals(1, GpioCallbackTriggerTests.callbackCounter);
        // update pin state
        GpioCallbackTriggerTests.provider.setMockState(MockPin.DIGITAL_INPUT_PIN, HIGH);
        // wait before continuing test
        Thread.sleep(50);
        // verify that the callback counter is correct
        Assert.assertEquals(2, GpioCallbackTriggerTests.callbackCounter);
        // update pin state
        GpioCallbackTriggerTests.provider.setMockState(MockPin.DIGITAL_INPUT_PIN, LOW);
        // wait before continuing test
        Thread.sleep(50);
        // verify that the callback counter is correct
        Assert.assertEquals(3, GpioCallbackTriggerTests.callbackCounter);
        // update pin state
        GpioCallbackTriggerTests.provider.setMockState(MockPin.DIGITAL_INPUT_PIN, HIGH);
        // wait before continuing test
        Thread.sleep(50);
        // verify that the callback counter is correct
        Assert.assertEquals(4, GpioCallbackTriggerTests.callbackCounter);
    }
}

