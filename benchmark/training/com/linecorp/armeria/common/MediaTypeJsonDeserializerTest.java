/**
 * Copyright 2017 LINE Corporation
 *
 *  LINE Corporation licenses this file to you under the Apache License,
 *  version 2.0 (the "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at:
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */
package com.linecorp.armeria.common;


import MediaType.PLAIN_TEXT_UTF_8;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;


public class MediaTypeJsonDeserializerTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void simple() throws Exception {
        assertThat(MediaTypeJsonDeserializerTest.mapper.readValue("\"text/plain; charset=utf-8\"", MediaType.class)).isEqualTo(PLAIN_TEXT_UTF_8);
    }

    @Test
    public void malformed() throws Exception {
        assertThatThrownBy(() -> mapper.readValue("\"text_plain\"", .class)).isInstanceOf(JsonMappingException.class);
    }

    @Test
    public void nonTextual() throws Exception {
        assertThatThrownBy(() -> mapper.readValue("42", .class)).isInstanceOf(JsonMappingException.class);
    }
}

