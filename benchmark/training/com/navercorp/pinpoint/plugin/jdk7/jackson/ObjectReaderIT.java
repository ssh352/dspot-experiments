/**
 * Copyright 2019 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.jdk7.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.navercorp.pinpoint.bootstrap.plugin.test.Expectations;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.plugin.AgentPath;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointPluginTestSuite;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @see JacksonPlugin#intercept_ObjectMapper(com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext)
 * @author Sungkook Kim
 */
// 2.7.0, 2.7.1 has JDK6 compatibility issue - https://github.com/FasterXML/jackson-databind/issues/1134
@RunWith(PinpointPluginTestSuite.class)
@PinpointAgent(AgentPath.PATH)
@Dependency({ "com.fasterxml.jackson.core:jackson-databind:[2.8.0,)" })
public class ObjectReaderIT {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testWriteValue() throws Exception {
        ObjectReaderIT.__POJO pojo = new ObjectReaderIT.__POJO();
        pojo.setName("Jackson");
        ObjectWriter writer = mapper.writer();
        String jsonStr = writer.writeValueAsString(pojo);
        byte[] jsonByte = writer.writeValueAsBytes(pojo);
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        Method writeval1 = ObjectWriter.class.getMethod("writeValueAsString", Object.class);
        Method writeval2 = ObjectWriter.class.getMethod("writeValueAsBytes", Object.class);
        verifier.verifyTrace(Expectations.event("JACKSON", writeval1, Expectations.annotation("jackson.json.length", jsonStr.length())));
        verifier.verifyTrace(Expectations.event("JACKSON", writeval2, Expectations.annotation("jackson.json.length", jsonByte.length)));
        verifier.verifyTraceCount(0);
    }

    @Test
    public void testReadValue() throws Exception {
        String json_str = "{\"name\" : \"Jackson\"}";
        byte[] json_b = json_str.getBytes(ObjectReaderIT.UTF_8);
        ObjectReader reader = mapper.reader(ObjectReaderIT.__POJO.class);
        ObjectReaderIT.__POJO pojo = reader.readValue(json_str);
        pojo = reader.readValue(json_b);
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        Method readval1 = ObjectReader.class.getMethod("readValue", String.class);
        Method readval2 = ObjectReader.class.getMethod("readValue", byte[].class);
        verifier.verifyTrace(Expectations.event("JACKSON", readval1, Expectations.annotation("jackson.json.length", json_str.length())));
        verifier.verifyTrace(Expectations.event("JACKSON", readval2, Expectations.annotation("jackson.json.length", json_b.length)));
        verifier.verifyTraceCount(0);
    }

    private static class __POJO {
        public String name;

        public String getName() {
            return name;
        }

        public void setName(String str) {
            name = str;
        }
    }
}
