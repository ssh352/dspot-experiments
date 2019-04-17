/**
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.item.jms;


import javax.jms.Message;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Dave Syer
 * @author Will Schipp
 */
public class JmsNewMethodArgumentsIdentifierTests {
    private JmsNewMethodArgumentsIdentifier<String> newMethodArgumentsIdentifier = new JmsNewMethodArgumentsIdentifier();

    @Test
    public void testIsNewForMessage() throws Exception {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getJMSRedelivered()).thenReturn(true);
        Assert.assertEquals(false, newMethodArgumentsIdentifier.isNew(new Object[]{ message }));
    }

    @Test
    public void testIsNewForNonMessage() throws Exception {
        Assert.assertEquals(false, newMethodArgumentsIdentifier.isNew(new Object[]{ "foo" }));
    }
}
