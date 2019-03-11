/**
 * (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.security.decorators;


import org.geoserver.security.WrapperPolicy;
import org.geoserver.security.impl.AbstractAuthorizationTest;
import org.junit.Assert;
import org.junit.Test;


public class ReadSecuredCatalogDecoratorsTest extends AbstractAuthorizationTest {
    @Test
    public void testSecuredLayerInfoFeatures() {
        SecuredLayerInfo ro = new SecuredLayerInfo(statesLayer, WrapperPolicy.hide(null));
        Assert.assertFalse(((statesLayer.getResource()) instanceof SecuredFeatureTypeInfo));
        Assert.assertTrue(((ro.getResource()) instanceof SecuredFeatureTypeInfo));
        Assert.assertSame(ro.policy, ((SecuredFeatureTypeInfo) (ro.getResource())).policy);
    }

    @Test
    public void testSecuredLayerInfoCoverages() {
        SecuredLayerInfo ro = new SecuredLayerInfo(arcGridLayer, WrapperPolicy.hide(null));
        Assert.assertFalse(((arcGridLayer.getResource()) instanceof SecuredCoverageInfo));
        Assert.assertTrue(((ro.getResource()) instanceof SecuredCoverageInfo));
        Assert.assertSame(ro.policy, ((SecuredCoverageInfo) (ro.getResource())).policy);
    }

    @Test
    public void testSecuredFeatureTypeInfoHide() throws Exception {
        SecuredFeatureTypeInfo ro = new SecuredFeatureTypeInfo(states, WrapperPolicy.hide(null));
        SecuredFeatureSource fs = ((SecuredFeatureSource) (ro.getFeatureSource(null, null)));
        Assert.assertEquals(SecuredFeatureSource.class, fs.getClass());
        Assert.assertTrue(fs.policy.isHide());
        SecuredDataStoreInfo store = ((SecuredDataStoreInfo) (ro.getStore()));
        Assert.assertTrue(((SecuredDataStoreInfo) (store)).policy.isHide());
    }

    @Test
    public void testSecuredFeatureTypeInfoMetadata() throws Exception {
        SecuredFeatureTypeInfo ro = new SecuredFeatureTypeInfo(states, WrapperPolicy.metadata(null));
        try {
            ro.getFeatureSource(null, null);
            Assert.fail("This should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
        SecuredDataStoreInfo store = ((SecuredDataStoreInfo) (ro.getStore()));
        Assert.assertTrue(((SecuredDataStoreInfo) (store)).policy.isMetadata());
    }

    @Test
    public void testSecuredTypeInfoReadOnly() throws Exception {
        SecuredFeatureTypeInfo ro = new SecuredFeatureTypeInfo(states, WrapperPolicy.readOnlyChallenge(null));
        SecuredFeatureStore fs = ((SecuredFeatureStore) (ro.getFeatureSource(null, null)));
        Assert.assertTrue(fs.policy.isReadOnlyChallenge());
        SecuredDataStoreInfo store = ((SecuredDataStoreInfo) (ro.getStore()));
        Assert.assertTrue(((SecuredDataStoreInfo) (store)).policy.isReadOnlyChallenge());
    }

    @Test
    public void testSecuredDataStoreInfoHide() throws Exception {
        SecuredDataStoreInfo ro = new SecuredDataStoreInfo(statesStore, WrapperPolicy.hide(null));
        ReadOnlyDataStore dataStore = ((ReadOnlyDataStore) (ro.getDataStore(null)));
        Assert.assertTrue(dataStore.policy.isHide());
    }

    @Test
    public void testSecuredDataStoreInfoMetadata() throws Exception {
        SecuredDataStoreInfo ro = new SecuredDataStoreInfo(statesStore, WrapperPolicy.metadata(null));
        try {
            ReadOnlyDataStore dataStore = ((ReadOnlyDataStore) (ro.getDataStore(null)));
            Assert.fail("This should have failed with a security exception");
        } catch (Exception e) {
            if ((ReadOnlyDataStoreTest.isSpringSecurityException(e)) == false)
                Assert.fail("Should have failed with a security exception");

        }
    }
}

