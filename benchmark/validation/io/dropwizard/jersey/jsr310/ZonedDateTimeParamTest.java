package io.dropwizard.jersey.jsr310;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;


public class ZonedDateTimeParamTest {
    @Test
    public void parsesDateTimes() throws Exception {
        final ZonedDateTimeParam param = new ZonedDateTimeParam("2012-11-19T13:37+01:00[Europe/Berlin]");
        assertThat(param.get()).isEqualTo(ZonedDateTime.of(2012, 11, 19, 13, 37, 0, 0, ZoneId.of("Europe/Berlin")));
    }
}
