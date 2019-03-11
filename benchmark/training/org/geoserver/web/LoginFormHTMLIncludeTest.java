/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web;


import LoginFormHTMLInclude.GEOSERVER_LOGIN_AUTOCOMPLETE;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the default LoginFormHTMLInclude
 */
public class LoginFormHTMLIncludeTest extends GeoServerWicketTestSupport {
    @Test
    public void testImportAsStringDefault() throws Exception {
        LoginFormHTMLInclude defaultInclude = new LoginFormHTMLInclude("login.form", new PackageResourceReference(LoginFormInfo.class, "include_login_form.html"));
        String defaultString = defaultInclude.importAsString();
        Assert.assertTrue("Default include should contains autocomplete=\"on\"", defaultString.contains("autocomplete=\"on\""));
    }

    @Test
    public void testImportAsStringAutocompleteOff() throws Exception {
        String existingAutocompleteSetting = System.getProperty(GEOSERVER_LOGIN_AUTOCOMPLETE);
        System.setProperty(GEOSERVER_LOGIN_AUTOCOMPLETE, "off");
        LoginFormHTMLInclude defaultInclude = new LoginFormHTMLInclude("login.form", new PackageResourceReference(LoginFormInfo.class, "include_login_form.html"));
        String defaultString = defaultInclude.importAsString();
        Assert.assertTrue("Default include should contains autocomplete=\"off\"", defaultString.contains("autocomplete=\"off\""));
        if (existingAutocompleteSetting != null) {
            System.setProperty(GEOSERVER_LOGIN_AUTOCOMPLETE, existingAutocompleteSetting);
        }
    }
}

