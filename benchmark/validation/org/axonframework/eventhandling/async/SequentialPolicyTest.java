/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventhandling.async;


import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class SequentialPolicyTest {
    @Test
    public void testSequencingIdentifier() {
        // ok, pretty useless, but everything should be tested
        SequentialPolicy testSubject = new SequentialPolicy();
        Object id1 = testSubject.getSequenceIdentifierFor(newStubDomainEvent(UUID.randomUUID()));
        Object id2 = testSubject.getSequenceIdentifierFor(newStubDomainEvent(UUID.randomUUID()));
        Object id3 = testSubject.getSequenceIdentifierFor(newStubDomainEvent(UUID.randomUUID()));
        Assert.assertEquals(id1, id2);
        Assert.assertEquals(id2, id3);
        // this can only fail if equals is not implemented correctly
        Assert.assertEquals(id1, id3);
    }
}

