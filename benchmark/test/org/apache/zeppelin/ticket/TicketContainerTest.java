/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.ticket;


import org.junit.Assert;
import org.junit.Test;


public class TicketContainerTest {
    private TicketContainer container;

    @Test
    public void isValidAnonymous() {
        boolean ok = container.isValid("anonymous", "anonymous");
        Assert.assertTrue(ok);
    }

    @Test
    public void isValidExistingPrincipal() {
        String ticket = container.getTicket("someuser1");
        boolean ok = container.isValid("someuser1", ticket);
        Assert.assertTrue(ok);
    }

    @Test
    public void isValidNonExistingPrincipal() {
        boolean ok = container.isValid("unknownuser", "someticket");
        Assert.assertFalse(ok);
    }

    @Test
    public void isValidunkownTicket() {
        String ticket = container.getTicket("someuser2");
        boolean ok = container.isValid("someuser2", (ticket + "makeitinvalid"));
        Assert.assertFalse(ok);
    }
}

