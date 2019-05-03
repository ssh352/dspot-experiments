package org.jsoup.parser;


import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.BooleanAttribute;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;


public class AmplAttributeParseTest {
    @Test(timeout = 10000)
    public void parsesBooleanAttributes_add157() throws Exception {
        String html = "<a normal=\"123\" boolean empty=\"\"></a>";
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Element el = Jsoup.parse(html).select("a").first();
        String o_parsesBooleanAttributes_add157__6 = el.attr("normal");
        Assert.assertEquals("123", o_parsesBooleanAttributes_add157__6);
        String o_parsesBooleanAttributes_add157__7 = el.attr("boolean");
        Assert.assertEquals("", o_parsesBooleanAttributes_add157__7);
        String o_parsesBooleanAttributes_add157__8 = el.attr("empty");
        Assert.assertEquals("", o_parsesBooleanAttributes_add157__8);
        List<Attribute> attributes = el.attributes().asList();
        int o_parsesBooleanAttributes_add157__12 = attributes.size();
        Assert.assertEquals(3, ((int) (o_parsesBooleanAttributes_add157__12)));
        boolean boolean_203 = (attributes.get(0)) instanceof BooleanAttribute;
        Attribute o_parsesBooleanAttributes_add157__15 = attributes.get(1);
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add157__15)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add157__15)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add157__15)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add157__15)).getKey());
        boolean boolean_204 = (attributes.get(1)) instanceof BooleanAttribute;
        boolean boolean_205 = (attributes.get(2)) instanceof BooleanAttribute;
        String o_parsesBooleanAttributes_add157__20 = el.outerHtml();
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", o_parsesBooleanAttributes_add157__20);
        Assert.assertEquals("<a normal=\"123\" boolean empty=\"\"></a>", html);
        Assert.assertEquals("123", o_parsesBooleanAttributes_add157__6);
        Assert.assertEquals("", o_parsesBooleanAttributes_add157__7);
        Assert.assertEquals("", o_parsesBooleanAttributes_add157__8);
        Assert.assertEquals(3, ((int) (o_parsesBooleanAttributes_add157__12)));
        Assert.assertEquals("boolean=\"\"", ((BooleanAttribute) (o_parsesBooleanAttributes_add157__15)).toString());
        Assert.assertEquals(2006063320, ((int) (((BooleanAttribute) (o_parsesBooleanAttributes_add157__15)).hashCode())));
        Assert.assertNull(((BooleanAttribute) (o_parsesBooleanAttributes_add157__15)).getValue());
        Assert.assertEquals("boolean", ((BooleanAttribute) (o_parsesBooleanAttributes_add157__15)).getKey());
    }
}
