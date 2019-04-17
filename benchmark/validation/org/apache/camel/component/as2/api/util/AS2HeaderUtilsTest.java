/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.as2.api.util;


import org.apache.camel.component.as2.api.util.AS2HeaderUtils.Parameter;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;
import org.junit.Assert;
import org.junit.Test;


public class AS2HeaderUtilsTest {
    private static final String TEST_NAME_VALUES = " signed-receipt-protocol   =   optional  , pkcs7-signature  ;    signed-receipt-micalg   =    required  ,  sha1  ";

    private static final String SIGNED_RECEIPT_PROTOCOL_ATTRIBUTE = "signed-receipt-protocol";

    private static final String SIGNED_RECEIPT_PROTOCOL_IMPORTANCE = "optional";

    private static final String[] SIGNED_RECEIPT_PROTOCOL_VALUES = new String[]{ "pkcs7-signature" };

    private static final String SIGNED_RECEIPT_MICALG_ATTRIBUTE = "signed-receipt-micalg";

    private static final String SIGNED_RECEIPT_MICALG_IMPORTANCE = "required";

    private static final String[] SIGNED_RECEIPT_MICALG_VALUES = new String[]{ "sha1" };

    @Test
    public void parseNameValuePairTest() {
        final CharArrayBuffer buffer = new CharArrayBuffer(AS2HeaderUtilsTest.TEST_NAME_VALUES.length());
        buffer.append(AS2HeaderUtilsTest.TEST_NAME_VALUES);
        final ParserCursor cursor = new ParserCursor(0, AS2HeaderUtilsTest.TEST_NAME_VALUES.length());
        Parameter parameter = AS2HeaderUtils.parseParameter(buffer, cursor);
        Assert.assertEquals("Unexpected value for parameter attribute", AS2HeaderUtilsTest.SIGNED_RECEIPT_PROTOCOL_ATTRIBUTE, parameter.getAttribute());
        Assert.assertEquals("Unexpected value for parameter importance", AS2HeaderUtilsTest.SIGNED_RECEIPT_PROTOCOL_IMPORTANCE, parameter.getImportance().getImportance());
        Assert.assertArrayEquals("Unexpected value for parameter values", AS2HeaderUtilsTest.SIGNED_RECEIPT_PROTOCOL_VALUES, parameter.getValues());
        parameter = AS2HeaderUtils.parseParameter(buffer, cursor);
        Assert.assertEquals("Unexpected value for parameter attribute", AS2HeaderUtilsTest.SIGNED_RECEIPT_MICALG_ATTRIBUTE, parameter.getAttribute());
        Assert.assertEquals("Unexpected value for parameter importance", AS2HeaderUtilsTest.SIGNED_RECEIPT_MICALG_IMPORTANCE, parameter.getImportance().getImportance());
        Assert.assertArrayEquals("Unexpected value for parameter values", AS2HeaderUtilsTest.SIGNED_RECEIPT_MICALG_VALUES, parameter.getValues());
    }
}
