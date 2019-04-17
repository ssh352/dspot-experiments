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
package org.apache.camel.component.cxf.mtom;


import ExchangePattern.InOut;
import java.awt.image.BufferedImage;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.Endpoint;
import javax.xml.xpath.XPathConstants;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.component.cxf.CxfPayload;
import org.apache.camel.converter.jaxp.XmlConverter;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.helpers.XPathUtils;
import org.apache.cxf.staxutils.StaxUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.w3c.dom.Element;


/**
 * Unit test for exercising MTOM feature of a CxfProducer in PAYLOAD mode
 */
@ContextConfiguration
public class CxfMtomProducerPayloadModeTest extends AbstractJUnit4SpringContextTests {
    static int port = CXFTestSupport.getPort1();

    @Autowired
    protected CamelContext context;

    protected Endpoint endpoint;

    @SuppressWarnings("unchecked")
    @Test
    public void testProducer() throws Exception {
        if (MtomTestHelper.isAwtHeadless(logger, null)) {
            return;
        }
        // START SNIPPET: producer
        Exchange exchange = context.createProducerTemplate().send("direct:testEndpoint", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.setPattern(InOut);
                List<Source> elements = new ArrayList<>();
                elements.add(new DOMSource(StaxUtils.read(new StringReader(MtomTestHelper.REQ_MESSAGE)).getDocumentElement()));
                CxfPayload<SoapHeader> body = new CxfPayload(new ArrayList<SoapHeader>(), elements, null);
                exchange.getIn().setBody(body);
                exchange.getIn().addAttachment(MtomTestHelper.REQ_PHOTO_CID, new DataHandler(new ByteArrayDataSource(MtomTestHelper.REQ_PHOTO_DATA, "application/octet-stream")));
                exchange.getIn().addAttachment(MtomTestHelper.REQ_IMAGE_CID, new DataHandler(new ByteArrayDataSource(MtomTestHelper.requestJpeg, "image/jpeg")));
            }
        });
        // process response
        CxfPayload<SoapHeader> out = exchange.getOut().getBody(CxfPayload.class);
        Assert.assertEquals(1, out.getBody().size());
        Map<String, String> ns = new HashMap<>();
        ns.put("ns", MtomTestHelper.SERVICE_TYPES_NS);
        ns.put("xop", MtomTestHelper.XOP_NS);
        XPathUtils xu = new XPathUtils(ns);
        Element oute = new XmlConverter().toDOMElement(out.getBody().get(0));
        Element ele = ((Element) (xu.getValue("//ns:DetailResponse/ns:photo/xop:Include", oute, XPathConstants.NODE)));
        String photoId = ele.getAttribute("href").substring(4);// skip "cid:"

        ele = ((Element) (xu.getValue("//ns:DetailResponse/ns:image/xop:Include", oute, XPathConstants.NODE)));
        String imageId = ele.getAttribute("href").substring(4);// skip "cid:"

        DataHandler dr = exchange.getOut().getAttachment(decodingReference(photoId));
        Assert.assertEquals("application/octet-stream", dr.getContentType());
        MtomTestHelper.assertEquals(MtomTestHelper.RESP_PHOTO_DATA, IOUtils.readBytesFromStream(dr.getInputStream()));
        dr = exchange.getOut().getAttachment(decodingReference(imageId));
        Assert.assertEquals("image/jpeg", dr.getContentType());
        BufferedImage image = ImageIO.read(dr.getInputStream());
        Assert.assertEquals(560, image.getWidth());
        Assert.assertEquals(300, image.getHeight());
        // END SNIPPET: producer
    }
}
