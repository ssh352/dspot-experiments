package com.baeldung.time;


import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ LocalDateTime.class })
public class LocalDateTimeUnitTest {
    @Test
    public void givenLocalDateTimeMock_whenNow_thenGetFixedLocalDateTime() {
        Clock clock = Clock.fixed(Instant.parse("2014-12-22T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDateTime dateTime = LocalDateTime.now(clock);
        mockStatic(LocalDateTime.class);
        Mockito.when(LocalDateTime.now()).thenReturn(dateTime);
        String dateTimeExpected = "2014-12-22T10:15:30";
        LocalDateTime now = LocalDateTime.now();
        assertThat(now).isEqualTo(dateTimeExpected);
    }

    @Test
    public void givenFixedClock_whenNow_thenGetFixedLocalDateTime() {
        Clock clock = Clock.fixed(Instant.parse("2014-12-22T10:15:30.00Z"), ZoneId.of("UTC"));
        String dateTimeExpected = "2014-12-22T10:15:30";
        LocalDateTime dateTime = LocalDateTime.now(clock);
        assertThat(dateTime).isEqualTo(dateTimeExpected);
    }
}

