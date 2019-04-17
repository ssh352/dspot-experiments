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
package org.apache.camel.component.schematron.processor;


import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.apache.camel.component.schematron.util.Utils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SchematronEngine Unit Test.
 */
public class SchematronProcessorTest {
    private Logger logger = LoggerFactory.getLogger(SchematronProcessorTest.class);

    @Test
    public void testValidXML() throws Exception {
        String payload = IOUtils.toString(ClassLoader.getSystemResourceAsStream("xml/article-1.xml"));
        logger.info("Validating payload: {}", payload);
        // validate
        String result = getProcessor("sch/schematron-1.sch", null).validate(payload);
        logger.info("Schematron Report: {}", result);
        Assert.assertEquals(0, Integer.valueOf(Utils.evaluate("count(//svrl:failed-assert)", result)).intValue());
        Assert.assertEquals(0, Integer.valueOf(Utils.evaluate("count(//svrl:successful-report)", result)).intValue());
    }

    @Test
    public void testInvalidXMLWithClientResolver() throws Exception {
        String payload = IOUtils.toString(ClassLoader.getSystemResourceAsStream("xml/article-3.xml"));
        logger.info("Validating payload: {}", payload);
        // validate
        String result = getProcessor("sch/schematron-3.sch", new SchematronProcessorTest.ClientUriResolver()).validate(payload);
        logger.info("Schematron Report: {}", result);
        Assert.assertEquals("A title should be at least two characters", Utils.evaluate("//svrl:failed-assert/svrl:text", result));
        Assert.assertEquals(0, Integer.valueOf(Utils.evaluate("count(//svrl:successful-report)", result)).intValue());
    }

    @Test
    public void testInValidXML() throws Exception {
        String payload = IOUtils.toString(ClassLoader.getSystemResourceAsStream("xml/article-2.xml"));
        logger.info("Validating payload: {}", payload);
        // validate
        String result = getProcessor("sch/schematron-2.sch", null).validate(payload);
        logger.info("Schematron Report: {}", result);
        // should throw two assertions because of the missing chapters in the XML.
        Assert.assertEquals("A chapter should have a title", Utils.evaluate("//svrl:failed-assert/svrl:text", result));
        Assert.assertEquals("'chapter' element has more than one title present", Utils.evaluate("//svrl:successful-report/svrl:text", result).trim());
    }

    class ClientUriResolver implements URIResolver {
        @Override
        public Source resolve(String href, String base) throws TransformerException {
            return new StreamSource(SchematronProcessorTest.ClientUriResolver.class.getClassLoader().getResourceAsStream("custom-resolver/".concat(href)));
        }
    }
}
