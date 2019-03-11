package org.robolectric;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.io.StringReader;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


@RunWith(AndroidJUnit4.class)
public class IncludedDependenciesTest {
    @Test
    public void jsonShouldWork() throws Exception {
        Assert.assertEquals("value", new JSONObject("{'name':'value'}").getString("name"));
    }

    @Test
    public void xppShouldWork() throws Exception {
        XmlPullParser xmlPullParser = XmlPullParserFactory.newInstance().newPullParser();
        xmlPullParser.setInput(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?><test name=\"value\"/>"));
        Assert.assertEquals(XmlPullParser.START_TAG, xmlPullParser.nextTag());
        Assert.assertEquals(1, xmlPullParser.getAttributeCount());
        Assert.assertEquals("name", xmlPullParser.getAttributeName(0));
        Assert.assertEquals("value", xmlPullParser.getAttributeValue(0));
    }
}

