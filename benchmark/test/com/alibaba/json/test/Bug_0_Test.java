/**
 * Copyright 1999-2017 Alibaba Group.
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
package com.alibaba.json.test;


import junit.framework.TestCase;


public class Bug_0_Test extends TestCase {
    private String text;

    private int COUNT = 1000;

    public void test_0() throws Exception {
        for (int i = 0; i < 50; ++i) {
            // f_ali_json();
            f_jackson();
        }
    }
}

