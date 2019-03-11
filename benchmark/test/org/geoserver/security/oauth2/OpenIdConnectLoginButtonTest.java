/**
 * (c) 2018 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.oauth2;


import java.util.logging.Level;
import org.geoserver.web.GeoServerHomePage;
import org.geoserver.web.GeoServerWicketTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class OpenIdConnectLoginButtonTest extends GeoServerWicketTestSupport {
    @Test
    public void testLoginButton() {
        tester.startPage(GeoServerHomePage.class);
        String html = tester.getLastResponseAsString();
        LOGGER.log(Level.INFO, ("Last HTML page output:\n" + html));
        // the login form is there and has the link
        Assert.assertTrue(html.contains("<form style=\"display: inline-block;\" method=\"post\" action=\"../web/j_spring_oauth2_openid_connect_login\">"));
        Assert.assertTrue(html.contains("<img src=\"./wicket/resource/org.geoserver.web.security.oauth2.OpenIdConnectAuthProviderPanel/openid"));
    }
}

