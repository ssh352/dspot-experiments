/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.event.aggregator;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Date: 12/12/15 - 3:04 PM
 *
 * @author Jeroen Meulemeester
 */
public class KingJoffreyTest {
    private KingJoffreyTest.InMemoryAppender appender;

    /**
     * Test if {@link KingJoffrey} tells us what event he received
     */
    @Test
    public void testOnEvent() {
        final KingJoffrey kingJoffrey = new KingJoffrey();
        for (int i = 0; i < (Event.values().length); ++i) {
            Assertions.assertEquals(i, appender.getLogSize());
            Event event = Event.values()[i];
            kingJoffrey.onEvent(event);
            final String expectedMessage = "Received event from the King's Hand: " + (event.toString());
            Assertions.assertEquals(expectedMessage, appender.getLastMessage());
            Assertions.assertEquals((i + 1), appender.getLogSize());
        }
    }

    private class InMemoryAppender extends AppenderBase<ILoggingEvent> {
        private List<ILoggingEvent> log = new LinkedList<>();

        public InMemoryAppender(Class<?> clazz) {
            addAppender(this);
            start();
        }

        @Override
        protected void append(ILoggingEvent eventObject) {
            log.add(eventObject);
        }

        public String getLastMessage() {
            return log.get(((log.size()) - 1)).getFormattedMessage();
        }

        public int getLogSize() {
            return log.size();
        }
    }
}
