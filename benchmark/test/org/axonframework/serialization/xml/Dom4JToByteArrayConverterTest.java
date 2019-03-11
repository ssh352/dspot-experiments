/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.serialization.xml;


import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class Dom4JToByteArrayConverterTest {
    private Dom4JToByteArrayConverter testSubject;

    @Test
    public void testCanConvert() {
        Assert.assertEquals(Document.class, testSubject.expectedSourceType());
        Assert.assertEquals(byte[].class, testSubject.targetType());
    }

    @Test
    public void testConvert() {
        DocumentFactory df = DocumentFactory.getInstance();
        Document doc = df.createDocument("UTF-8");
        doc.setRootElement(df.createElement("rootElement"));
        byte[] actual = testSubject.convert(doc);
        Assert.assertNotNull(actual);
        String actualString = new String(actual);
        Assert.assertTrue(("Wrong output: " + actualString), actualString.contains("rootElement"));
    }
}

