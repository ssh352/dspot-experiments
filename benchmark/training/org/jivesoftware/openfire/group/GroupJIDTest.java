package org.jivesoftware.openfire.group;


import org.jivesoftware.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xmpp.packet.JID;


public class GroupJIDTest {
    @Test
    public void testBase32Encoding() {
        String testGroupName = "Test Group (1)";
        String testDomainName = "localhost";
        String testBase32GroupName = StringUtils.encodeBase32(testGroupName);
        // no need for JID escaping
        JID testJid = new JID(testBase32GroupName, testDomainName, null);
        Assert.assertEquals(testBase32GroupName, testJid.getNode());
        String testDecodedGroupName = new String(StringUtils.decodeBase32(testJid.getNode()));
        Assert.assertEquals(testGroupName, testDecodedGroupName);
    }

    @Test
    public void testBase32Alphabet() {
        String testABC = "ABC";
        Assert.assertTrue(StringUtils.isBase32(testABC));
        String test123 = "123";
        Assert.assertTrue(StringUtils.isBase32(test123));
        // should be case insensitve
        String testabc = "abc";
        Assert.assertTrue(StringUtils.isBase32(testabc));
        String testXYZ = "XYZ";
        Assert.assertFalse(StringUtils.isBase32(testXYZ));
    }

    @Test
    public void testParseGroupJIDFromString() {
        String testGroupName = "Test Group (2);  - now with *special* =characters= too!";
        JID testJid = new JID(StringUtils.encodeBase32(testGroupName), "localhost", StringUtils.hash(testGroupName));
        Assert.assertTrue(GroupJID.isGroup(testJid));
        Assert.assertEquals(testGroupName, getGroupName());
    }
}

