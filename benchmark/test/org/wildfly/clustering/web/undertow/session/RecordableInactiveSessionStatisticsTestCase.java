/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.clustering.web.undertow.session;


import java.time.Duration;
import java.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.clustering.web.session.ImmutableSession;
import org.wildfly.clustering.web.session.ImmutableSessionMetaData;


/**
 * Unit test for {@link RecordableInactiveSessionStatistics}.
 *
 * @author Paul Ferraro
 */
public class RecordableInactiveSessionStatisticsTestCase {
    private final RecordableInactiveSessionStatistics statistics = new RecordableInactiveSessionStatistics();

    @Test
    public void test() {
        ImmutableSession session = Mockito.mock(ImmutableSession.class);
        ImmutableSessionMetaData metaData = Mockito.mock(ImmutableSessionMetaData.class);
        Instant now = Instant.now();
        Instant created = now.minus(Duration.ofMinutes(20L));
        Mockito.when(session.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.isExpired()).thenReturn(false);
        Mockito.when(metaData.getCreationTime()).thenReturn(created);
        this.statistics.record(session);
        Assert.assertEquals(0L, this.statistics.getExpiredSessionCount());
        Assert.assertEquals(20L, this.statistics.getMaxSessionLifetime().toMinutes());
        Assert.assertEquals(20L, this.statistics.getMeanSessionLifetime().toMinutes());
        now = Instant.now();
        created = now.minus(Duration.ofMinutes(10L));
        Mockito.when(metaData.isExpired()).thenReturn(true);
        Mockito.when(metaData.getCreationTime()).thenReturn(created);
        this.statistics.record(session);
        Assert.assertEquals(1L, this.statistics.getExpiredSessionCount());
        Assert.assertEquals(20L, this.statistics.getMaxSessionLifetime().toMinutes());
        Assert.assertEquals(15L, this.statistics.getMeanSessionLifetime().toMinutes());
        this.statistics.reset();
        Assert.assertEquals(0L, this.statistics.getExpiredSessionCount());
        Assert.assertEquals(0L, this.statistics.getMaxSessionLifetime().toMinutes());
        Assert.assertEquals(0L, this.statistics.getMeanSessionLifetime().toMinutes());
    }
}

