/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.graylog2.shared.bindings.providers.ObjectMapperProvider;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;


public class MongoJodaDateTimeDeserializerTest {
    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Test
    public void deserializeDateTime() throws Exception {
        final String json = "{\"date_time\":\"2016-12-13T16:00:00.000+0200\"}";
        final MongoJodaDateTimeDeserializerTest.TestBean value = objectMapper.readValue(json, MongoJodaDateTimeDeserializerTest.TestBean.class);
        assertThat(value.dateTime).isEqualTo(new DateTime(2016, 12, 13, 14, 0, DateTimeZone.UTC));
    }

    @Test
    public void deserializeNull() throws Exception {
        final String json = "{\"date_time\":null}";
        final MongoJodaDateTimeDeserializerTest.TestBean value = objectMapper.readValue(json, MongoJodaDateTimeDeserializerTest.TestBean.class);
        assertThat(value.dateTime).isNull();
    }

    static class TestBean {
        @JsonDeserialize(using = MongoJodaDateTimeDeserializer.class)
        DateTime dateTime;
    }
}

