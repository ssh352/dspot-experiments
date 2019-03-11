/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.security.providers.httpauth;


import java.util.Random;
import javax.crypto.Cipher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit test for {@link HttpAuthUtil}.
 */
public class HttpAuthUtilTest {
    @Test
    public void positiveTestLong() throws Exception {
        testToAndBack(45545554L);
        testToAndBack(Long.MAX_VALUE);
        testToAndBack(Long.MIN_VALUE);
    }

    @Test
    public void negativeTetLong() {
        byte[] bytes = HttpAuthUtil.toBytes(455687);
        try {
            HttpAuthUtil.toLong(bytes, 0, 1);
        } catch (IllegalArgumentException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("Wrong length"));
        }
        try {
            HttpAuthUtil.toLong(bytes, 4, bytes.length);
        } catch (IllegalArgumentException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("capacity of the array"));
        }
    }

    @Test
    public void cipher() throws Exception {
        byte[] salt = new byte[16];
        Random r = new Random();
        r.nextBytes(salt);
        Cipher cipher = HttpAuthUtil.cipher("pwd".toCharArray(), salt, Cipher.ENCRYPT_MODE);
        MatcherAssert.assertThat(cipher, CoreMatchers.notNullValue());
    }

    @Test
    public void cipherWrongSalt() throws Exception {
        byte[] salt = new byte[4];
        Random r = new Random();
        r.nextBytes(salt);
        Assertions.assertThrows(HttpAuthException.class, () -> HttpAuthUtil.cipher("pwd".toCharArray(), salt, Cipher.ENCRYPT_MODE));
    }
}

