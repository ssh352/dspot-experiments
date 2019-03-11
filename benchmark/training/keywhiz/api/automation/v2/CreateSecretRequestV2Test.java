/**
 * Copyright (C) 2015 Square, Inc.
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
package keywhiz.api.automation.v2;


import com.google.common.collect.ImmutableMap;
import org.junit.Test;


public class CreateSecretRequestV2Test {
    @Test
    public void deserializesCorrectly() throws Exception {
        CreateSecretRequestV2 createSecretRequest = CreateSecretRequestV2.builder().name("secret-name").content("YXNkZGFz").description("secret-description").metadata(ImmutableMap.of("owner", "root")).expiry(0).type("text/plain").groups("secret-group1", "secret-group2").build();
        assertThat(fromJson(jsonFixture("fixtures/v2/createSecretRequest.json"), CreateSecretRequestV2.class)).isEqualTo(createSecretRequest);
    }

    @Test(expected = IllegalStateException.class)
    public void emptyNameFailsValidation() throws Exception {
        CreateSecretRequestV2.builder().name("").content("").build();
    }
}

