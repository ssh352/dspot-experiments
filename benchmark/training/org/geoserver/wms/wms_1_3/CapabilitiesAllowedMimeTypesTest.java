/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2014 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.wms_1_3;


import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.geoserver.wms.GetMapOutputFormat;
import org.geoserver.wms.WMSTestSupport;
import org.geoserver.wms.featureinfo.GetFeatureInfoOutputFormat;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


public class CapabilitiesAllowedMimeTypesTest extends WMSTestSupport {
    GetMapOutputFormat getMapFormat;

    GetFeatureInfoOutputFormat getInfoFormat;

    @Test
    public void testAllowedMimeTypes() throws Exception {
        // check with no restrictions
        Document doc = getAsDOM("sf/PrimitiveGeoFeature/wms?service=WMS&request=getCapabilities&version=1.3.0", true);
        XpathEngine xpath = XMLUnit.newXpathEngine();
        NodeList formatNodes = xpath.getMatchingNodes("wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetMap/wms:Format", doc);
        Assert.assertTrue(((formatNodes.getLength()) > 1));
        formatNodes = xpath.getMatchingNodes("/wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetFeatureInfo/wms:Format", doc);
        Assert.assertTrue(((formatNodes.getLength()) > 1));
        // add mime type restrictions
        addMimeTypes();
        doc = getAsDOM("sf/PrimitiveGeoFeature/wms?service=WMS&request=getCapabilities&version=1.3.0", true);
        formatNodes = xpath.getMatchingNodes("wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetMap/wms:Format", doc);
        Assert.assertEquals(1, formatNodes.getLength());
        Assert.assertEquals(getMapFormat.getMimeType(), formatNodes.item(0).getTextContent());
        formatNodes = xpath.getMatchingNodes("/wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetFeatureInfo/wms:Format", doc);
        Assert.assertEquals(1, formatNodes.getLength());
        Assert.assertEquals(getInfoFormat.getContentType(), formatNodes.item(0).getTextContent());
        // remove restrictions
        removeMimeTypes();
        doc = getAsDOM("sf/PrimitiveGeoFeature/wms?service=WMS&request=getCapabilities&version=1.3.0", true);
        formatNodes = xpath.getMatchingNodes("wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetMap/wms:Format", doc);
        Assert.assertTrue(((formatNodes.getLength()) > 1));
        formatNodes = xpath.getMatchingNodes("/wms:WMS_Capabilities/wms:Capability/wms:Request/wms:GetFeatureInfo/wms:Format", doc);
        Assert.assertTrue(((formatNodes.getLength()) > 1));
    }
}

