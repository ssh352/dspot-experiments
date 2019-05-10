package com.github.mustachejava;


import com.github.mustachejava.codes.DefaultMustache;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;


public class AmplDotNotationTest {
    private static final String EARLY_MISS_TEMPLATE = "{{container1.container2.target}}";

    private static final String LAST_ELEMENT_MISS_TEMPLATE = "{{container1.nothing}}";

    private static final class ModelObject {
        @SuppressWarnings("unused")
        public Object getContainer2() {
            return null;
        }
    }

    private MustacheFactory factory;

    private Map<String, Object> mapModel;

    private Map<String, Object> objectModel;

    @Before
    public void setUp() {
        factory = new DefaultMustacheFactory();
        mapModel = new HashMap<>();
        Map<String, Object> container1 = new HashMap<>();
        mapModel.put("container1", container1);
        objectModel = new HashMap<>();
        objectModel.put("container1", new AmplDotNotationTest.ModelObject());
    }

    private void testMiss(Object model, String template) {
        Mustache mustache = compile(template);
        StringWriter writer = new StringWriter();
        mustache.execute(writer, model);
        Assert.assertEquals("", writer.toString());
    }

    private Mustache compile(String template) {
        Reader reader = new StringReader(template);
        return factory.compile(reader, "template");
    }

    public void testReadmeSerial_add12536_remove13484_add16638() throws MustacheException, IOException {
        MustacheFactory c = createMustacheFactory();
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        Mustache m = c.compile("items2.html");
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        StringWriter sw = new StringWriter();
        long start = System.currentTimeMillis();
        Writer o_testReadmeSerial_add12536__9 = m.execute(sw, new AmplInterpreterTest.Context());
        long diff = (System.currentTimeMillis()) - start;
        sw.toString();
        String o_testReadmeSerial_add12536_remove13484_add16638__16 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12536_remove13484_add16638__16);
        String o_testReadmeSerial_add12536__14 = TestUtil.getContents(this.root, "items.txt");
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12536__14);
        String String_30 = "Should be a little bit more than 4 seconds: " + diff;
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_30);
        boolean boolean_31 = (diff > 3999) && (diff < 6000);
        TestCase.assertNull(((DefaultMustacheFactory) (c)).getExecutorService());
        TestCase.assertEquals(100, ((int) (((DefaultMustacheFactory) (c)).getRecursionLimit())));
        TestCase.assertFalse(((DefaultMustache) (m)).isRecursive());
        TestCase.assertEquals("items2.html", ((DefaultMustache) (m)).getName());
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12536_remove13484_add16638__16);
        TestCase.assertEquals("Name: Item 1\nPrice: $19.99\n  Feature: New!\n  Feature: Awesome!\nName: Item 2\nPrice: $29.99\n  Feature: Old.\n  Feature: Ugly.\n", o_testReadmeSerial_add12536__14);
        TestCase.assertEquals("Should be a little bit more than 4 seconds: 4001", String_30);
    }
}
