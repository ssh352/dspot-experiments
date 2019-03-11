/**
 * -
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */
package org.rapidoid.http;


import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;


@Authors("Nikolche Mihajlovski")
@Since("5.4.0")
public class HttpChunkedResponseTest extends IsolatedIntegrationTest {
    private static final int REQUESTS = Msc.normalOrHeavy(100, 10000);

    @Test
    public void testChunkedEncoding() {
        On.req(( req, resp) -> {
            resp.chunk("ab".getBytes());
            resp.chunk("c".getBytes());
            resp.chunk("d".getBytes());
            return resp;
        });
        getReq("/");
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            Self.get("/").expect("abcd").execute();
            Self.get("/").expect("abcd").benchmark(1, 100, HttpChunkedResponseTest.REQUESTS);
            Self.post("/").expect("abcd").benchmark(1, 100, HttpChunkedResponseTest.REQUESTS);
        });
    }

    @Test
    public void testChunkedEncodingAsync() {
        On.req(( req, resp) -> {
            U.must((!(req.isAsync())));
            req.async();
            U.must(req.isAsync());
            async(() -> {
                resp.chunk("ab".getBytes());
                async(() -> {
                    resp.chunk("c".getBytes());
                    async(() -> {
                        resp.chunk("d".getBytes());
                        req.done();
                    });
                });
            });
            return req;
        });
        getReq("/");
        Assertions.assertTimeout(Duration.ofSeconds(20), () -> {
            Self.get("/").expect("abcd").execute();
            Self.get("/").expect("abcd").benchmark(1, 100, HttpChunkedResponseTest.REQUESTS);
            Self.post("/").expect("abcd").benchmark(1, 100, HttpChunkedResponseTest.REQUESTS);
        });
    }
}

