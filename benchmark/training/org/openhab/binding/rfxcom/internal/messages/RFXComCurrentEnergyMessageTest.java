/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.rfxcom.internal.messages;


import RFXComCurrentEnergyMessage.SubType.ELEC4;
import org.junit.Test;
import org.openhab.binding.rfxcom.internal.exceptions.RFXComException;


/**
 * Test for RFXCom-binding
 *
 * @author Martin van Wingerden
 */
public class RFXComCurrentEnergyMessageTest {
    @Test
    public void testSomeMessages() throws RFXComException {
        testMessage("135B0106B800000016000000000000006F148889", ELEC4, 6, "47104", 0, 2.2, 0.0, 0.0, 32547.4, 8, 9);
        testMessage("135B014FB80002001D0000000000000000000079", ELEC4, 79, "47104", 2, 2.9, 0.0, 0.0, 0.0, 7, 9);
    }
}

