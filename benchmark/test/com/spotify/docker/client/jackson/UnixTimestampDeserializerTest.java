/**
 * -
 * -\-\-
 * docker-client
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.docker.client.jackson;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.spotify.docker.client.ObjectMapperProvider;
import java.util.Date;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;


public class UnixTimestampDeserializerTest {
    private final DateTime referenceDateTime = new DateTime(2013, 7, 17, 9, 32, 4, DateTimeZone.UTC);

    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperProvider.objectMapper();

    private static class TestClass {
        @JsonProperty("date")
        @JsonDeserialize(using = UnixTimestampDeserializer.class)
        private Date date;

        private Date getDate() {
            return date;
        }
    }

    @Test
    public void testFromString() throws Exception {
        final String json = toJson("{\"date\": \"%s\"}");
        final UnixTimestampDeserializerTest.TestClass value = UnixTimestampDeserializerTest.OBJECT_MAPPER.readValue(json, UnixTimestampDeserializerTest.TestClass.class);
        Assert.assertThat(value.getDate(), Matchers.equalTo(referenceDateTime.toDate()));
    }

    @Test
    public void testFromNumber() throws Exception {
        final String json = toJson("{\"date\": %s}");
        final UnixTimestampDeserializerTest.TestClass value = UnixTimestampDeserializerTest.OBJECT_MAPPER.readValue(json, UnixTimestampDeserializerTest.TestClass.class);
        Assert.assertThat(value.getDate(), Matchers.equalTo(referenceDateTime.toDate()));
    }
}
