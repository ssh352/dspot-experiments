/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.module.mockmvc;


import io.restassured.module.mockmvc.http.GreetingController;
import io.restassured.module.mockmvc.http.PostController;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MockMvcRequestMethodTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void request_method_accepts_enum_verb() {
        RestAssuredMockMvc.given().standaloneSetup(new PostController()).param("name", "Johan").when().request(POST, "/greetingPost").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan!"));
    }

    @Test
    public void request_method_accepts_enum_verb_and_unnamed_path_params() {
        RestAssuredMockMvc.given().standaloneSetup(new GreetingController()).queryParam("name", "John").when().request(GET, "/{x}", "greeting").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, John!"));
    }

    @Test
    public void request_method_accepts_string_verb() {
        RestAssuredMockMvc.given().standaloneSetup(new PostController()).param("name", "Johan").when().request("post", "/greetingPost").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, Johan!"));
    }

    @Test
    public void request_method_accepts_string_verb_and_unnamed_path_params() {
        RestAssuredMockMvc.given().standaloneSetup(new GreetingController()).queryParam("name", "John").when().request("GEt", "/{x}", "greeting").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, John!"));
    }

    @Test
    public void static_request_method_accepts_string_verb() {
        RestAssuredMockMvc.standaloneSetup(new GreetingController());
        try {
            RestAssuredMockMvc.request("  gEt ", "/greeting").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, World!"));
        } finally {
            RestAssuredMockMvc.reset();
        }
    }

    @Test
    public void static_request_method_accepts_enum_verb_and_path_params() {
        RestAssuredMockMvc.standaloneSetup(new GreetingController());
        try {
            RestAssuredMockMvc.request(GET, "/{greeting}", "greeting").then().body("id", Matchers.equalTo(1)).body("content", Matchers.equalTo("Hello, World!"));
        } finally {
            RestAssuredMockMvc.reset();
        }
    }

    @Test
    public void throws_iae_when_http_verb_is_not_supported_by_mock_mvc() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("HTTP method 'connect' is not supported by MockMvc");
        RestAssuredMockMvc.given().standaloneSetup(new GreetingController()).request("connect", "/greeting");
    }
}
